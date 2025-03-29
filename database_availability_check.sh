#!/bin/bash

# Переменные для базы данных
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="casino_db"
DB_USER="casino_user"
DB_PASSWORD="casino_pass"
CONFIG_FILE="database_configuration"

# Проверка доступности PostgreSQL
echo "Проверяем доступность базы данных PostgreSQL..."
until PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c '\q' > /dev/null 2>&1; do
  echo "База данных недоступна, пробуем снова через 2 секунды..."
  sleep 2
done

echo "База данных доступна! Запускаем приложение..."

# Запуск Spring Boot приложения с файлом конфигурации database_configuration.yml
java -Dspring.config.name=$CONFIG_FILE -jar target/your-application.jar