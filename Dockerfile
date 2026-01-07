# Multi-stage Dockerfile for Trip Planner API

# Stage 1: Build the application
FROM gradle:8.10-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Make wrapper executable
RUN chmod +x gradlew

# Build the application (skip tests, checkStyle for faster builds)
RUN ./gradlew bootJar -x test -x checkstyleMain -x checkstyleTest

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r tripplanner && useradd -r -g tripplanner tripplanner

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to the non-root user
RUN chown -R tripplanner:tripplanner /app

# Switch to non-root user
USER tripplanner

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]