# Deployment to Kubernetes

## Prerequisites

1. You need a Kubernetes cluster and have access to it - as administrators as you need to install operators
2. You need to be able to push images to this Kubernetes cluster. The following instruction are using the internal image registry (offered by OpenShift)
 but you can use Docker Hub
3. You need `helm` to be installed on your machine 
 
## Deploying the infrastructure

Run:

```bash
cd kubernetes
./install-infra.sh
cd ..
```

It installs:

* Strimzi and instantiate a Kafka cluster
* Keda

It also creates the `coffee` namespace used for the application.
 
## Building the native executables

Be sure to be authenticated to the registry you are going to push to. For instance:

```bash
docker login -u $KUBERNETES_USER -p  $KUBERNETES_TOKEN $REGISTRY
```

NOTE: On OpenShift, if enabled, you can access the internal registry. First, get the url using:
`export REGISTRY=$(oc get route -n openshift-image-registry -o jsonpath='{$.items[*].spec.host}')` 

From the project root run:

```bash
mvn clean package -Pnative -Dquarkus.native.container-build=true
```

This command as generated the native executable using the Linux 64 architecture.

## Building and pushing the container images

Run, from the project root

```shell
cd coffeeshop-service
docker build -f src/main/docker/Dockerfile.native -t coffee/coffee-shop .
docker tag coffee/coffee-shop $REGISTRY/coffee/coffee-shop 
docker push $REGISTRY/coffee/coffee-shop 
cd ..

cd barista-http
docker build -f src/main/docker/Dockerfile.native -t coffee/barista-http .
docker tag coffee/barista-http $REGISTRY/coffee/barista-http 
docker push $REGISTRY/coffee/barista-http 
cd ..

cd barista-kafka
docker build -f src/main/docker/Dockerfile.native -t coffee/barista-kafka .
docker tag coffee/barista-kafka $REGISTRY/coffee/barista-kafka 
docker push $REGISTRY/coffee/barista-kafka 
cd ..
```

On OpenShift you can check the image stream using:

```shell
$ oc get is -n coffee
barista-http    .../coffee/barista-http    latest   About a minute ago
barista-kafka   .../coffee/barista-kafka   latest   28 seconds ago
coffee-shop     .../coffee/coffee-shop     latest   5 seconds ago
```

## Deploy the application

Open the `kubernetes/charts/values.yaml`, and if needed, edit the registry part of the image names.

Then, from the project root, run:

```shell 
helm install coffee-v1 kubernetes/charts  -n coffee --wait --timeout 300s
oc apply -f kubernetes/route.yaml -n coffee
export COFFEE_URL="https://$(oc get route -n coffee -o jsonpath='{$.items[*].spec.host}')" 
echo "Open url: ${COFFEE_URL}"
```


## Uninstalling

```shell 
cd kubernetes
./uninstall.sh
```

## Updating the charts

```shell
helm uninstall coffee-v1 -n coffee
oc delete KafkaTopic -name orders -n kafka
oc delete KafkaTopic -name queue -n kafka
helm install coffee-v1 kubernetes/charts  -n coffee --wait --timeout 300s
```
