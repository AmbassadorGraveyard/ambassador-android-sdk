#!/bin/sh

# Gets hit on any error before the program aborts.
abort()
{
	# Set a commit fail status for device farm if any error causes the script to abort.
	echo '{"state":"failure","target_url":"http://google.com","description":"An error occurred with device farm execution.","context":"aws/devicefarm"}' | curl -d @- https://api.github.com/repos/GetAmbassador/ambassador-android-sdk/statuses/$sha?access_token=$GITHUB_ACCESS_TOKEN;
	exit 0;
}

trap 'abort' 0;

# Fail on any command error
set -e;

# Get the commit msg
msg=`git log -1 --pretty=%B`;

# Get the branch name
branch=`git rev-parse --abbrev-ref HEAD`;

# Get commit sha for GitHub reporting
sha=`git rev-parse HEAD`;

# If "RunUiTests" in commit msg
if [[ $msg == *"@RunUiTests"* ]] || [ $branch == "master" ]
then
	# Get current time for naming
	TIME=`date +%s`;

	# Build the app APK
	./gradlew -p ambassadorsdk-demo assembleDebug;

	# Create new name for app APK as current epoch time + '.apk'
	APP_NAME=$TIME; APP_NAME+='.apk';

	# Copy APK to artifacts directory with new name
	cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_ARTIFACTS/$APP_NAME;

	# Upload APK to AWS Device Farm and store returned JSON
	APK_UPLOAD=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$APP_NAME --type ANDROID_APP`;

	# Extract ARN from the upload JSON
	APK_ARN=`echo $APK_UPLOAD | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["arn"]'`;

	# Build the tests APK
	./gradlew -p ambassadorsdk-demo assembleAndroidTest;

	# Create a new name for tests APK as current epoch time + '.apk'
	TESTS_NAME=$TIME; TESTS_NAME+='_tests.apk';

	# Copy APK to artifacts directory with new name
	cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug-androidTest-unaligned.apk $CIRCLE_ARTIFACTS/$TESTS_NAME;

	# Upload APK to AWS Device Farm and store returned JSON
	TESTS_UPLOAD=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$TESTS_NAME --type INSTRUMENTATION_TEST_PACKAGE`;

	# Extract ARN from the upload JSON
	TESTS_ARN=`echo $TESTS_UPLOAD | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["arn"]'`;

	# Create a name for the test run
	RUN_NAME="test$TIME";

	# Setup AWS test info
	TEST_INFO="{\"type\":\"INSTRUMENTATION\",\"testPackageArn\":\"$TESTS_ARN\"}";

	# Start AWS test run
	TEST_RESULT=`aws devicefarm schedule-run --project-arn "$AWS_PROJECT_ARN" --app-arn "$APK_ARN" --device-pool-arn "$AWS_DEVICE_POOL_ARN" --name "$RUN_NAME" --test "$TEST_INFO"`

	# Send GitHub status
	STATUS="success";
	TARGET_URL="http://google.com";
	DESCRIPTION="This test build succeeded";
	echo "{\"state\":\"$STATUS\",\"target_url\":\"$TARGET_URL\",\"description\":\"$DESCRIPTION\",\"context\":\"aws/devicefarm\"}" | curl -d @- https://api.github.com/repos/GetAmbassador/ambassador-android-sdk/statuses/$sha?access_token=$GITHUB_ACCESS_TOKEN;

	echo $TEST_RESULT
else
	# Clarifiy in CircleCI why devicefarm.sh did nothing on this commit.
	echo "Tests not running. To run tests outside of master add @RunUiTests to the commit message.";

	# Set failure commit status whenever tests not run. This way a merge can only ever happen if UI tests are run and pass.
	echo '{"state":"failure","target_url":"http://google.com","description":"Instrumentation tests not run.","context":"aws/devicefarm"}' | curl -d @- https://api.github.com/repos/GetAmbassador/ambassador-android-sdk/statuses/$sha?access_token=$GITHUB_ACCESS_TOKEN;
fi

trap : 0