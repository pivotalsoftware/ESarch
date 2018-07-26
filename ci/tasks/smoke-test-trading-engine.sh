#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid-runtime jq --allow-unauthenticated

set -eux

echo "Smoke Testing the Trading Engine using the URL: ${URL}"

# Begin the Smoke-testing...

export HEALTH_STATUS=`curl -sL -X GET ${URL}/actuator/health | jq -r .status`
if [ -z $HEALTH_STATUS ] || [ "$HEALTH_STATUS" != "UP" ]
then
    echo -e "\e[31mError. The smoke test has failed, the application health check didn't work!"
    exit 1
else
    echo "The health check status is reporting that ${URL} is ${HEALTH_STATUS}"
fi
echo -e "\e[32mTRADING-ENGINE SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0