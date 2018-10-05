#!/bin/bash

set -xe

# Login to PCF
cf api $CF_API --skip-ssl-validation

# Don't echo password
set +x
echo "Logging in to PCF as $CF_USER (org: $CF_ORG, space: $CF_SPACE)"
cf login -u $CF_USER -p $CF_PWD -o "$CF_ORG" -s "$CF_SPACE"
set -x

cf apps

set +e

cf routes

# Map route to "next" app
export NEXT_APP_COLOUR=$(cat ./app-info/next-deployment.txt)
echo "Mapping main app route to point to $NEXT_APP_COLOUR instance"
cf map-route $CF_APP-$NEXT_APP_COLOUR $CF_APP_DOMAIN --hostname $CF_APP

if [ $? = 0 ]; then
	# Delete old App
	export OLD_APP_COLOUR=$(cat ./app-info/current-app.txt)
	cf delete $CF_APP-$OLD_APP_COLOUR -f
	echo "Apps and Routes updated"
	cf apps
	cf routes
else
	exit 1
fi


