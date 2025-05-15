# Makefile для управления проектом через Docker

# Сборка .jar файла
build-jar:
	mvn clean package -DskipTests

# Пересобрать и поднять все контейнеры
up:
	docker compose up -d --build

# Остановить все контейнеры
down:
	docker compose down

# Перезапустить контейнеры без пересборки
restart:
	docker compose restart

# Посмотреть логи приложения
logs:
	docker compose logs -f logger

# Пересобрать только контейнер с приложением
rebuild-logger:
	docker compose build logger

# Перезапустить только logger
restart-logger:
	docker compose stop logger && docker compose up -d logger
