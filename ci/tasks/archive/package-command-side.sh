#!/usr/bin/env bash

set -e +x

pushd source-code
  echo "Packaging JAR"
  ./gradlew command-side:assemble
popd

jar_count=`find source-code/command-side/build/libs -type f -name *.jar | wc -l`

if [ $jar_count -gt 1 ]; then
  echo "More than one jar found, don't know which one to deploy. Exiting :("
  exit 1
fi

find source-code/command-side/build/libs -type f -name *.jar -exec cp "{}" package-output/pcf-command-side.jar \;

echo "Done packaging"
exit 0