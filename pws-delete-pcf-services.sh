#!/usr/bin/env bash

cf d command-side
cf d query-side

cf ds mysql
cf ds rabbit
cf ds registry
cf ds config
