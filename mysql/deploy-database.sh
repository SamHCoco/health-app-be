#!/bin/bash

namespace=health-app

kubectl apply -f namespace.yaml
kubectl apply -f mysql-config.yaml -n ${namespace}
kubectl apply -f mysql-secret.yaml -n ${namespace}
kubectl apply -f statefulset.yaml -n ${namespace}
