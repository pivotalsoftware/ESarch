#!/usr/bin/env bash

set -e

if [ -n $engineURL ]
then
    export engineURL=https://esrefarch-demo-trading-engine.cfapps.io
    echo -e "Setting URL for the Trading Engine to ${engineURL}"
fi

if [ -n $appURL ]
then
    export appURL=https://esrefarch-demo-trader-app.cfapps.io
    echo -e "Setting URL for the Trading App to ${appURL}"
fi

if [ -n $uiURL ]
then
    export uiURL=https://esrefarch-demo-trader-ui.cfapps.io
    echo -e "Setting URL for the Trader UI to ${uiURL}"
fi

env URL="$engineURL" ./tasks/smoke-test-trading-engine.sh
env URL="$appURL" ./tasks/smoke-test-trader-app.sh
./tasks/e2e-test-both-sides.sh