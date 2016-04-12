#!/bin/sh

# fail if err occurs on any step
set -o errexit

if [ "$CIRCLE_BRANCH" != "master" ]
  then
    APK_NAME="rainforest.apk";
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_ARTIFACTS/$APK_NAME;
    curl -H "Authorization: Bearer $DROPBOX_TOKEN" https://api-content.dropbox.com/1/files_put/auto/ -T $CIRCLE_ARTIFACTS/$APK_NAME

    rainforest validate
    rainforest upload --token $RAINFOREST_TOKEN

    GITHUB_PULL_NUMBER=$(echo $CI_PULL_REQUEST | awk -F/ '{print $7}')
    rainforest run all --fg --fail-fast --git-trigger --token "$RAINFOREST_TOKEN" --site-id "$RAINFOREST_SITE" --description "CI run for $CIRCLE_BRANCH" --browsers android_phone_portrait
fi
