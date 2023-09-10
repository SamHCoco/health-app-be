#!/bin/bash

./mvnw clean install

version=1.0.0

./health/src/main/build-image.sh -v $version
./store/src/main/build-image.sh -v $version
./user/src/main/build-image.sh -v $version
./spring-cloud-gateway/src/main/build-image.sh -v $version