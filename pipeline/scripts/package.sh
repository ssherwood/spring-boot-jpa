#!/bin/sh

set -xe
cd git-assets
mvn package
ls target
ls ..
cp target/*-SNAPSHOT.jar ../mvn-package-output/
