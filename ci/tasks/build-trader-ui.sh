#!/usr/bin/env sh

set -e +x

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Packaging the Trader UI App..."

cd source-code/trader-app-ui
  npm install
  npm run build
  cp manifest.yml build/manifest.yml
cd $FOLDER

mkdir package-output/build
cp -R source-code/trader-app-ui/build package-output/build

ls -la source-code/trader-app-ui
ls -la package-output/build

echo "Done building trader ui"
exit 0