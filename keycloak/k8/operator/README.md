# Keycloak Operator Installation

### Installing by using kubectl without Operator Lifecycle Manager
You can install the Operator on a vanilla Kubernetes cluster by using kubectl commands:

- Install the CRDs by entering the following commands:

    kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/22.0.3/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
 
    kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/22.0.3/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml`


- Install the Keycloak Operator deployment by entering the following command:

    kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/22.0.3/kubernetes/kubernetes.yml

Currently the Operator watches only the namespace where the Operator is installed.
source: https://www.keycloak.org/operator/installation

Deployment: https://www.keycloak.org/operator/basic-deployment