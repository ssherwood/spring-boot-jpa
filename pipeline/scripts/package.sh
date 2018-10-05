#!/bin/sh

set -xe
cd git-assets
mvn package
ls target
ls ..
cp target/demo-0.0.1-SNAPSHOT.jar ../mvn-package-output/
