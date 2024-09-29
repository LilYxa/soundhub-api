from sys import argv
from uuid import UUID

import pandas as pd
from dotenv import dotenv_values
from sklearn.neighbors import NearestNeighbors
from sklearn.preprocessing import MultiLabelBinarizer
from sqlalchemy import create_engine

from logger import logger

config = dotenv_values()

def potential_friends(user_id: UUID, neigh: int = 5):
    engine = engine_init()
    try:
        query = "SELECT * FROM user_favorite_genres;"

        # Выполняем запрос и результат сохраняем в DataFrame
        df = pd.read_sql_query(query, con=engine)

        # Группируем по user_id и собираем genres_id в список
        df = df.groupby('user_id')['genre_id'].agg(list).reset_index()
        users_registered: int = len(df.index)

        if neigh > users_registered:
            neigh = users_registered

        # Преобразуем preferredGenres в формат, пригодный для машинного обучения
        mlb = MultiLabelBinarizer()
        genres_encoded = mlb.fit_transform(df['genre_id'])

        # Создаем модель для k ближайших соседей
        knn = NearestNeighbors(n_neighbors=neigh, algorithm='auto').fit(genres_encoded)
        user_index = df.index[df['user_id'] == user_id]

        # Получаем индексы и расстояния до ближайших соседей
        distances, indices = knn.kneighbors(genres_encoded[user_index])

        # Извлекаем userId ближайших соседей
        neighbors_user_ids = df.iloc[indices[0]]['user_id'].tolist()

        if user_id in neighbors_user_ids:
            neighbors_user_ids.remove(user_id)

        logger.debug(f"potential_friends[1]: {neighbors_user_ids}")

        return neighbors_user_ids
    except Exception as e:
        logger.error(f"potential_friends[2]: {e}")
        return []
    finally:
        # Закрываем подключение
        engine.dispose()


def engine_init():
    db_user = config['DATASOURCE_USERNAME']
    db_password = config['DATASOURCE_PASSWORD']
    db_url = config['DATASOURCE_URL']

    # Создаем строку подключения
    clean_url = db_url
    first_slash_index = clean_url.find('/')
    db_connection_string = clean_url[:first_slash_index + 2] + f"{db_user}:{db_password}@" + clean_url[first_slash_index + 2:]

    logger.debug(f'engine_init[2]: connection string is {db_connection_string}')
    return create_engine(db_connection_string)


def main(user_id: UUID):
    if user_id is None:
        logger.error("main[1]: User is not defined")
        raise Exception("User is not defined")

    neigh: int

    try:
        neigh = int(argv[2])
    except Exception as e:
        logger.error(f"main[2]: {e}")
        neigh: int = int(config['NEIGHBORS_DEFAULT'])

    try:
        friend_list = potential_friends(user_id, neigh)
        logger.info(f"main[3]: potential friends {friend_list}")

        return friend_list
    except Exception as e:
        logger.error(f"main[4]: {e}")
        return []