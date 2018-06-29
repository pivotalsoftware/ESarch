#!/bin/bash

set -ex

pushd source-code
  echo "Fetching Dependencies & Building Code..."
  ./gradlew common-api:assemble > /dev/null

  echo "Running Tests..."
  ./gradlew common-api:test
popd

exit 0
