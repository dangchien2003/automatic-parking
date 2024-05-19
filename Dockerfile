FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./target/automatic-parking-0.0.1-SNAPSHOT.jar /app/target/automatic-parking-0.0.1-SNAPSHOT.jar
COPY ./.env /app/.env
WORKDIR /app
ENTRYPOINT ["java", "-jar", "target/automatic-parking-0.0.1-SNAPSHOT.jar"]