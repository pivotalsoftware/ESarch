#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid-runtime jq --allow-unauthenticated

set -eux

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

# Make sure the homepage shows...

if curl -sL -w %{http_code} "$engineURL/actuator/info" -o /dev/null | grep "200"
then
    echo "[$engineURL/info] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Trading-engine is unresponsive. Failed to show '200 OK' on [$engineURL/info]"
    exit 1
fi

if curl -sL -w %{http_code} "$appURL/actuator/info" -o /dev/null | grep "200"
then
    echo "[$appURL/info] shows 'HTTP/1.1 200 OK' (Expected)."
else
    echo -e "\e[31mError. Trader-app is unresponsive. Failed to show '200 OK' on [$appURL/info]"
    exit 1
fi

# Begin the Integration-testing...

export MESSAGE=`uuidgen`

# Start by testing for a 200 OK response code from the echo function...

if curl -sL -w %{http_code} "$appURL/echo/$MESSAGE" -o /dev/null | grep "200"
then
    echo "[$appURL/echo/{message}] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Trader-app 'echo' is unresponsive. Failed to show '200 OK' on [$appURL/echo]"
    exit 1
fi

# Next test the content of the echo reply was as expected...

export REPLY=`curl -X GET -sL ${appURL}/echo/${MESSAGE}`
if [ -z $REPLY ] || [ $REPLY =~ .*You said: $MESSAGE.* ]
then
  echo -e "\e[31mError. [${appURL}/echo/${MESSAGE}] didn't return a valid response [$REPLY]"
  exit 1
else
  echo "[${appURL}/echo/${MESSAGE}] returned a response that contained our expected message."
  echo "The full response was: ${REPLY}"
fi

echo -e "\e[32mINTEGRATION TESTS FINISHED - NO ERRORS ;D "
exit 0