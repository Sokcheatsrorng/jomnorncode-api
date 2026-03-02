# Builder stage
FROM gradle:8.4-jdk17-alpine AS builder
WORKDIR /app
COPY . .
# Skipping tests – only do this temporarily if needed
RUN gradle build --no-daemon -x test

# Final stage – runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Keep your volumes
VOLUME /app/filestorage/forum-medias
VOLUME /keys

# Use JRE-friendly entrypoint (no need for full JDK in prod)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]