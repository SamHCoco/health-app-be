#!/bin/bash

version=""
service="user-service"

while getopts "v:" opt; do
  case $opt in
    v)
      version="$OPTARG"
      ;;
    \?)
      exit 1
      ;;
  esac
done


if [ -z "$version" ]; then
  echo "Image version '-v' required."
  exit 1
fi


echo -n "Building store-service Docker image: version $version"

cd ..

./mvnw clean install -pl core,store

cd store

docker rmi health-app-$service:$version

sudo docker build -t health-app-$service:$version .

sudo docker save health-app-$service:$version > health-app-$service.tar

eval $(minikube docker-env)

docker rmi health-app-$service:$version

docker load < health-app-$service.tar

kubectl delete deployment health-app-$service -n health-app
kubectl apply -f ./src/main/k8/

