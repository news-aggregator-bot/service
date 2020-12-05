FROM adoptopenjdk/openjdk11

MAINTAINER Vladyslav Yemelianov <emelyanov.vladyslav@gmail.com>
RUN useradd -ms /bin/bash admin

RUN mkdir /opt/driver \
 && curl -L https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-linux64.tar.gz | tar xz -C /opt/driver \
 && chown -R admin:admin /opt/driver \
 && chmod +x /opt/driver/geckodriver

RUN curl -fL -o /tmp/firefox.tar.bz2 https://ftp.mozilla.org/pub/firefox/releases/78.0.1/linux-x86_64/en-GB/firefox-78.0.1.tar.bz2 \
 && tar -xjf /tmp/firefox.tar.bz2 -C /tmp/ \
 && mv /tmp/firefox /opt/firefox

ENV PATH="/opt/firefox/:${PATH}"
ADD ./target/na-service.jar /app/
USER admin
CMD ["java", "-Xmx500m", "-jar", "/app/na-service.jar"]

EXPOSE 8080