#!/bin/sh
# Builds a debug APK and tests APK for the demo app and uploads them
# to AWS Device Farm. Runs tests and reports status to GitHub.

# Gets hit on any error before the program aborts.
abort()
{
    # Set a commit fail status for device farm if any error causes the script to abort.
    report_github_status "failure" "A fatal error occurred with device farm test execution.";
    exit 0;
}

# Call abort on exit
trap 'abort' 0;

# Fail on any command error
set -e;

# Get the commit msg
msg=`git log -1 --pretty=%B`;

# Get the branch name
branch=`git rev-parse --abbrev-ref HEAD`;

# Get commit sha for GitHub reporting
sha=`git rev-parse HEAD`;

# Function to report a github commit status
report_github_status() 
{
    echo "{\"state\":\"$1\",\"target_url\":\"$CIRCLE_BUILD_URL\",\"description\":\"$2\",\"context\":\"aws/devicefarm\"}" | curl -d @- https://api.github.com/repos/GetAmbassador/ambassador-android-sdk/statuses/$sha?access_token=$GITHUB_ACCESS_TOKEN;
}

# Takes any number of key arguments and will find the value at that point
# First parameter is json, all subsequent parameters are keys
get_value_from_json()
{
    KEYS_ACCESSOR="";
    for x in $@; do
        VALUE=`echo "${!x}"`;
        KEYS_ACCESSOR+="[\"$VALUE\"]";
    done
    VALUE=`echo $1 | python -c "import json,sys;obj=json.load(sys.stdin);print obj$KEYS_ACCESSOR"`;
    return VALUE;
}

# Uploads a file to a url using curl
upload_file()
{
    curl -T $1 $2;
}

# Function hangs pending upload success or failure
wait_for_upload_success() {
    # Wait for upload success
    while true; do
        # Get upload info from AWS using ARN
        GET_UPLOAD=`aws devicefarm get-upload --arn $1`;

        # Get status from respone JSON
        UPLOAD_STATUS=`echo $GET_UPLOAD| python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["status"]'`;

        # DEBUGGING
        echo $UPLOAD_STATUS;

        # Break loop if success
        if [ $UPLOAD_STATUS == "SUCCEEDED" ]
        then
            break;
        elif [ $UPLOAD_STATUS == "FAILED" ]
        then
            echo $GET_UPLOAD;
            exit 0;
            break;
        fi

        # Wait 3 seconds before checking again
        sleep 3;
    done
}

# If "RunUiTests" in commit msg or branch is master
if [[ $msg == *"@RunUiTests"* ]] || [ $branch == "master" ]
then
    # Get current time for naming
    TIME=`date +%s`;

    # Build the app APK
    echo "Assembling debug APK...";
    ./gradlew -p ambassadorsdk-demo assembleDebug --quiet;

    # Create new name for app APK as current epoch time + '.apk'
    APK_NAME="app$TIME.apk";

    # Copy APK to artifacts directory with new name
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug.apk $CIRCLE_ARTIFACTS/$APK_NAME;

    # Request APK upload to AWS Device Farm and store returned JSON
    APK_UPLOAD=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$APK_NAME --type ANDROID_APP`;

    # Extract ARN from the response JSON
    APK_ARN=`get_value_from_json $APK_UPLOAD "upload" "arn"`;

    # Get the remote url to upload the app APK to
    APK_UPLOAD_URL=`get_value_from_json $APK_UPLOAD "upload" "url"`;

    echo $APK_UPLOAD;

    # Upload APK to AWS Device Farm
    upload_file $CIRCLE_ARTIFACTS/$APK_NAME $APK_UPLOAD_URL;

    # Hang program pending upload success
    wait_for_upload_success $APK_ARN;

    # Build the tests APK
    echo "Assembling tests APK...";
    ./gradlew -p ambassadorsdk-demo assembleAndroidTest --quiet;

    # Create a new name for tests APK as current epoch time + '.apk'
    TESTS_NAME="tests$TIME.apk";

    # Copy APK to artifacts directory with new name
    cp ./ambassadorsdk-demo/build/outputs/apk/ambassadorsdk-demo-debug-androidTest-unaligned.apk $CIRCLE_ARTIFACTS/$TESTS_NAME;

    # Request APK upload to AWS Device Farm and store response JSON
    TESTS_UPLOAD=`aws devicefarm create-upload --project-arn $AWS_PROJECT_ARN --name $CIRCLE_ARTIFACTS/$TESTS_NAME --type INSTRUMENTATION_TEST_PACKAGE`;

    # Extract ARN from the response JSON
    TESTS_ARN=`get_value_from_json $TESTS_UPLOAD "upload" "arn"`

    #Get the remote url to upload the tests APK to
    TESTS_UPLOAD_URL=`echo $APK_UPLOAD | python -c 'import json,sys;obj=json.load(sys.stdin);print obj["upload"]["url"]'`;

    # Upload APK to AWS Device Farm
    upload_file $CIRCLE_ARTIFACTS/$TESTS_NAME $TESTS_UPLOAD_URL;

    # Hang program pending upload success
    wait_for_upload_success $TESTS_ARN;

    # Create a name for the test run
    RUN_NAME="test$TIME";

    # Setup AWS test info
    TEST_INFO="{\"type\":\"INSTRUMENTATION\",\"testPackageArn\":\"$TESTS_ARN\"}";

    # Start AWS test run
    TEST_RESULT=`aws devicefarm schedule-run --project-arn "$AWS_PROJECT_ARN" --app-arn "$APK_ARN" --device-pool-arn "$AWS_DEVICE_POOL_ARN" --name "$RUN_NAME" --test "$TEST_INFO"`

    # Send GitHub status
    report_github_status "success" "This test build succeeded";

    echo $TEST_RESULT
else
    # Clarifiy in CircleCI why devicefarm.sh did nothing on this commit.
    echo "Tests not running. To run tests outside of master add @RunUiTests to the commit message.";

    # Set failure commit status whenever tests not run. This way a merge can only ever happen if UI tests are run and pass.
    report_github_status "failure" "Instrumentation tests not run.";
fi

# End abort on exit block
trap : 0