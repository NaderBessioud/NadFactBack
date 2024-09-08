FROM openjdk:17-jdk-alpine

# Install necessary packages, including Tesseract
RUN apk update && \
    apk add --no-cache openssl tesseract-ocr tesseract-ocr-dev

# Download and install Tesseract language data
RUN mkdir -p /usr/share/tessdata && \
    apk add --no-cache wget && \
    wget -O /usr/share/tessdata/fra.traineddata https://github.com/tesseract-ocr/tessdata/raw/master/fra.traineddata

RUN mkdir -p /opt/certificates
RUN mkdir -p /opt/images

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar

# Set environment variable for Tesseract data
ENV TESSDATA_PREFIX=/usr/share/tessdata

ENTRYPOINT ["java","-jar","/nadfact.jar"]
EXPOSE 8082
