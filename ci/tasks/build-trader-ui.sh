#!/usr/bin/env sh

set -e +x

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Packaging the Trader UI App..."
cd source-code/trader-app-ui
  npm install
  npm run build
cd $FOLDER

# copy the build folder to package-output
cp -r source-code/trader-app-ui/build package-output/build
cp -r source-code/trader-app-ui/manifest.yml package-output/build/manifest.yml

ls -la source-code
ls -la source-code/trader-app-ui
ls -la package-output
ls -la package-output/build

echo "Done building trader ui"
exit 0