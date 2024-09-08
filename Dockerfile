FROM openjdk:17-jdk-alpine

# Install necessary packages, including Tesseract and its dependencies
RUN apk update && \
    apk add --no-cache \
        openssl \
        tesseract-ocr \
        tesseract-ocr-dev \
        wget

# Download and install Tesseract language data
RUN mkdir -p /usr/share/tessdata && \
    wget -O /usr/share/tessdata/fra.traineddata https://github.com/tesseract-ocr/tessdata/raw/master/fra.traineddata

# Create necessary directories
RUN mkdir -p /opt/certificates /opt/images

# Copy the JAR file into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar

# Set environment variable for Tesseract data
ENV TESSDATA_PREFIX=/usr/share/tessdata

# Expose the application port
EXPOSE 8082

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "/nadfact.jar"]
