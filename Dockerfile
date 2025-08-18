FROM ubuntu:latest
LABEL authors="Bryan Tao Long"

ENTRYPOINT ["top", "-b"]

# 使用官方 OpenJDK 17 基础镜像
FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 将 jar 包复制到容器中（注意版本号对应）
COPY target/user-system-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（Spring Boot 默认 8080）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
