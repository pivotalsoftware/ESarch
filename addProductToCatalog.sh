#!/usr/bin/env bash
# For Linux use uuid, for Mac OS use uuidgen...
#export UUID=`uuid`
export UUID=`uuidgen`
export cmdURL="https://command-side-busy-camel.cfapps.io/"
export qryURL="https://query-side-quick-shark.cfapps.io/"
echo "CmdURL= ${cmdURL}"
echo "QryURL= ${qryURL}"
echo "Generated ProductID = ${UUID}"
curl --insecure -H "Content-Type:application/json" -d "{\"id\":\"${UUID}\", \"name\":\"test-${UUID}\"}" ${cmdURL}/add
sleep 2
curl --insecure -s ${qryURL}/products | grep test-${UUID}
