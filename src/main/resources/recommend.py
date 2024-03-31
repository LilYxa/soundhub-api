import pandas as pd
import psycopg2
from sqlalchemy import create_engine
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.neighbors import NearestNeighbors
import uuid
import sys
from sys import argv

try:
    user=uuid.UUID(argv[1])
except:
    print("User is not defined!")
    sys.exit()

try:
    neigh=int(argv[2])
except:
    neigh=10

def potentialFriends(user_id, neigh):
    # Параметры подключения к базе данных
    database = 'soundtest'
    db_user = 'test'
    db_password = '123456'
    host = 'localhost'
    port = '5432'  # обычно 5432 для PostgreSQL

    # Создаем строку подключения
    conn_string = f"postgresql://{db_user}:{db_password}@{host}:{port}/{database}"
#     conn_string = "postgresql://test:123456@localhost:5432/soundtest"

    # Создаем подключение
    engine = create_engine(conn_string)

    try:
        # Подготавливаем SQL запрос
        query = "SELECT * FROM user_favorite_genres;"  # Измените на ваш SQL запрос

        # Выполняем запрос и результат сохраняем в DataFrame
        df = pd.read_sql_query(query, con=engine)

        # Группируем по user_id и собираем genres_id в список
        df = df.groupby('user_id')['genre_id'].agg(list).reset_index()

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

potentialFriends(user, neigh)