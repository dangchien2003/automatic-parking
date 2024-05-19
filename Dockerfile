FROM openjdk:17-alpine
COPY . /app
COPY ./target/automatic-parking-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]