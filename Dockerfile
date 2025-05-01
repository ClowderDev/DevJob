FROM openjdk:21

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} devjob.jar

ENTRYPOINT [ "java", "-jar", "devjob.jar" ]

EXPOSE 8080
