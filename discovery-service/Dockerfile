# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy all project files (use root as context in docker-compose)
COPY ../ .

# Build only the current module with dependencies
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the built JAR
COPY --from=builder /workspace/discovery-service/target/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]