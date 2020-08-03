wget -q $1 -O docker-stack-service.yml
echo "Deploying new na-service stack"
docker stack rm na-service
docker stack deploy --compose-file docker-stack-service.yml --with-registry-auth na-service