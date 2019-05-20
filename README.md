# Coffeeshop Demo with Quarkus

This directory contains a set of demo around _reactive_ in Quarkus with Kafka.
It demonstrates the elasticity and resilience of the system.

## Build

```bash
mvn clean package
```

## Prerequisites

Run Kafka with:

```bash
docker-compose up
```

Then, create the `orders` topic with `./create-orders.sh`

# Run the demo

You need to run:

* the coffee shop service
* the HTTP barista
* the Kafka barista

Im 3 terminals: 

```bash
cd coffeeshop-service
mvn compile quarkus:dev
```

```bash
cd barista-http
mvn compile quarkus:dev
```

```bash
cd barista-kafka
mvn compile quarkus:dev
```

# Execute with HTTP

The first part of the demo shows HTTP interactions:

* Barista code: `me.escoffier.quarkus.coffeeshop.BaristaResource`
* CoffeeShop code: `me.escoffier.quarkus.coffeeshop.CoffeeShopResource.http`
* Generated client: `me.escoffier.quarkus.coffeeshop.http.BaristaService`

Important points:
* Request-reply

Order coffees with:

```bash
http POST :8080/http product=latte name=clement
http POST :8080/http product=expresso name=neo
http POST :8080/http product=mocha name=flore
```

Stop the HTTP Barista, you can't order coffee anymore.

# Execute with Kafka

* Barista code: `me.escoffier.quarkus.coffeeshop.KafkaBarista`: Read from `orders`, write to `queue`
* Bridge in the CoffeeShop: `me.escoffier.quarkus.coffeeshop.messaging.KafkaBaristas` just enqueue the orders in a single thread (one counter)
* Get prepared beverages on `me.escoffier.quarkus.coffeeshop.dashboard.BoardResource` and send to SSE

* Open browser to http://localhost:8080/queue
* Order coffee with:

```bash
http POST :8080/messaging product=latte name=clement
http POST :8080/messaging product=expresso name=neo
http POST :8080/messaging product=mocha name=flore
```

# Baristas do breaks

1. Stop the Kafka barista
1. Continue to enqueue order
```bash
http POST :8080/messaging product=frappuccino name=clement
http POST :8080/messaging product=chai name=neo
http POST :8080/messaging product=hot-chocolate name=flore
```
1. On the dashboard, the orders are in the "IN QUEUE" state
1. Restart the barista
1. They are processed

# 2 baristas are better

1. Start a second barista with: 
```bash
java -Dquarkus.http.port=9095 -Dbarista.name=tom -jar target/barista-kafka-1.0-SNAPSHOT-runner.jar
```
1. Order more coffee
```bash
http POST :8080/messaging product=frappuccino name=clement
http POST :8080/messaging product=chai name=neo
http POST :8080/messaging product=hot-chocolate name=flore
http POST :8080/messaging product=latte name=clement
http POST :8080/messaging product=expresso name=neo
http POST :8080/messaging product=mocha name=flore
```

The dashboard shows that the load is dispatched among the baristas.
