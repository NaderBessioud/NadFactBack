# Use a base image with Java (OpenJDK 17 in this case)
FROM openjdk:17-jdk-slim

# Install required packages and Tesseract OCR
RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    libtesseract-dev \
    openssl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create directories for certificates and images
RUN mkdir -p /opt/certificates \
    && mkdir -p /opt/images

# Copy your application JAR and other necessary files
COPY nadfact.jar /app/nadfact.jar

# Set the working directory
WORKDIR /app

# Expose the port on which the application will run
EXPOSE 8082

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "nadfact.jar"]
