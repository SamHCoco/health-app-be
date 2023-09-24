#!/bin/bash

version=""

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
../mvnw clean install -pl core,store
sudo docker build -t health-app-store-service:$version .
docker save health-app-store-service:$version > health-app-store-service.tar