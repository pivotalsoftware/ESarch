#!/usr/bin/env bash

apt-get update && apt-get install -y curl uuid --allow-unauthenticated

#set -ex

if [ -z $cmdURL ];
then
  echo -e "\e[31mThe Command-side URL to test has not been set."
  exit 1
else
  echo -e "The Command-side URL for this smoke test is: \e[32m $cmdURL \e[0m"
fi

if [ -z $qryURL ];
then
  echo -e "\e[31mThe Query-side URL to test has not been set."
  exit 1
else
  echo -e "The Query-side URL for this smoke test is: \e[32m $qryURL \e[0m"
fi

# Make sure the homepage shows...

if curl -sL -w %{http_code} "$cmdURL/info" -o /dev/null | grep "200"
then
    echo "[$cmdURL/info] shows 'HTTP/1.1 200 OK' (as expected)."
else
    echo -e "\e[31mError. Command-side unresponsive. Failed to show '200 OK' on [$cmdURL/info]"
    exit 1
fi

if curl -sL -w %{http_code} "$qryURL/info" -o /dev/null | grep "200"
then
    echo "[$qryURL/info] shows 'HTTP/1.1 200 OK' (Expected)."
else
    echo -e "\e[31mError. Query-side unresponsive. Failed to show '200 OK' on [$qryURL/info]"
    exit 1
fi

# Begin the Integration-testing...

export UUID=`uuid`
export PRODUCT_ID=`curl -s -H "Content-Type:application/json" -d "{\"id\":\"${UUID}\", \"name\":\"test-${UUID}\"}" ${cmdURL}/add`

# Add a Product using a command...

if [ "$PRODUCT_ID" = "$UUID" ]
then
    echo -e "[$cmdURL/add] for Product $UUID returned $PRODUCT_ID (Expected)."
else
    echo -e "\e[31mError. Add Command Failed. The Product ID $UUID wasn't returned as expected (got $PRODUCT_ID)! \e[0m"
    exit 1
fi

sleep 5

# List all the products, expect our new Product to be there in the list...

if curl -s ${qryURL}/products | grep test-${PRODUCT_ID}
then
    echo "[$qryURL/products] had the product '$PRODUCT_ID' in the product list (Expected)."
else
    echo -e "\e[31mError. Query-side failed to show product '$PRODUCT_ID' on [$qryURL]"
    exit 1
fi

echo -e "\e[32mINTEGRATION TESTS FINISHED - NO ERRORS ;D "
exit 0