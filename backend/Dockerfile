FROM maven:3.9.8-eclipse-temurin-17-alpine AS build

WORKDIR /backend

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests



FROM openjdk:17-jdk-alpine

WORKDIR /backend

COPY --from=build backend/target/various_topics_forum-0.0.1-SNAPSHOT.jar ./various_topics_forum-0.0.1-SNAPSHOT.jar

ENV PORT=8080
ENV MYSQL_HOST=db
ENV MYSQL_PORT=3306
ENV MYSQL_DB=various_topics_forum
ENV MAIL_HOST=maildev
ENV MAIL_PORT=1025

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/backend/various_topics_forum-0.0.1-SNAPSHOT.jar"]
