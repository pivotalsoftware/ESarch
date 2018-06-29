#!/bin/bash

set -ex

pushd source-code
  echo "Fetching Dependencies & Building Code..."
  ./gradlew query-side:assemble > /dev/null

  echo "Running Tests..."
  ./gradlew query-side:test
popd

exit 0
