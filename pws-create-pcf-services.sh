#!/usr/bin/env bash

cf create-service cleardb spark mysql
cf create-service cloudamqp lemur rabbit
cf create-service p-service-registry standard registry
cf create-service p-config-server standard config -c config-server-setup.json
