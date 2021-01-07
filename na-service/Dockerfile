FROM adoptopenjdk/openjdk11

MAINTAINER Vladyslav Yemelianov <emelyanov.vladyslav@gmail.com>

RUN apt-get update && apt-get install -y wget xvfb unzip gnupg2

RUN apt-get update && \
    apt-get install -y gnupg wget curl unzip --no-install-recommends && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list && \
    apt-get update -y && \
    apt-get install -y google-chrome-stable && \
    CHROMEVER=$(google-chrome --product-version | grep -o "[^\.]*\.[^\.]*\.[^\.]*") && \
    DRIVERVER=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROMEVER") && \
    rm /etc/apt/sources.list.d/google-chrome.list && \
    rm -rf /var/lib/apt/lists/* /var/cache/apt/* && \
    sed -i 's/"$HERE\/chrome"/"$HERE\/chrome" --no-sandbox/g' /opt/google/chrome/google-chrome && \
    wget -q -O /tmp/chromedriver.zip "http://chromedriver.storage.googleapis.com/$DRIVERVER/chromedriver_linux64.zip" && \
    unzip /tmp/chromedriver.zip -d /opt && \
    rm /tmp/chromedriver.zip && \
    mv /opt/chromedriver /opt/chromedriver-$DRIVERVER && \
    chmod 755 /opt/chromedriver-$DRIVERVER && \
    ln -s /opt/chromedriver-$DRIVERVER /usr/bin/chromedriver

## Google Chrome
#ARG CHROME_VERSION=86.0.4240.22
#RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
#	&& echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
#	&& apt-get update -qqy \
#	&& apt-get -qqy install google-chrome-stable=$CHROME_VERSION \
#	&& rm /etc/apt/sources.list.d/google-chrome.list \
#	&& rm -rf /var/lib/apt/lists/* /var/cache/apt/* \
#	&& sed -i 's/"$HERE\/chrome"/"$HERE\/chrome" --no-sandbox/g' /opt/google/chrome/google-chrome
#
## ChromeDriver
#ARG CHROME_DRIVER_VERSION=86.0.4240.22
#RUN wget -q -O  https://chromedriver.storage.googleapis.com/$CHROME_DRIVER_VERSION/chromedriver_linux64.zip \
#	&& unzip /tmp/chromedriver.zip -d /opt \
#	&& rm /tmp/chromedriver.zip \
#	&& mv /opt/chromedriver /opt/chromedriver-$CHROME_DRIVER_VERSION \
#	&& chmod 755 /opt/chromedriver-$CHROME_DRIVER_VERSION \
#	&& ln -s /opt/chromedriver-$CHROME_DRIVER_VERSION /usr/bin/chromedriver

ADD ./target/na-service.jar /app/
USER root
CMD ["java", "-Xmx512m", "-jar", "/app/na-service.jar"]

EXPOSE 8080