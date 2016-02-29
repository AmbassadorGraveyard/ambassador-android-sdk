#!/bin/sh

# fail if err occurs on any step
set -o errexit

if [ "$CIRCLE_BRANCH" != "master" ]
  then
    rainforest validate
    rainforest upload --token $RAINFOREST_TOKEN

    GITHUB_PULL_NUMBER=$(echo $CI_PULL_REQUEST | awk -F/ '{print $7}')
    rainforest run all --fg --fail-fast --git-trigger --token "$RAINFOREST_TOKEN" --site-id "$RAINFOREST_SITE" --description "CI run for $CIRCLE_BRANCH"
fi

