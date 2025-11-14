FROM maven:3-openjdk-17 AS builder
WORKDIR /build

COPY . .

RUN mvn -f pom.xml clean install -DskipTests

FROM openjdk:17.0.2-slim-bullseye
WORKDIR /app

COPY --from=builder /build/wallet-service-src/target/*.jar app.jar

COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

RUN apt-get update && apt-get install -y curl procps coreutils && rm -rf /var/lib/apt/lists/*

EXPOSE 8083

ENTRYPOINT ["/app/wait-for-it.sh", "postgres:5432", "--timeout=60", "--strict", "--", \
            "/app/wait-for-it.sh", "kafka:9092", "--timeout=60", "--strict", "--", \
            "/app/wait-for-it.sh", "backend-keycloak-auth:8080", "--timeout=300", "--strict", "--", \
            "java", "-jar", "app.jar"]