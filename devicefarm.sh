#!/bin/sh

# Fail on any command failure
set -o errexit;

# Get the commit msg
msg=`git log -1 --pretty=%B`;

# If "RunUiTests" in commit msg
if [[ $msg == *"@RunUiTests"* ]]
then
	# Build the APK
    ./gradlew -p ambassadorsdk-demo assembleDebug;

    # Rename APK to current epoch time
    APP_NAME=`date +%s`; APP_NAME+='.apk';

    # Copy APK to artifacts directory with new name
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_ARTIFACTS/$APP_NAME;

    # Upload APK to AWS Device Farm and store returned JSON
	ret=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$APP_NAME --type ANDROID_APP`;

	# Extract ARN from the upload JSON
	uploadArn=`echo $ret | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["arn"]'`;
fi
