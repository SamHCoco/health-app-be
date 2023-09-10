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


echo -n "Building spring-cloud-gateway-service Docker image: version $version"

docker build -t health-app-spring-cloud-gateway-service:$version .