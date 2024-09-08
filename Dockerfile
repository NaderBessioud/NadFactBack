FROM openjdk:17-jdk-alpine

# Update and install necessary packages, including Tesseract and language data
RUN apk update && \
    apk add --no-cache openssl tesseract-ocr tesseract-ocr-dev tesseract-ocr-fra

RUN mkdir -p /opt/certificates
RUN mkdir -p /opt/images

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar

# Set environment variable for Tesseract data
ENV TESSDATA_PREFIX=/usr/share/tessdata

ENTRYPOINT ["java","-jar","/nadfact.jar"]
EXPOSE 8082
