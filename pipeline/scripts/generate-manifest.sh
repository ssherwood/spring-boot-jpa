#!/bin/sh

set -xe

# Copy template file
cp ./git-assets/pipeline/templates/manifest-template.yml ./manifest-output/manifest.yml

# Replace variable names (using % as deliminator to avoid having to mess with / path variable)
sed -i "s%APPNAME%$CF_APP-$(cat ./app-info/next-deployment.txt)%" ./manifest-output/manifest.yml
sed -i "s%APPPATH%$APP_PACKAGE%" ./manifest-output/manifest.yml
sed -i "s%APPSERVICES%$(cat ./app-info/services.txt)%" ./manifest-output/manifest.yml
sed -i "s%APPSCALE%$(cat ./app-info/scale.txt)%" ./manifest-output/manifest.yml

# Bit of debugging
cat ./manifest-output/manifest.yml
ls