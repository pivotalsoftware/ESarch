#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid-runtime jq --allow-unauthenticated

set -eu

echo "Smoke Testing the Trader App using the URL: ${URL}"

# Begin the Smoke-testing...

export HEALTH_STATUS=`curl -sL -X GET ${URL}/actuator/health | jq -r .status`
if [ -z ${HEALTH_STATUS} ] || [ "$HEALTH_STATUS" != "UP" ]
then
    echo -e "\e[31mError. The smoke test has failed, the application health check didn't work!"
    exit 1
else
    echo "The health check status is reporting that ${URL} is ${HEALTH_STATUS}"
fi

export API=`curl -sL -X GET ${URL}/command/api | jq '. | length'`
if [ -z ${API} ] || [[ ${API} < 1 ]]
then
    echo -e "\e[31mError. The smoke test has failed, the application API command count was too low!"
    exit 1
else
    echo "The API check status is reporting that there are ${API} commands available."
fi

echo -e "\e[32mTRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0