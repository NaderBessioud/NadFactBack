# Use the OpenJDK 17 Alpine base image
FROM openjdk:17-jdk-alpine

# Update and install Tesseract and its dependencies
RUN apk update && \
    apk add --no-cache openssl tesseract-ocr=4.1.1-r1 tesseract-ocr-dev leptonica && \
    rm -rf /var/cache/apk/*

# Create necessary directories
RUN mkdir -p /opt/certificates /opt/images

# Set the environment variable for Tesseract
ENV TESSDATA_PREFIX=/usr/share/tessdata

# Copy your Spring Boot application jar file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8082

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/nadfact.jar"]
