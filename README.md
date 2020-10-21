# Coffeeshop Demo with Quarkus

This directory contains a set of demo around _reactive_ in Quarkus with Kafka.
It demonstrates the elasticity and resilience of the system.

## Build

```bash
mvn clean package
```

Install node.js dependencies (<a href="https://nodejs.org/en/download/">node.js</a> is required)

```bash
cd barista-node/ && npm install
```

## Prerequisites

Install Kafka locally for the Kafka tools e.g.

```bash
brew install kafka
```

Run Kafka with:

```bash
docker-compose up
```

In case of previous run, you can clean the state with

```bash
docker-compose down
docker-compose rm
```

Then, create the `orders` topic with `./create-topics.sh`

# Run the demo

You need to run:

* the coffee shop service
* the HTTP barista
* the Kafka barista / the Node.js Kafka barista

In 3 terminals: 

```bash
cd coffeeshop-service
mvn compile quarkus:dev
```

```bash
cd barista-http
java -jar target/barista-http-1.0-SNAPSHOT-runner.jar
```

#### Quarkus Barista

```bash
cd barista-kafka
mvn compile quarkus:dev
```

#### Node.js Barista

```bash
cd barista-node
npm start
```

# Execute with HTTP

The first part of the demo shows HTTP interactions:

* Barista code: `me.escoffier.quarkus.coffeeshop.BaristaResource`
* CoffeeShop code: `me.escoffier.quarkus.coffeeshop.CoffeeShopResource.http`
* Generated client: `me.escoffier.quarkus.coffeeshop.http.BaristaService`

Order coffees by opening `http://localhost:8080`. Select the HTTP method.

Stop the HTTP Barista, you can't order coffee anymore.

# Execute with Kafka

* Barista code: `me.escoffier.quarkus.coffeeshop.KafkaBarista`: Read from `orders`, write to `queue`
* Bridge in the CoffeeShop: `me.escoffier.quarkus.coffeeshop.messaging.CoffeeShopResource#messaging` just enqueue the orders in a single thread (one counter)
* Get prepared beverages on `me.escoffier.quarkus.coffeeshop.dashboard.BoardResource` and send to SSE

* Open browser to http://localhost:8080/
* Order coffee with Order coffees by opening `http://localhost:8080`. Select the messaging method.

# Baristas do breaks

1. Stop the Kafka barista
1. Continue to enqueue order
1. On the dashboard, the orders are in the "IN QUEUE" state
1. Restart the barista
1. They are processed

# 2 baristas are better

#### Quarkus

1. Build `barista-kafka` with native image:
   ```bash
   mvn package -Pnative
   ```
1. Start a second barista with: 
    ```bash
    ./barista-kafka/target/barista-kafka-1.0-SNAPSHOT-runner -Dquarkus.http.port=9999
    ```
1. Order more coffee

#### Node.js

1. Open a new terminal and run `npm  start` again.

<br />
The dashboard shows that the load is dispatched among the baristas.
