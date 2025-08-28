# Stage 1: Build the application using Maven with Java 17
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller Docker image using Java 17
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/kitchen-assistant-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java","-jar","app.jar"]