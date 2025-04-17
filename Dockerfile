# 构建阶段：使用Maven和JDK镜像
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 复制pom.xml并下载依赖，利用Docker缓存机制
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码并构建应用
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段：使用更小的JRE镜像
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 复制构建的jar文件
COPY --from=build /app/target/*.jar app.jar

# 设置入口点
ENTRYPOINT ["java", "-jar", "app.jar"]