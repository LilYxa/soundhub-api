import sys

import pandas as pd
from sqlalchemy import create_engine
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.neighbors import NearestNeighbors
from uuid import UUID
import os
from sys import argv
from dotenv import dotenv_values
from logger import logger

path = os.getcwd()
res_path = os.path.abspath(os.path.join(path, os.pardir))
filepath = os.path.join(res_path, "application.properties")

config = dotenv_values(filepath)


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
    db_user = config['spring.datasource.username']
    db_password = config['spring.datasource.password']
    db_url = config['spring.datasource.url']

    # Создаем строку подключения
    clean_url = db_url[5:]
    first_slash_index = clean_url.find('/')
    db_connection_string = clean_url[:first_slash_index+2] + f"{db_user}:{db_password}@" + clean_url[first_slash_index+2:]
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
        neigh: int = int(config['python.neighbors.default'])

    try:
        friend_list = potential_friends(user_id, neigh)
        logger.info(f"main[3]: potential friends {friend_list}")

        return friend_list
    except Exception as e:
        logger.error(f"main[4]: {e}")
        return []
