#!/bin/sh

set -o errexit;

msg=`git log -1 --pretty=%B`;

if [[ $msg == *"@RunUiTests"* ]]
then
    ./gradlew -p ambassadorsdk-demo assembleDebug;
    APP_NAME=`date +%s`; APP_NAME+='.apk';
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_TEST_REPORTS/$APP_NAME;
	ret=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_TEST_REPORTS/ambassadorsdk-demo-debug.apk --type ANDROID_APP`;
	uploadArn=`echo $ret | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["arn"]'`;
fi