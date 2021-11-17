FROM adoptopenjdk/openjdk11

MAINTAINER Vladyslav Yemelianov <emelyanov.vladyslav@gmail.com>

ADD ./target/na-service.jar /app/
USER root
CMD ["java", "-Xmx512m", "-jar", "/app/na-service.jar"]

EXPOSE 8080