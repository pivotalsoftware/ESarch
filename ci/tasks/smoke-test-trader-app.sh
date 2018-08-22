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

export RANDOM_COMPANY_ID=`uuidgen`
echo "The randomly generated Company Id is: ${RANDOM_COMPANY_ID}"
export RANDOM_COMPANY_NAME=`uuidgen`
echo "The randomly generated Company Name is: ${RANDOM_COMPANY_NAME}"
export CREATE_COMPANY_PAYLOAD="{\"companyId\": \"${RANDOM_COMPANY_ID}\", \"userId\": \"1bae0365-949c-467d-a6f5-4a32ddd96dbb\", \"companyName\": \"${RANDOM_COMPANY_NAME}\", \"companyValue\": \"1337\", \"amountOfShares\": \"42\"}"

export COMPANY_UUID=`curl -X POST -sL -d "${CREATE_COMPANY_PAYLOAD}" -H "Content-Type:application/json" ${URL}/command/CreateCompanyCommand | jq -r ''`
if [ -z ${COMPANY_UUID} ] || [ "$COMPANY_UUID" = "" ];
then
    echo -e "\e[31mError. The smoke test has failed, it didn't create a new company!"
    exit 1
else
    echo "The company (${RANDOM_COMPANY_NAME}) was created, with a UUID of [${COMPANY_UUID}]."
fi

export COMPANY_NAME=`curl -sL -H "Content-Type: application/json" -X GET ${URL}/query/order-book/by-company/${COMPANY_UUID} | jq -r '.[]|.companyName'`
echo "The Company Name  is: ${COMPANY_NAME}"

if [ "$COMPANY_NAME" != "$RANDOM_COMPANY_NAME" ];
then
    echo -e "\e[31mError. The smoke test has failed, it was unable to find the orderbook for the company with the company-id [$COMPANY_UUID]"
    exit 1
else
    echo "The Orderbook for the company-id ${COMPANY_UUID} lists the company's name as: ${COMPANY_NAME}"
fi

echo -e "\e[32mTRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0