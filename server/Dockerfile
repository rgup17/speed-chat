# Stage 1: Build stage using Maven with Java 23
FROM maven:3.9.9-eclipse-temurin-23-alpine AS build

WORKDIR /app

# Copy Maven dependencies and source code
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage using Java 23 only
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

# Expose port for the Spring Boot application
#EXPOSE 8080

# Copy the JAR file built in the first stage
COPY --from=build /app/target/*.jar app.jar

# Run the Spring Boot app with Java 23
ENTRYPOINT ["java", "-jar", "app.jar"]
