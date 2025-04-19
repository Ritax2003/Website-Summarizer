# Use the official OpenJDK image from Docker Hub
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file into the container
COPY build/libs/spring-backend_docker-0.0.1-SNAPSHOT.jar /app/spring-backend.jar


# Expose port 8080 for the application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "spring-backend.jar"]
