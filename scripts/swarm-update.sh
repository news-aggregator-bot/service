wget -q $1 -O docker-stack-service.yml
docker stack ls | grep -q na-service
if [ $? -eq 0 ]; then
    echo "Updating only selected services"
    docker service update --image docker.pkg.github.com/news-aggregator-bot/artifactory/vlad110kg.na-service --with-registry-auth na-service_na-service
else
    echo "Deploying new na stack"
    docker stack deploy --compose-file docker-stack-service.yml --with-registry-auth na-service
fi