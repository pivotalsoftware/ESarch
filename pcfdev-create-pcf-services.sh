#!/usr/bin/env bash

cf create-service p-mysql 512mb mysql
cf create-service p-rabbitmq standard rabbit
cf create-service p-service-registry standard registry
cf create-service p-config-server standard config -c config-server-setup.json