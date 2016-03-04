#!/bin/sh

set -o errexit;

msg=`git log -1 --pretty=%B`;

if [[ $msg == *"@RunUiTests"* ]]
then
    ./gradlew -p ambassadorsdk-demo assembleDebug;
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_TEST_REPORTS/app.apk;
	ret=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_TEST_REPORTS/ambassadorsdk-demo-debug.apk --type ANDROID_APP`;
	echo $ret;
fi
