# We use Eclipse Temurin (the new standard for OpenJDK)
FROM eclipse-temurin:21-jdk-alpine

VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]