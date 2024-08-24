FROM openjdk:17-jdk-alpine
RUN apt-get update && \
    apt-get install -y openssl && \
    apt-get clean
RUN mkdir -p /opt/certificates
RUN mkdir -p /opt/images
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar
ENTRYPOINT ["java","-jar","/nadfact.jar"]
EXPOSE 8082
