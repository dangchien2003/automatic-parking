
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./target /app/target
COPY ./.env /app/.env
ENTRYPOINT ["java", "-jar", "target/automatic-parking-0.0.1-SNAPSHOT.jar"]