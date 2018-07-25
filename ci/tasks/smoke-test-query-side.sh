#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid jq --allow-unauthenticated

#set -ex

# Begin the Integration-testing...

export RANDOM_NAME=`uuid`
export COMPANY_UUID=`curl -X POST -sL -d "${RANDOM_NAME}" -H "Content-Type:application/json" ${URL}/company | jq -r .identifier`


if [ "$COMPANY_UUID" != "" ]
then
    echo "[$URL/company] has created a company called $RANDOM_NAME with ID $COMPANY_UUID (as expected)."
else
    echo -e "\e[31mError. Didn't create a new company!"
    exit 1
fi

export COMPANY_NAME=`curl -sL -H "Content-Type: application/json" -X GET ${URL}/orderbook/${COMPANY_UUID} | jq -r '.[]|.companyName'
if [ "$COMPANY_NAME" = "$RANDOM_NAME" ]
then
    echo "[$URL/orderbook/$COMPANY_UUID] Has been found (as expected)."
else
    echo -e "\e[31mError. Unable to find the orderbook for company [$RANDOM_NAME]"
    exit 1
fi

echo -e "\e[32mTRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0