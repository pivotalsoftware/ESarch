#!/usr/bin/env bash

# Pave your PWS space with the required services
# You'll need to create the file 'private-config-server-setup.json')

cf create-service cleardb spark mysql
cf create-service cloudamqp lemur rabbit
cf create-service p-service-registry trial registry
cf create-service p-config-server trial config -c private-config-server-setup.json

# The instructions above need several minutes
# It takes time for the Config and the Registry to be created (these are async)
