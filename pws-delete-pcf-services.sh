#!/usr/bin/env bash

cf us esrefarch-demo-trader-app mysql
cf us esrefarch-demo-trading-engine mysql
cf us esrefarch-demo-trader-app rabbit
cf us esrefarch-demo-trading-engine rabbit

cf ds -f mysql
cf ds -f rabbit
cf ds -f registry
cf ds -f config
