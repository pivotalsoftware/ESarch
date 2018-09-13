#!/usr/bin/env bash

set -e

if [ -n $engineURL ]
then
    echo -e "Setting a default URL for the Trading Engine..."
    export engineURL=https://esrefarch-demo-trading-engine.cfapps.io
fi

if [ -n $appURL ]
then
    echo -e "Setting a default URL for the Trading App..."
    export appURL=https://esrefarch-demo-trader-app.cfapps.io
fi

if [ -n $uiURL ]
then
    echo -e "Setting a default URL for the Trader UI..."
    export uiURL=https://esrefarch-demo-trader-ui.cfapps.io
fi

env URL="$engineURL" ./tasks/smoke-test-trading-engine.sh
env URL="$appURL" ./tasks/smoke-test-trader-app.sh
./tasks/e2e-test-both-sides.sh