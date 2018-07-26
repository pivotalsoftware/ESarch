#!/usr/bin/env bash

set -e +x

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Testing and Packaging the Trading Engine JAR..."
cd source-code/trading-engine
  mvn verify
cd $FOLDER

jar_count=`find source-code/trading-engine/target -type f -name *.jar | wc -l`

if [ $jar_count -gt 1 ]; then
  echo "More than one jar found, don't know which one to deploy. Exiting :("
  exit 1
fi

find source-code/trading-engine/target -type f -name *.jar -exec cp "{}" package-output/trading-engine.jar \;

echo "Done packaging"
exit 0