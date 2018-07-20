#!/usr/bin/env bash

cf ds -f mysql
cf ds -f rabbit
cf ds -f registry
cf ds -f config
