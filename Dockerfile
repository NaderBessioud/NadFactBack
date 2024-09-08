FROM openjdk:17-jdk-alpine

# Update and install necessary packages, including Tesseract
RUN apk update && \
    apk add --no-cache openssl tesseract-ocr tesseract-ocr-dev

RUN mkdir -p /opt/certificates
RUN mkdir -p /opt/images

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar

ENTRYPOINT ["java","-jar","/nadfact.jar"]
EXPOSE 8082
