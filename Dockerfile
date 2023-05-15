FROM eclipse-temurin:17-alpine

# Run with lower privileges
RUN addgroup -S springGroup && adduser -S springUser -G springGroup
USER springUser:springGroup

# Create work directory
WORKDIR /usr/app

# Copy jar to image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} usr/app/app.jar


# Base command
ENTRYPOINT ["java", "-jar", "usr/app/app.jar"]