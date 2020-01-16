# Deploy on OpenShift

## Prerequisites

* Having an OpenShift cluster running
* Having access to the console
* Having `oc` connected

## Installation

#### Project creation

```bash
oc new-project reactive-demo
```

## Strimzi

1. Install the Strimzi operator from the operator hub
2. `oc apply -f strimzi/kafka-topic-order-descriptor.yaml`
3. `oc apply -f strimzi/kafka-topic-queue-descriptor.yaml`
4. Create a `order` topic with 5 partitions, and a `queue` topic with a single partition
5. Wait until everything is ready

You should have:  

```bash
$ oc get Kafka
  NAME       DESIRED KAFKA REPLICAS   DESIRED ZK REPLICAS
  my-kafka   3
$ oc get KafkaTopic
  NAME    PARTITIONS   REPLICATION FACTOR
  order   5            3
  queue   1            1
```

At this point, Kafka is deployed and ready to manage our application.

## Coffee-Shop

Before running the following command, build the native executable. 
Be sure to generate the Linux 64 bits executable:

```bash
cd coffeeshop-service
mvn clean package -Pnative -Dnative-image.docker-build=true -DskipTests
```

Then, deploy the application with:

```bash
oc new-build --name=coffee-shop \
  --binary=true \
  --docker-image=quay.io/quarkus/ubi-quarkus-native-binary-s2i:19.2.0
  
oc start-build coffee-shop --from-file target/coffeeshop-service-1.0-SNAPSHOT-runner
sleep 40 
oc new-app -i coffee-shop  
oc expose svc/coffee-shop
```

Once everything is ready, this page should be available:
```bash
open http://$(oc get routes | grep coffee-shop | awk -F ' ' '{print $2}')
```

## Kafka Barista

Before running the following command, build the native executable for the `barista-kafka` module. 
Be sure to generate the Linux 64 bits executable:

```bash
cd barista-kafka
mvn clean package -Pnative -Dnative-image.docker-build=true -DskipTests
```

Then, deploy the application with:


```bash
oc new-build --name=barista-kafka \
  --binary=true \
  --docker-image=quay.io/quarkus/ubi-quarkus-native-binary-s2i:19.2.0
  
oc start-build barista-kafka --from-file target/*-runner
sleep 40
oc new-app -i barista-kafka  
```

## HTTP Barista

Before running the following command, build the native executable for the `barista-http` module. 
Be sure to generate the Linux 64 bits executable:

```bash
cd barista-http
mvn clean package -Pnative -Dnative-image.docker-build=true -DskipTests
```

Then, deploy the application with:


```bash
oc new-build --name=barista-http \
  --binary=true \
  --docker-image=quay.io/quarkus/ubi-quarkus-native-binary-s2i:19.2.0
  
oc start-build barista-http --from-file target/*-runner
sleep 40
oc new-app -i barista-http  
```
