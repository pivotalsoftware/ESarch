#!/usr/bin/env sh

set -e +x

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Packaging the Trader UI App..."
cd source-code/trader-app-ui
  npm run build
cd $FOLDER

# find build folder and copy it to package-output
find source-code/trader-app-ui/build -type d -name build -exec cp -r "{}"/. package-output/build \;

echo "Done building trader ui"
exit 0