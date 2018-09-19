#!/usr/bin/env bash

# apt-get update && apt-get install -y curl uuid-runtime jq --allow-unauthenticated

set -eu

if [ -z $engineURL ];
then
  echo -e "\e[31mThe Trading-engine URL to test has not been set."
  exit 1
else
  echo -e "The Trading-engine URL for this e2e test is: \e[32m $engineURL \e[0m"
fi

if [ -z $appURL ];
then
  echo -e "\e[31mThe Trader-app URL to test has not been set."
  exit 1
else
  echo -e "The Trader-app URL for this e2e test is: \e[32m $appURL \e[0m"
fi

if [ -z $uiURL ];
then
  echo -e "\e[31mThe Trader-UI URL to test has not been set."
  exit 1
else
  echo -e "The Trader-UI URL for this e2e test is: \e[32m $uiURL \e[0m"
fi

# Make sure the apps are healthy first...

export ENGINE_HEALTH_STATUS=`curl -sL -X GET ${engineURL}/actuator/health | jq -r .status`
if [ -z $ENGINE_HEALTH_STATUS ] || [ "$ENGINE_HEALTH_STATUS" != "UP" ]
then
    echo -e "\e[31mError. The e2e test has failed, the application health check didn't work!"
    exit 1
else
    echo "The health check status is reporting that ${engineURL} is ${ENGINE_HEALTH_STATUS}"
fi

export APP_HEALTH_STATUS=`curl -sL -X GET ${appURL}/actuator/health | jq -r .status`
if [ -z $APP_HEALTH_STATUS ] || [ "$APP_HEALTH_STATUS" != "UP" ]
then
    echo -e "\e[31mError. The e2e test has failed, the application health check didn't work!"
    exit 1
else
    echo "The health check status is reporting that ${appURL} is ${APP_HEALTH_STATUS}"
fi

# Make sure the info page shows on the Trading Engine...

if curl -sL -w %{http_code} "$engineURL/actuator/info" -o /dev/null | grep "200"
then
    echo "[$engineURL/info] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Trading-engine is unresponsive. Failed to show '200 OK' on [$engineURL/info]"
    exit 1
fi

# Make sure the info page shows on the Trading App...

if curl -sL -w %{http_code} "$appURL/actuator/info" -o /dev/null | grep "200"
then
    echo "[$appURL/info] shows 'HTTP/1.1 200 OK' (Expected)."
else
    echo -e "\e[31mError. Trader-app is unresponsive. Failed to show '200 OK' on [$appURL/info]"
    exit 1
fi

# Make sure the info page shows on the Trading UI...

if curl -sL -w %{http_code} "$uiURL" -o /dev/null | grep "200"
then
    echo "[$uiURL] (Trader-UI) shows 'HTTP/1.1 200 OK' (Expected)."
else
    echo -e "\e[31mError. Trader-UI is unresponsive. Failed to show '200 OK' on [$uiURL]"
    exit 1
fi

# Initialise the data if it hasn't been done already...

export DATA=`curl -sL -X POST ${appURL}/actuator/data-initializer`
if [ -z ${DATA} ] || [[ ${DATA} != true ]]
then
    echo -e "\e[31mError. The e2e test has failed, the application's data could not be initialised!"
    exit 1
else
    echo -e "The DATA initializer is reporting data initialisation = $DATA."
fi

# Begin the Integration-testing...

# Start by testing for a 200 OK response code from the command-api function...

if curl -sL -w %{http_code} "$appURL/command/api" -o /dev/null | grep "200"
then
    echo "[$appURL/command/api] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Trader-app 'command-api' is unresponsive. Failed to show '200 OK' on [$appURL/command/api]"
    exit 1
fi

# That a new company can be created...

export RANDOM_COMPANY_NAME=`uuidgen`
export CREATE_COMPANY_PAYLOAD="{\"companyName\": \"${RANDOM_COMPANY_NAME}\", \"companyValue\": \"1337\", \"amountOfShares\": \"42\"}"
export COMPANY_UUID=`curl -X POST -sL -d "${CREATE_COMPANY_PAYLOAD}" -H "Content-Type:application/json" ${appURL}/command/CreateCompanyCommand | jq -r ''`

echo "The randomly generated Company Name for the e2e test is: ${RANDOM_COMPANY_NAME}"
echo "The Company Payload for the e2e test is: ${CREATE_COMPANY_PAYLOAD}"
echo "The Company UUID returned for the e2e test is: ${COMPANY_UUID}"

if [ -z ${COMPANY_UUID} ] || [ "$COMPANY_UUID" = "" ];
then
   echo -e "\e[31mError. The e2e test has failed, it didn't create a new company!"
   exit 1
else
   echo "The company (${RANDOM_COMPANY_NAME}) was created, with a UUID of [${COMPANY_UUID}]."
fi

export COMPANY_NAME=`curl -sL -H "Content-Type: application/json" -X GET ${appURL}/query/order-book/by-company/${COMPANY_UUID} | jq -r '.[]|.companyName'`
echo "The Company Name  is: ${COMPANY_NAME}"

if [ "$COMPANY_NAME" != "$RANDOM_COMPANY_NAME" ];
then
   echo -e "\e[31mError. The e2e test has failed, it was unable to find the orderbook for the company with the company-id [$COMPANY_UUID]"
   exit 1
else
   echo "The OrderBook for the company-id ${COMPANY_UUID} lists the company's name as: ${COMPANY_NAME}"
fi

echo -e "\e[32mINTEGRATION TESTS FINISHED - NO ERRORS ;D "
exit 0

