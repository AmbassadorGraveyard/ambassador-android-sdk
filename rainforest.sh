#!/bin/sh
# Builds a debug APK and tests APK for the demo app and uploads them
# to AWS S3. Runs tests and reports status to GitHub.

# Call abort on exit
trap 'abort' 0;

# Fail on any command error
set -e;

# Get the commit msg
msg=`git log -1 --pretty=%B`;

# Get commit sha for GitHub reporting
sha=`git rev-parse HEAD`;

# Get the branch name
branch=`git rev-parse --abbrev-ref HEAD`;

# If "RunUiTests" in commit msg or branch is master
if [ $msg == *"@RunUiTests"* ] || [ $branch == "master" ]
then

    # Create new name for app APK as current epoch time + '.apk'
    APK_NAME="ambassadorsdk-demo-debug.apk";

    # Copy APK to artifacts directory with new name
    echo "Copying debug apk to $CIRCLE_ARTIFACTS/$APK_NAME";
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_ARTIFACTS/$APK_NAME;

    # Upload Debug APK file to S3
    echo "Requesting upload to device farm";
    aws aws s3 cp --acl public-read $CIRCLE_ARTIFACTS/$APK_NAME s3://ambassador-rainforest/android/debug/DemoApp.apk;

    # Trigger RainforestQA test run
    rainforest run --run-group 1793 --fail-fast --environment-id 5165 --token "$RAINFOREST_TOKEN" --description "Ambassador Android Demo App automated post deploy run" --bg


else
    # Clarify in CircleCI why devicefarm.sh did nothing on this commit.
    echo "Tests not running. To run tests outside of master add @RunUiTests to the commit message.";
fi

# End abort on exit block
trap : 0