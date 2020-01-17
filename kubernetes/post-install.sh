#!/bin/bash

# Display overall system state for kafka and coffee namespaces
kubectl get services -n kafka
kubectl get services -n coffee

# Get NodePort for coffeeshop-service
NODE_PORT=$(kubectl get -o jsonpath="{.spec.ports[0].nodePort}" services coffee-v1-coffeeshop-service --namespace coffee)
# Get external IP address for node (assumes single node).
NODE_IP=$(kubectl get nodes -o jsonpath='{ $.items[*].status.addresses[?(@.type=="ExternalIP")].address }')
# If there is no ExternalIP, assume localhost
echo "Order coffees at http://${NODE_IP:-"localhost"}:${NODE_PORT}/"