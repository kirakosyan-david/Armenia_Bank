# Этап 1: Сборка JAR
FROM maven:3-openjdk-17 AS builder
WORKDIR /build

COPY . .

RUN mvn -f pom.xml clean install -DskipTests

# Этап 2: Запуск
FROM openjdk:17.0.2-slim-bullseye
WORKDIR /app

# Копируем JAR
COPY --from=builder /build/transaction-service-src/target/*.jar app.jar

# Копируем wait-for-it.sh
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Устанавливаем curl
RUN apt-get update && apt-get install -y curl procps coreutils && rm -rf /var/lib/apt/lists/*


EXPOSE 8084

# Ожидание зависимостей
ENTRYPOINT ["/app/wait-for-it.sh", "postgres:5432", "--timeout=60", "--strict", "--", \
            "/app/wait-for-it.sh", "kafka:9092", "--timeout=60", "--strict", "--", \
            "java", "-jar", "app.jar"]