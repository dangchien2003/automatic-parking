# Sử dụng OpenJDK 11 làm base image
FROM openjdk:11

# Set thư mục làm việc mặc định trong Docker container
WORKDIR /app

# Copy file JAR/WAR vào thư mục làm việc
COPY target/your-application.jar /app/your-application.jar

# Chạy ứng dụng khi container được khởi chạy
CMD ["java", "-jar", "your-application.jar"]

# Expose cổng mà ứng dụng của bạn đang chạy
EXPOSE 8080