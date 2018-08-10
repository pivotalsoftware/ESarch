#!/usr/bin/env sh

set -e +x

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Testing and Packaging the trader App JAR..."
cd source-code/trader-app
  mvn verify
cd $FOLDER

# jar_count=`find source-code/trader-app/target -type f -name *.jar | wc -l`

# if [ $jar_count -gt 1 ]; then
#   echo "More than one jar found, don't know which one to deploy. Exiting :("
#   exit 1
# fi

# find source-code/trader-app/target -type f -name *.jar -exec cp "{}" package-output/trader-app.jar \;

find source-code/trader-app/target -type f -name trader-app.jar -exec cp "{}" package-output/trader-app.jar \;

echo "Done packaging"
exit 0