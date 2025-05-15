# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-slim

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл сборки внутрь контейнера
COPY target/logger-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт (если нужно)
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
