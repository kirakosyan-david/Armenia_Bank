FROM openjdk:17.0.2-slim-bullseye

COPY notification-service-src/target/*.jar app.jar

EXPOSE 8080

RUN apt-get update && apt-get install -y curl telnet

ENTRYPOINT ["java", "-jar", "/app.jar"]