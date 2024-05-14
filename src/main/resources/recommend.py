import pandas as pd
import psycopg2
from sqlalchemy import create_engine
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.neighbors import NearestNeighbors
import uuid
import sys
from sys import argv
from dotenv import dotenv_values
config = dotenv_values("src/main/resources/application.properties")

def potentialFriends(user_id, neigh):
    # Создаем подключение
    engine = engine_init()
    try:
        # Подготавливаем SQL запрос
        query = "SELECT * FROM user_favorite_genres;"
        # Выполняем запрос и результат сохраняем в DataFrame
        df = pd.read_sql_query(query, con=engine)
        # Группируем по user_id и собираем genres_id в список
        df = df.groupby('user_id')['genre_id'].agg(list).reset_index()
        users_registered = len(df.index)
        if(neigh>users_registered):
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
        neighbors_userIds = df.iloc[indices[0]]['user_id'].tolist()
        if user_id in neighbors_userIds:
            neighbors_userIds.remove(user_id)
        for i in neighbors_userIds:
            print(str(i))
        return neighbors_userIds
    except Exception as e:
        print(f"Python error: {e}")
        return e
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

if __name__ == '__main__':
    try:
        user = uuid.UUID(argv[1])
    except:
        print("User is not defined!")
        sys.exit()

    try:
        neigh = int(argv[2])
    except:
        neigh = int(config['python.neighbors.default'])
    potentialFriends(user, neigh)