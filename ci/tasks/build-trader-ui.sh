#!/usr/bin/env sh

set -e +x

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Packaging the Trader UI App..."
cd source-code/trader-app-ui
  npm install
  npm run build
  # copy the build folder to package-output
  cp -R ./build package-output/build
cd $FOLDER

ls -la source-code/trader-app-ui
ls -la package-output/build

echo "Done building trader ui"
exit 0