FROM openjdk:17-jdk-alpine as build
RUN apk update
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-dev \
    openssl \
    wget
RUN mkdir -p /usr/share/tessdata
ADD https://github.com/tesseract-ocr/tessdata/raw/main/fra.traineddata /usr/share/tessdata/fra.traineddata
RUN mkdir -p /opt/certificates /opt/images
ENV APP_FILE=*.jar
COPY target/${APP_FILE} /app.jar
ENV TESSDATA_PREFIX=/usr/share/tessdata
EXPOSE 8082
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
