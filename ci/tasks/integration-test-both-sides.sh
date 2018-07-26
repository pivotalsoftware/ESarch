#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid jq --allow-unauthenticated

#set -ex

if [ -z $engineURL ];
then
  echo -e "\e[31mThe Command-side URL to test has not been set."
  exit 1
else
  echo -e "The Command-side URL for this smoke test is: \e[32m $engineURL \e[0m"
fi

if [ -z $appURL ];
then
  echo -e "\e[31mThe Query-side URL to test has not been set."
  exit 1
else
  echo -e "The Query-side URL for this smoke test is: \e[32m $appURL \e[0m"
fi

# Make sure the homepage shows...

if curl -sL -w %{http_code} "$engineURL/info" -o /dev/null | grep "200"
then
    echo "[$engineURL/info] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Command-side unresponsive. Failed to show '200 OK' on [$engineURL/info]"
    exit 1
fi

if curl -sL -w %{http_code} "$appURL/info" -o /dev/null | grep "200"
then
    echo "[$appURL/info] shows 'HTTP/1.1 200 OK' (Expected)."
else
    echo -e "\e[31mError. Query-side unresponsive. Failed to show '200 OK' on [$appURL/info]"
    exit 1
fi

# Begin the Integration-testing...

export UUID=`uuid`

echo -e "\e[32mINTEGRATION TESTS FINISHED - NO ERRORS ;D "
exit 0