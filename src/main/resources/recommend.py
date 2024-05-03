import os

import pandas as pd
from pandas import DataFrame

import psycopg2
from sqlalchemy.engine.base import Engine
from sqlalchemy import create_engine

from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.neighbors import NearestNeighbors

import uuid
from uuid import UUID

import sys
from sys import argv

from env import read_postgres_properties_from_file


def potential_friends(user_id: UUID, neigh: int) -> list:
    engine: Engine = get_db_engine()

    try:
        query = "SELECT * FROM user_favorite_genres;"

        df: DataFrame = pd.read_sql_query(query, con=engine)

        # Группируем по user_id и собираем genres_id в список
        df: DataFrame = df.groupby('user_id')['genre_id'].agg(list).reset_index()

        # Преобразуем preferredGenres в формат, пригодный для машинного обучения
        mlb = MultiLabelBinarizer()
        genres_encoded = mlb.fit_transform(df['genre_id'])

        # Создаем модель для k ближайших соседей
        knn = NearestNeighbors(n_neighbors=neigh, algorithm='auto').fit(genres_encoded)
        user_index = df.index[df['user_id'] == user_id]

        # Получаем индексы и расстояния до ближайших соседей
        distances, indices = knn.kneighbors(genres_encoded[user_index])

        # Извлекаем userId ближайших соседей
        neighbors_userIds = df.iloc[indices[0]]['user_id'].tolist()
        neighbors_userIds.remove(user_id)

        for i in neighbors_userIds:
            print(str(i))

        return neighbors_userIds
    except Exception as e:
        print(f"Произошла ошибка: {e}")
    finally:
        # Закрываем подключение
        engine.dispose()


def get_db_engine() -> Engine:
    # db_username_key = "spring.datasource.username"
    # db_password_key = "spring.datasource.password"
    # db_jdbc_url_key = "spring.datasource.url"

    # properties_filename = "application.properties"
    # properties_file_path = f"{os.getcwd()}/{properties_filename}"
    # config = read_postgres_properties_from_file(properties_file_path)

    database = 'soundtest'
    db_user = 'postgres'
    db_password = 'postgres'
    host = 'localhost'
    port = '5432'

    # db_host = config[db_jdbc_url_key].split("://")[-1]
    # db_user = config[db_username_key]
    # db_password = config[db_password_key]

    conn_string = f"postgresql://{db_user}:{db_password}@{host}:{port}/{database}"
    # conn_string = f"postgresql://{db_user}:{db_password}@{db_host}"

    return create_engine(conn_string)


try:
    user: UUID = uuid.UUID(argv[1])
except Exception as e:
    print(f"User is not defined!\n exception: {e}")
    sys.exit(-1)

try:
    neigh = int(argv[2])
except (ValueError, IndexError):
    neigh = 10

potential_friends(user, neigh)
