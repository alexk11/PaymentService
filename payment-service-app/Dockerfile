# Используем минимальный runtime-образ с JDK 21
FROM eclipse-temurin:21-jre-alpine
# Задаем переменную окружения с названием jar-файла, можно переопределить при сборке
RUN echo "Current PWD:" && pwd
ARG JAR_FILE=target/payment-service-app-0.0.1-SNAPSHOT.jar
# Создаём рабочую директорию приложения
WORKDIR /app
# Копируем jar-файл в контейнер
COPY ${JAR_FILE} app.jar
RUN echo "Listing files in current directory:" && ls -lR
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
# Открываем порт для доступа к приложению
EXPOSE 8082
