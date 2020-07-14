mvn clean package
docker build na-service -t docker.pkg.github.com/news-aggregator-bot/artifactory/vlad110kg.na-service:latest
docker push docker.pkg.github.com/news-aggregator-bot/artifactory/vlad110kg.na-service:latest
