#!/usr/bin/env bash

set -eux
env URL="$engineURL" ./tasks/smoke-test-trading-engine.sh
env URL="$appURL" ./tasks/smoke-test-trader-app.sh
./tasks/e2e-test-both-sides.sh