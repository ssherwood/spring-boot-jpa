#!/bin/bash

set -xe

export NEXT_APP_COLOUR=$(cat ./app-info/next-deployment.txt)
export NEXT_APP_URL=http://$CF_APP-$NEXT_APP_COLOUR.$CF_APP_DOMAIN/

echo "Running integration test..."

curl -s --include -k $NEXT_APP_URL/actuator/health | grep "HTTP/1.1 200 OK"
