#!/usr/bin/env bash

cf create-service cleardb spark mysql
cf create-service cloudamqp lemur rabbit
cf create-service p-service-registry standard registry
cf create-service p-config-server standard config -c '{"git": { "uri": "https://github.com/dcaron/esrefarch-config" } }'
cf add-network-policy esrefarch-demo-trader-app --destination-app esrefarch-demo-trading-engine
cf add-network-policy esrefarch-demo-trading-engine --destination-app esrefarch-demo-trader-app

