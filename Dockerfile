FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} nadfact.jar
ENTRYPOINT ["java","-jar","/nadfact.jar"]
EXPOSE 8082
