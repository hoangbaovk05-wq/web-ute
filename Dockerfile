# Giai đoạn 1: biên dịch project bằng Maven và JDK 21.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package


# Giai đoạn 2: chạy ứng dụng bằng Tomcat 9 và JDK 21.
FROM tomcat:9.0-jdk21-temurin

# Xóa các ứng dụng mặc định.
RUN rm -rf /usr/local/tomcat/webapps/*

# Đặt tên ROOT.war để truy cập ứng dụng ngay tại đường dẫn /.
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]