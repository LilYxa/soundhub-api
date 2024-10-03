#!/bin/bash

java -jar /app/app.jar &

if [ -f .env ]; then
  source .env
else
  echo "Error: .env file not found"
  exit 1
fi

cd /app/recommendationApi
exec pipenv run start