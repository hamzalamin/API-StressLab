FROM eclipse-temurin:22-jre-alpine
LABEL authors="unregistred"

WORKDIR /app
COPY ./target/*.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

