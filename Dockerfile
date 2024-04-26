# Sử dụng OpenJDK 11 làm base image
FROM openjdk:17

# Set thư mục làm việc mặc định trong Docker container
WORKDIR /app

# Copy file JAR/WAR vào thư mục làm việc
COPY target/automatic-parking-0.0.1-SNAPSHOT.jar /app/automatic-parking-0.0.1-SNAPSHOT.jar

# Chạy ứng dụng khi container được khởi chạy
CMD ["java", "-jar", "automatic-parking-0.0.1-SNAPSHOT.jar"]

# Expose cổng mà ứng dụng của bạn đang chạy
EXPOSE 8080