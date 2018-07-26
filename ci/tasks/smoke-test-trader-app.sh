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

export RANDOM_NAME=`uuid`
echo "The randomly generated Company Name is: ${RANDOM_NAME}"

export COMPANY_UUID=`curl -X POST -sL -d "${RANDOM_NAME}" -H "Content-Type:application/json" ${URL}/company | jq -r .identifier`
if [ -z $COMPANY_UUID ];
then
    echo -e "\e[31mError. The smoke test has failed, it didn't create a new company!"
    exit 1
else
    echo "The company (${RANDOM_NAME}) was created and assigned a UUID of: ${COMPANY_UUID} by the application"
fi

export COMPANY_NAME=`curl -sL -H "Content-Type: application/json" -X GET ${URL}/orderbook/${COMPANY_UUID} | jq -r '.[]|.companyName'`
echo "The Company Name  is: ${COMPANY_NAME}"

if [ "$COMPANY_NAME" != "$RANDOM_NAME" ];
then
    echo -e "\e[31mError. The smoke test has failed, it was unable to find the orderbook for the company with the company-id [$COMPANY_UUID]"
    exit 1
else
    echo "The Orderbook for the company-id ${COMPANY_UUID} lists the company's name as: ${COMPANY_NAME}"
fi

echo -e "\e[32mTRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0