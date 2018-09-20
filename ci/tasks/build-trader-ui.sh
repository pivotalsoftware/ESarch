#!/usr/bin/env sh

set -eux

export FOLDER=`pwd`
echo "The path is ${FOLDER}"

echo "Packaging the Trader UI App..."

cd source-code/trader-app-ui
  npm install
  npm run build
  cp manifest.yml ${FOLDER}/package-output/manifest.yml
  cp Staticfile build/Staticfile
  cp -R build ${FOLDER}/package-output
cd $FOLDER

ls -la package-output
ls -la package-output/build
ls -la package-output/build/static

echo "Done building trader ui"
exit 0