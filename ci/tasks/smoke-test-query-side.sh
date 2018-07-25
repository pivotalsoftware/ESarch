#!/usr/bin/env bash

apt-get update && apt-get install -y curl --allow-unauthenticated

#set -ex

# Begin the Integration-testing...

export UUID=`uuid`
if curl -X POST -sL -w %{http_code} -d "${UUID}" "${URL}/company" -o /dev/null | grep "200"
then
    echo "[$URL] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Not showing '200 OK' on [$URL]"
    exit 1
fi

echo -e "\e[32mTRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D "
exit 0