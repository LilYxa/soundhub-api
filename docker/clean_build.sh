#!/bin/bash
set -e

cd ..
mvn clean package

cd docker
docker-compose down --remove-orphans
docker-compose build
docker-compose up --build