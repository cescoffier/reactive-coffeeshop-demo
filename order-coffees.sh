#!/usr/bin/env bash
# Change the following url according to the cluster.
export URL=http://$(oc get routes | grep coffee-shop | awk -F ' ' '{print $2}')/messaging
http POST ${URL} name=clement product=iced-latte
http POST ${URL} name=edson product=cappuccino
http POST ${URL} name=rafaele product=espresso
http POST ${URL} name=ken product=americano
http POST ${URL} name=emmanuel product=frappucino
