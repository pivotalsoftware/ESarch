#!/bin/bash

set -ex

pushd source-code
  echo "Fetching Dependencies & Building Code..."
  ./gradlew command-side:assemble > /dev/null

  echo "Running Tests..."
  ./gradlew command-side:test
popd

exit 0
