#!/usr/bin/env sh

set -e +x

pushd source-code
  echo "Testing and Packaging Trading Engine JAR..."
  cd trading-engine
  ../mvnw verify
popd

jar_count=`find source-code/trading-engine/target -type f -name *.jar | wc -l`

if [ $jar_count -gt 1 ]; then
  echo "More than one jar found, don't know which one to deploy. Exiting :("
  exit 1
fi

find source-code/trading-engine/target -type f -name *.jar -exec cp "{}" package-output/trading-engine.jar \;

echo "Done packaging"
exit 0