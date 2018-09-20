#!/usr/bin/env sh

set -eux

export FOLDER=`pwd`
echo "The path is ${OLDPATH}"

echo "Packaging the Trader UI App..."

cd source-code/trader-app-ui
  npm install
  npm run build
  cp manifest.yml build/manifest.yml
  cp Staticfile build/Staticfile
cd $FOLDER

echo "package-output/build..."
ls -la package-output/build
echo "package-output/build/static..."
ls -la package-output/build/static

echo "cp -R source-code/trader-app-ui/build package-output"
cp -R source-code/trader-app-ui/build package-output

echo "Done building trader ui"
exit 0