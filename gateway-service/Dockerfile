FROM maven:3-openjdk-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM ubuntu:22.04
WORKDIR /app

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl bash procps && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/target/*.jar /app/app.jar

COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

EXPOSE 9091

ENTRYPOINT ["/bin/bash", "/app/start.sh"]
