# Build stage
FROM maven:3.8.5-openjdk-17-slim AS build
LABEL authors="jae_young.kim"
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","app.jar"]
