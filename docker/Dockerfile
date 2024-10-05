FROM python:3.12-slim

RUN apt-get update && apt-get install -y openjdk-17-jdk redis-tools

RUN pip install pipenv

WORKDIR /app

COPY target/api-0.0.1-SNAPSHOT.jar /app/app.jar

COPY recommendationApi /app/recommendationApi

COPY docker/application.properties /app/resources/application.properties

WORKDIR /app/recommendationApi
RUN pipenv install --system --deploy

WORKDIR /app

EXPOSE 8080 8888

COPY docker/.env /app/.env
COPY docker/.env /app/recommendationApi/.env

COPY docker/start_server.sh /app/start_server.sh
RUN chmod +x /app/start_server.sh

ENTRYPOINT ["/app/start_server.sh"]