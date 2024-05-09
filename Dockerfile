FROM maven:3.8.5-openjdk-17 AS build
COPY . .
#COPY ./.env ./.env
#RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /
COPY --from=build /target/automatic-parking-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo.jar"]