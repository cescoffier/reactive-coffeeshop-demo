#!/usr/bin/env bash
# Change the following url according to the cluster.
export URL="http://coffee-shop-reactive-demo.apps.cluster-vienna-6fd3.vienna-6fd3.open.redhat.com/messaging"
http POST ${URL} name=clement product=iced-latte
http POST ${URL} name=edson product=cappuccino
http POST ${URL} name=rafaele product=espresso
http POST ${URL} name=ken product=americano
http POST ${URL} name=emmanuel product=frappucino
