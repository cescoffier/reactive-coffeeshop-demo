#!/usr/bin/env bash
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic orders
kafka-topics --bootstrap-server localhost:9092 --list