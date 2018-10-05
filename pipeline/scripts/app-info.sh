#!/bin/bash
# Get a load of info about an app including its current scale, services and colour (blue or green)

set -xe

# Login to PCF
cf api $CF_API --skip-ssl-validation

# Don't echo password
set +x
echo "Logging in to PCF as $CF_USER (org: $CF_ORG, space: $CF_SPACE)"
cf login -u $CF_USER -p $CF_PWD -o "$CF_ORG" -s "$CF_SPACE"
set -x

cf apps

set +ex

# Get app colour
CURR_APP_COLOUR=$(cf apps | grep "$CF_APP.$CF_APP_DOMAIN" | awk '{print $1}')
if [[ $CURR_APP_COLOUR =~ .*green.* ]]; then
  echo "green" > ./app-info/current-app.txt
  echo "blue" > ./app-info/next-deployment.txt
else
  echo "blue" > ./app-info/current-app.txt
  echo "green" > ./app-info/next-deployment.txt
fi

APP_NAME=$CURR_APP_COLOUR

echo APP_NAME = $APP_NAME

# Get list of services
echo $(echo $(cf services | grep $APP_NAME | awk '{print $1}')|sed 's/ /, /g') > ./app-info/services.txt
echo Services: $(cat ./app-info/services.txt)

# Get app scale
echo $(cf app $APP_NAME | grep instances: | awk '{print $2}' | awk -F/ '{print $2}') > ./app-info/scale.txt
echo Scale: $(cat ./app-info/scale.txt)

set -xe

echo "Current app is: $(cat ./app-info/current-app.txt)"
echo "New version of app will be: $(cat ./app-info/next-deployment.txt)"
