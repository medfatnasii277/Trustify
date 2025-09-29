# Stage 1: Build the application
FROM maven:3.9-amazoncorretto-21-debian as build

# Set working directory
WORKDIR /app

# Copy the Maven POM file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src/

# Build the application
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM amazoncorretto:21-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]