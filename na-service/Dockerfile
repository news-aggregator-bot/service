FROM java:8-jre

MAINTAINER Vladyslav Yemelianov <emelyanov.vladyslav@gmail.com>

ADD ./target/na-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/na-service.jar"]

EXPOSE 8080