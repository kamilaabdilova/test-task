# Используем официальный образ Maven для сборки
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:resolve

# Копируем исходный код проекта
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Используем официальный образ JDK для выполнения приложения
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный .jar файл из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Указываем порт, который будет использовать приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
