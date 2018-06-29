#!/usr/bin/env bash

apt-get update && apt-get install -y curl --allow-unauthenticated

#set -ex

if [ -z $URL ];
then
  echo -e "\e[31mThe URL to test has not been set."
  exit 1
else
  echo -e "The base URL for this smoke test is: \e[32m $URL \e[0m"
fi

# Make sure the homepage shows...

if curl -sL -w %{http_code} "$URL" -o /dev/null | grep "200"
then
    echo "[$URL] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Not showing '200 OK' on [$URL]"
    exit 1
fi

# Make sure the /info endpoint shows...

if curl -sL -w %{http_code} "$URL/info" -o /dev/null | grep "200"
then
    echo "[$URL/info] shows 'HTTP Status 200 OK' (as expected)."
else
    echo -e "\e[31mError. Not showing '200 OK' on [$URL/info]"
    exit 1
fi

# Make sure the /env endpoint shows...

if curl -sL -w %{http_code} "$URL/env" -o /dev/null | grep "200"
then
    echo "[$URL/env] shows 'HTTP Status 200 OK' (as expected)."
else
    echo -e "\e[31mError. Not showing '200 OK' on [$URL/env]"
    exit 1
fi

# Make sure the homepage shows there is a Config Service bound...

if curl -s "$URL/health" | grep "configServer"
then
    echo "The website [$URL/health] shows 'configServer' (as expected)."
else
    echo -e "\e[31mError. Not showing 'configServer' on [$URL/health]"
    exit 1
fi

# Make sure the homepage shows there is a DB Service bound...

if curl -s "$URL/health" | grep "db"
then
    echo "The website [$URL/health] shows 'db' (as expected)."
else
    echo -e "\e[31mError. Not showing 'db' on [$URL/health]"
    exit 1
fi

# Make sure the homepage shows there is a Registry Service bound...

if curl -s "$URL/health" | grep "eureka"
then
    echo "The website [$URL/health] shows 'eureka' (as expected)."
else
    echo -e "\e[31mError. Not showing 'eureka' on [$URL/health]"
    exit 1
fi

# Make sure the homepage shows there is a Registry Service bound...

if curl -s "$URL/health" | grep "rabbit"
then
    echo "The website [$URL/health] shows 'rabbit' (as expected)."
else
    echo -e "\e[31mError. Not showing 'rabbit' on [$URL/health]"
    exit 1
fi

echo -e "\e[32mQUERY-SIDE SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0