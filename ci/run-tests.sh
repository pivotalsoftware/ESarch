#!/usr/bin/env bash

set -eux

export engineURL=http://esrefarch-demo-trading-engine.cfapps.io
export appURL=http://esrefarch-demo-trader-app.cfapps.io

env URL="$engineURL" ./tasks/smoke-test-trading-engine.sh
env URL="$appURL" ./tasks/smoke-test-trader-app.sh
./tasks/e2e-test-both-sides.sh