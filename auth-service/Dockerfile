FROM maven:3-openjdk-17 AS builder
WORKDIR /build

COPY . .

RUN mvn -f pom.xml clean install -DskipTests -B --no-transfer-progress

FROM openjdk:17.0.2-slim-bullseye
WORKDIR /app

RUN apt-get update && apt-get install -y curl procps coreutils && rm -rf /var/lib/apt/lists/*


COPY --from=builder /build/auth-service-src/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]