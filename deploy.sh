#!/usr/bin/env sh
# Use this script to package up your code

set -eux


pushd () {
    command pushd "$@" > /dev/null
}

popd () {
    command popd "$@" > /dev/null
}

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

mvn clean package -DskipTests=true


pushd .
cd trading-engine
cf push -f manifest.yml --random-route
popd

pushd .
cd trader-app
cf push -f manifest.yml --random-route
popd

pushd .
cd trader-app-ui
npm install
npm run build
cf push -f manifest.yml --random-route
popd

cf add-network-policy esrefarch-demo-trader-app --destination-app esrefarch-demo-trading-engine
cf add-network-policy esrefarch-demo-trading-engine --destination-app esrefarch-demo-trader-app

