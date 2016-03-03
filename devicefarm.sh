#!/bin/sh

set -o errexit;

msg=`git log -1 --pretty=%B`;

if [[ $msg == *"@RunUiTests"* ]]
then
	ret=`aws create-upload --project-arn $AWS_PROJECT_ARN $CIRCLE_TEST_REPORTS/ambassadorsdk-demo-debug.apk --type ANDROID_APP`;
	echo $ret;
fi