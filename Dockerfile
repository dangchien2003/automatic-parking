FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./target/automatic-parking-0.0.1-SNAPSHOT.jar /app/automatic-parking-0.0.1-SNAPSHOT.jar
COPY ./.env /app/.env
ENTRYPOINT ["java", "-jar", "automatic-parking-0.0.1-SNAPSHOT.jar"]