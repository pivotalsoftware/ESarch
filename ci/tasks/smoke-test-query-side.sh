#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid jq --allow-unauthenticated

# Begin the Smoke-testing...

export RANDOM_NAME=`uuid`
echo "Generated a random company name of: ${RANDOM_NAME}"

export COMPANY_UUID=`curl -X POST -sL -d "${RANDOM_NAME}" -H "Content-Type:application/json" ${URL}/company | jq -r .identifier`
if [ -z $COMPANY_UUID ];
then
    echo -e "\e[31mError. The smoke test has failed, it didn't create a new company!"
    exit 1
else
    echo "The company (${RANDOM_NAME}) was created and assigned a UUID of: ${COMPANY_UUID} by the application"
fi

export COMPANY_NAME=`curl -sL -H "Content-Type: application/json" -X GET ${URL}/orderbook/${COMPANY_UUID} | jq -r '.[]|.companyName'`
if [ "$COMPANY_NAME" != "$RANDOM_NAME" ];
then
    echo -e "\e[31mError. The smoke test has failed, it was unable to find the orderbook for the company with the company-id [$COMPANY_UUID]"
    exit 1
else
    echo "The Orderbook for the company-id ${COMPANY_UUID} lists the company's name as: ${COMPANY_NAME}"
fi

echo -e "\e[32mTRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0