#!/bin/sh

# Create namespaces for Strimzi and Kafka
kubectl create ns strimzi
kubectl create ns kafka

# Install Strimzi Helm chart
helm repo add strimzi https://strimzi.io/charts
helm install strimzi strimzi/strimzi-kafka-operator -n strimzi --set watchNamespaces={kafka} --wait --timeout 300s

# Install Strimzi custom resource, and wait for cluster creation
kubectl apply -f kafka-strimzi.yaml -n kafka
kubectl wait --for=condition=Ready kafkas/my-cluster -n kafka --timeout 180s

# Create namespace for Keda
kubectl create ns keda

# Install Keda Helm chart
helm repo add kedacore https://kedacore.github.io/charts
helm install keda kedacore/keda -n keda --wait --timeout 300s

kubectl create ns coffee