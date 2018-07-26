#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid jq --allow-unauthenticated

# Begin the Smoke-testing...

export HEALTH_STATUS=`curl -sL -X GET ${URL}/actuator/health | jq -r .status`
echo "The Health status is: ${HEALTH_STATUS}"

if [ -z $HEALTH_STATUS] || [ $HEALTH_STATUS != "UP"]
then
    echo -e "\e[31mError. The smoke test has failed, the application health check didn't work!"
    exit 1
else
    echo "The health checl status is reporting that ${URL} is ${HEALTH_STATUS}"
fi
echo -e "\e[32mTRADING-ENGINE SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0