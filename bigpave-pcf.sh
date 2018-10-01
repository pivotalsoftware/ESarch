#!/usr/bin/env bash
# Use this script to pave the required services on a PCF environment other than PWS (such as an enterprise-deployed cluster)

cf create-service p.mysql db-small enginedb 
cf create-service p.mysql db-small appdb
cf create-service p.rabbitmq single-node-3.7 rabbit
cf create-service p-service-registry standard registry
cf create-service p-config-server standard config -c config-server-setup.json

