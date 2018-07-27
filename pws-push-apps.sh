#!/usr/bin/env bash

# Now build the apps...
./mvnw verify

# Now push the apps...
cf push -f manifest.yml --no-start

# Do these after a 'push' of the apps without starting them...
cf add-network-policy trader-app --destination-app trading-engine

# Now start the apps...
cf start trader-app
cf start trading-engine
