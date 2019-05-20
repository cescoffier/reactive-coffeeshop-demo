#!/usr/bin/env bash
java -Dbarista.name=tom \
     -Dquarkus.http.port=9095 \
     -jar target/barista-kafka-1.0-SNAPSHOT-runner.jar
