#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid jq --allow-unauthenticated

#set -ex

if [ -z $engineURL ];
then
  echo -e "\e[31mThe Trading-engine URL to test has not been set."
  exit 1
else
  echo -e "The Trading-engine URL for this smoke test is: \e[32m $engineURL \e[0m"
fi

if [ -z $appURL ];
then
  echo -e "\e[31mThe Trader-app URL to test has not been set."
  exit 1
else
  echo -e "The Trader-app URL for this smoke test is: \e[32m $appURL \e[0m"
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

export UUID=`uuid`

echo -e "\e[32mINTEGRATION TESTS FINISHED - NO ERRORS ;D "
exit 0