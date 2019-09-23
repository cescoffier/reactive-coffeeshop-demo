# Deploy on OpenShift

## Strimzi

1. Install the Strimzi operator from the operator hub
2. Create a kafka cluster instance, with 2 instances of kafka and 1 zookeeper, update the replicate attributes.
The cluster must be named `my-kafka` and be part of the project namespace. In other words, `my-kafka-kafka-brokers:9092` 
should give access to it. 
3. Create a `order` topic with 5 partitions, and a `queue` topic with a single partition
4. Wait until everything is ready  

## Coffee-Shop

Before running the following command, build the native executable. 
Be sure to generate the Linux 64 bits executable.

```bash
cd cofeeshop-service
oc new-build --name=coffee-shop \
  --binary=true \
  --docker-image=quay.io/quarkus/ubi-quarkus-native-binary-s2i:19.2.0
  
oc start-build coffee-shop --from-file target/coffeeshop-service-1.0-SNAPSHOT-runner 
oc new-app -i coffee-shop  
oc expose svc/cofee-shop
```

## Kafka Barista

Before running the following command, build the native executable for the `barista-kafka` module. 
Be sure to generate the Linux 64 bits executable.

```bash
cd barista-kafka
oc new-build --name=barista-kafka \
  --binary=true \
  --docker-image=quay.io/quarkus/ubi-quarkus-native-binary-s2i:19.2.0
  
oc start-build barista-kafka --from-file target/*-runner
oc new-app -i barista-kafka  
```
