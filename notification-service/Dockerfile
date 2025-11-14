FROM maven:3-openjdk-17 AS builder
WORKDIR /build

COPY . .

RUN mvn -f pom.xml clean package -DskipTests

FROM openjdk:17.0.2-slim-bullseye
WORKDIR /app

COPY --from=builder /build/notification-service-src/target/*.jar app.jar

RUN apt-get update && apt-get install -y curl procps coreutils && rm -rf /var/lib/apt/lists/*

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]