#!/usr/bin/env bash
# Use this script to pave your PWS instance with the correct services

set -eu

if [ -z ${1+x} ] && [ -z ${2+x} ] && [ -z ${3+x} ] && [ -z ${4+x} ]; then
    echo "Usage on PWS: USERNAME PASSWORD ORG SPACE"
    echo "Usage on other PCF environments: USERNAME PASSWORD ORG SPACE API_ENDPOINT"
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

export PWS="https://api.run.pivotal.io"
if [ -z ${5+x} ]; then
  export API=${PWS}
  export PCF_ENV="PWS"
else
  export API=$5
  export PCF_ENV="PCF"
fi
echo "API_ENDPOINT: ${API}"  


if [ -z ${1+x} ]; then
    echo "I need your ${PCF_ENV} username (email address) to continue."
    exit 1
else
    echo "Username: $1"
fi

if [ -z ${2+x} ]; then
    echo "I need your ${PCF_ENV} password to continue."
    exit 1
else
    echo "Password is {SECRET}."
fi

if [ -z ${3+x} ]; then
    echo "I need your ${PCF_ENV} ORG name to continue."
    exit 1
else
    echo "Org: $3"
fi

if [ -z ${4+x} ]; then
    echo "I need your ${PCF_ENV} SPACE name to continue."
    exit 1
else
    echo "Space: $4"
fi

echo "Logging in to ${PCF_ENV} using the details provided..."
echo $'\n'

cf login -a ${API} -u $1 -p $2 -o $3 -s $4

if [[ $? != 0 ]]; then
  echo "Abort. There was a problem logging in to ${PCF_ENV} with the details given. Please resolve the problem and try again."
  exit 1
else
  echo $'\nWe\'re logged in. Super!\n'
fi

echo $'Now we\'ll create some services using the Cloud Foundry marketplace [#AwesomePCF #NoOps :D ]...\n'

if [ "$PCF_ENV" = "PWS" ]; then
  cf create-service cleardb spark enginedb
  cf create-service cleardb spark appdb
  cf create-service cloudamqp lemur rabbit
  cf create-service p-service-registry trial registry
  cf create-service p-config-server trial config -c config-server-setup.json
else
  cf create-service p.mysql db-small enginedb 
  cf create-service p.mysql db-small appdb
  cf create-service p.rabbitmq single-node-3.7 rabbit
  cf create-service p-service-registry standard registry
  cf create-service p-config-server standard config -c config-server-setup.json
fi

echo $'\nNow we need to wait a few minutes until all five
 services show \"create succeeded\" as their status.' 
echo "Press [CTRL+C] any time to quit (the services will still be created)."
read -p "Press the [Enter] key to watch while the services get created..."

watch -n 1 "cf services"
echo $'\n'
echo "We're done. You can now deploy the Axom Trader applications to ${PCF_ENV}."