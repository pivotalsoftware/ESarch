#!/usr/bin/env bash
# Use this script to pave your PWS instance with the correct services

set -eu

if [ -z ${1+x} ] && [ -z ${2+x} ] && [ -z ${3+x} ] && [ -z ${4+x} ]; then
   echo "Usage: USERNAME PASSWORD ORG SPACE"
   exit 1
fi

export CF='command -v cf'
if [[ $? != 0 ]]; then
 echo "I don't think the CF command line client (cf-cli) is installed."
 echo "Please see the instructions here: https://docs.cloudfoundry.org/cf-cli/install-go-cli.html"
 exit 1
else
 echo $'\nYou have the CF command line client already; that\'s awesome!\n'
fi

export API="https://api.run.pivotal.io"
echo "API: ${API}"

if [ -z ${1+x} ]; then
   echo "I need your PWS username (email address) to continue."
   exit 1
else
   echo "Username: $1"
fi

if [ -z ${2+x} ]; then
   echo "I need your PWS password to continue."
   exit 1
else
   echo "Password is {SECRET}."
fi

if [ -z ${3+x} ]; then
   echo "I need your PWS ORG name to continue."
   exit 1
else
   echo "Org: $3"
fi

if [ -z ${4+x} ]; then
   echo "I need your PWS SPACE name to continue."
   exit 1
else
   echo "Space: $4"
fi

echo $'Logging in to PWS using the details provided...\n'

cf login -a ${API} -u $1 -p $2 -o $3 -s $4

if [[ $? != 0 ]]; then
 echo "Abort. There was a problem logging in to PWS with the details given. Please resolve the problem and try again."
 exit 1
else
 echo $'\nWe\'re logged in. Super!\n'
fi

cf stop esrefarch-demo-trader-ui
cf stop esrefarch-demo-trader-app
cf stop esrefarch-demo-trading-engine

# cf unbind-service APP_NAME SERVICE_INSTANCE
cf unbind-service esrefarch-demo-trader-app config
cf unbind-service esrefarch-demo-trader-app registry
cf unbind-service esrefarch-demo-trading-engine config
cf unbind-service esrefarch-demo-trading-engine registry

cf delete-service registry
cf delete-service config

# cf bind-service APP_NAME SERVICE_INSTANCE
cf create-service p-service-registry trial registry
cf create-service p-config-server trial config -c config-server-setup.json

echo $'\nNow we need to wait a few minutes until all the services show \"create succeeded\" as their status.'
echo "This may take while, so I'll start a watch. Press CTRL+C any time to quit (the services will still be created)."
read -p "Press [Enter] to start watching..."
watch -n 1 "cf services"
echo $'\nWe\'re done. You can now use the push.sh script provided to push your apps.'

cf bind-service esrefarch-demo-trader-app config
cf bind-service esrefarch-demo-trader-app registry
cf bind-service esrefarch-demo-trading-engine config
cf bind-service esrefarch-demo-trading-engine registry

cf restage esrefarch-demo-trader-ui
cf restage esrefarch-demo-trader-app
cf restage esrefarch-demo-trading-engine


