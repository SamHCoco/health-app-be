#!/bin/bash



openssl req -subj '/CN=com.samhcoco.org/O=samhcoco/C=US' -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
kubectl create secret tls keycloak-tls-secret --cert certificate.pem --key key.pem -n health-app
kubectl create secret generic keycloak-db-secret --from-literal=username=root --from-literal=password=admin -n health-app # todo - create secret for db credentials