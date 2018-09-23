#!/usr/bin/env bash
# Use this script to unpave your PWS space
#
#set -eux
#
#if [ -z ${1+x} ] && [ -z ${2+x} ] && [ -z ${3+x} ] && [ -z ${4+x} ]; then
#    echo "Usage: USERNAME PASSWORD ORG SPACE"
#    exit 1
#fi
#
#export CF='command -v cf'
#if [[ $? != 0 ]]; then
#  echo "I don't think the CF command line client (cf-cli) is installed."
#  echo "Please see the instructions here: https://docs.cloudfoundry.org/cf-cli/install-go-cli.html"
#  exit 1
#else
#  echo $'\nYou have the CF command line client already; that\'s awesome!\n'
#fi
#
#export API="https://api.run.pivotal.io"
#echo "API: ${API}"
#
#if [ -z ${1+x} ]; then
#    echo "I need your PWS username (email address) to continue."
#    exit 1
#else
#    echo "Username: $1"
#fi
#
#if [ -z ${2+x} ]; then
#    echo "I need your PWS password to continue."
#    exit 1
#else
#    echo "Password is {SECRET}."
#fi
#
#if [ -z ${3+x} ]; then
#    echo "I need your PWS ORG name to continue."
#    exit 1
#else
#    echo "Org: $3"
#fi
#
#if [ -z ${4+x} ]; then
#    echo "I need your PWS SPACE name to continue."
#    exit 1
#else
#    echo "Space: $4"
#fi
#
#echo $'Logging in to PWS using the details provided...\n'
#
#cf login -a ${API} -u $1 -p $2 -o $3 -s $4
#
#if [[ $? != 0 ]]; then
#  echo "Abort. There was a problem logging in to PWS with the details given. Please resolve the problem and try again."
#  exit 1
#else
#  echo $'\nWe\'re logged in. Super!\n'
#fi
#
#echo $'Now we\'ll create some services using the Cloud Foundry marketplace [#Awesome #NoOps]...\n'

cf stop esrefarch-demo-trader-app
cf stop esrefarch-demo-trader-ui
cf stop esrefarch-demo-trading-engine

cf us esrefarch-demo-trader-app registry
cf us esrefarch-demo-trading-engine registry
cf us esrefarch-demo-trader-app config
cf us esrefarch-demo-trading-engine config
cf us esrefarch-demo-trader-ui config
cf us esrefarch-demo-trader-app rabbit
cf us esrefarch-demo-trading-engine rabbit
cf us esrefarch-demo-trading-engine enginedb
cf us esrefarch-demo-trader-app appdb

cf ds rabbit -f
cf ds config -f
cf ds registry -f
cf ds appdb -f
cf ds enginedb -f

cf delete esrefarch-demo-trader-app -f
cf delete esrefarch-demo-trader-ui -f
cf delete esrefarch-demo-trading-engine -f

exit 0

# cf bs esrefarch-demo-trader-app registry
# cf bs esrefarch-demo-trading-engine registry
# cf bs esrefarch-demo-trader-app config
# cf bs esrefarch-demo-trading-engine config
# cf bs esrefarch-demo-trader-ui config
# cf bs esrefarch-demo-trader-app rabbit
# cf bs esrefarch-demo-trading-engine rabbit
# cf bs esrefarch-demo-trading-engine enginedb
# cf bs esrefarch-demo-trader-app appdb

# cf start esrefarch-demo-trader-app
# cf start esrefarch-demo-trader-ui
# cf start esrefarch-demo-trading-engine