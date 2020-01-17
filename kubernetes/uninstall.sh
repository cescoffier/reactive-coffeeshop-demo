#!/bin/sh

# Uninstall coffeeshop-demo and kafka cluster
helm uninstall coffee-v1 -n coffee

# Delete coffee namespace
kubectl delete ns coffee

# Uninstall Keda Helm chart
helm uninstall keda -n keda

# Remove Keda artifacts (not sure why helm uninstall doesn't clean this up?)
kubectl delete apiservice v1alpha1.keda.k8s.io
kubectl delete crd scaledobjects.keda.k8s.io
kubectl delete crd triggerauthentications.keda.k8s.io

# Delete keda namespace
kubectl delete ns keda

# Delete Kafka cluster
kubectl delete -f kafka-strimzi.yaml -n kafka

# Uninstall Strimzi Helm chart
helm uninstall strimzi -n strimzi

# Delete namespaces for Strimzi and Kafka
kubectl delete ns strimzi
kubectl delete ns kafka