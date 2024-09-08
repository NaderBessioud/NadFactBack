# Stage 1: Build the application
FROM openjdk:17-jdk-alpine as build

# Update package list
RUN apk update

# Install Tesseract, OpenSSL, and wget
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-dev \
    openssl \
    wget

# Create directory for Tesseract language data
RUN mkdir -p /usr/share/tessdata

# Download the Tesseract language package
ADD https://github.com/tesseract-ocr/tessdata/raw/main/fra.traineddata /usr/share/tessdata/fra.traineddata

# Create necessary directories
RUN mkdir -p /opt/certificates /opt/images

# Set the name of the JAR
ENV APP_FILE=*.jar

# Copy the JAR file into the container
COPY target/${APP_FILE} /app.jar

# Set environment variable for Tesseract data
ENV TESSDATA_PREFIX=/usr/share/tessdata

# Expose the application port
EXPOSE 8082

# Launch the Spring Boot application
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
