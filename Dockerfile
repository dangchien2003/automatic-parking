FROM openjdk:17-jdk-slim
COPY . .
WORKDIR /app
ENTRYPOINT ["java", "-jar", "./target/automatic-parking-0.0.1-SNAPSHOT.jar"]