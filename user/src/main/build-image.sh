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


echo -n "Building user-service Docker image: version $version \n\n\n"

docker build -t health-app-user-service:$version .