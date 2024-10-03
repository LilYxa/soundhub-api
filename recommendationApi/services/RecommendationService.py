import pandas as pd
from dotenv import dotenv_values
from exceptions.UserNotFoundException import UserNotFoundException
from sklearn.neighbors import NearestNeighbors
from sklearn.preprocessing import MultiLabelBinarizer
from sqlalchemy import Engine, create_engine
from typing import Self
from utils import logger
from uuid import UUID


class RecommendationService:
	instance: Self = None

	def __init__(self) -> None:
		self.__config: dict[str, str | None] = dotenv_values()
		self.__neighbour_count = int(self.__config['NEIGHBOURS_DEFAULT'])

	def __new__(cls):
		if cls.instance is None:
			cls.instance = super().__new__(cls)
		return cls.instance
		

	def __get_db_connection(self) -> Engine:
		try:
			db_user = self.__config['POSTGRES_USER']
			db_port = self.__config['POSTGRES_PORT']
			db_password = self.__config['POSTGRES_PASSWORD']
			db_url = self.__config['POSTGRES_HOST']
			db_name = self.__config['POSTGRES_DB']

			logger.info(f'engine_init[1]: {self.__config}')
			db_connection_string = f'postgresql://{db_user}:{db_password}@{db_url}:{db_port}/{db_name}'

			logger.debug(f'engine_init[2]: connection string is {db_connection_string}')
			return create_engine(db_connection_string)
		except KeyError as e:
			logger.error(f'engine_init[3]: missing environment variable {e}')
			raise ConnectionError(f'Missing environment variable(s): {e}')
		except Exception as e:
			logger.error(f'engine_init[4]: {e}')
			raise ConnectionError(f'Unable to connect to database: {e}')

	def __find_nearest_neighbours(self, df: pd.DataFrame, user_id: UUID):
		# Преобразуем preferredGenres в формат, пригодный для машинного обучения
		mlb = MultiLabelBinarizer()
		genres_encoded = mlb.fit_transform(df['genre_id'])

		# Создаем модель для k ближайших соседей
		knn = NearestNeighbors(
			n_neighbors=self.__neighbour_count, 
			algorithm='auto'
		).fit(genres_encoded)

		user_index = df.index[df['user_id'] == user_id]

		# Получаем индексы и расстояния до ближайших соседей
		distances, indices = knn.kneighbors(genres_encoded[user_index])

		# Извлекаем userId ближайших соседей
		neighbors_user_ids = df.iloc[indices[0]]['user_id'].tolist()

		if user_id in neighbors_user_ids:
			neighbors_user_ids.remove(user_id)

		return neighbors_user_ids

	def find_potential_friends(self, user_id: UUID):
		engine: Engine = None
		try:
			engine = self.__get_db_connection()
			query = "SELECT * FROM user_favorite_genres;"

			# Выполняем запрос и результат сохраняем в DataFrame
			df: pd.DataFrame = pd.read_sql_query(query, con=engine)

			if user_id not in df['user_id'].values:
				raise UserNotFoundException(user_id)

			# Группируем по user_id и собираем genres_id в список
			df = df.groupby('user_id')['genre_id'].agg(list).reset_index()
			registered_user_count: int = len(df.index)

			if self.__neighbour_count > registered_user_count:
				self.__neighbour_count = registered_user_count

			potential_friend_ids = self.__find_nearest_neighbours(df, user_id)

			logger.debug(f"potential_friends[1]: {potential_friend_ids}")
			return potential_friend_ids

		except Exception as e:
			logger.error(f"potential_friends[2]: {e}")
			raise e
		finally:
			# Закрываем подключение
			if engine:
				engine.dispose()