FROM java:8-jre

MAINTAINER Vladyslav Yemelianov <emelyanov.vladyslav@gmail.com>

RUN curl -fL -o /tmp/geckodriver https://raw.githubusercontent.com/news-aggregator-bot/service/master/na-service/src/main/resources/drivers/geckodriver_linux \
 && mv /tmp/geckodriver /usr/local/bin/geckodriver \
 && chmod +x /usr/local/bin/geckodriver

RUN curl -fL -o /tmp/firefox.tar.bz2 \
         https://ftp.mozilla.org/pub/firefox/releases/78.0.1/linux-x86_64/en-GB/firefox-78.0.1.tar.bz2 \
 && tar -xjf /tmp/firefox.tar.bz2 -C /tmp/ \
 && mv /tmp/firefox /opt/firefox

ENV PATH="/opt/firefox/:${PATH}"
ADD ./target/na-service.jar /app/
CMD ["java", "-Xmx500m", "-jar", "/app/na-service.jar"]

EXPOSE 8080