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

	# Create new name for APK as current epoch time + '.apk'
	APP_NAME=`date +%s`; APP_NAME+='.apk';

	# Copy APK to artifacts directory with new name
	cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_ARTIFACTS/$APP_NAME;

	# Upload APK to AWS Device Farm and store returned JSON
	apkUpload=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$APP_NAME --type ANDROID_APP`;

	# Extract ARN from the upload JSON
	apkArn=`echo $apkUpload | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["arn"]'`;

	# Create a new name for JAR as current epoch time + '.jar'
	JAR_NAME = `date +%s`; JAR_NAME+='.jar';

	# Copy JAR to artifacts directory with new name
	cp ./ambassadorsdk-demo/build/intermediates/transforms/jarMerging/debug/jars/1/1f/combined.jar $CIRCLE_ARTIFACTS/$JAR_NAME;

	# Upload JAR to AWS Device Farm and store returned JSON
	jarUpload=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$JAR_NAME --type UIAUTOMATOR_TEST_PACKAGE`;

	# Extract ARN from the upload JSON
	jarArn=`echo $jarUpload | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["arn"]'`;

	echo $apkArn;
	echo $jarArn;
fi
