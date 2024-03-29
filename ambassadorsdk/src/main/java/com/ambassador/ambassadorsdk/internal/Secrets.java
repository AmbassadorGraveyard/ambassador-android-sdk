package com.ambassador.ambassadorsdk.internal;

import com.ambassador.ambassadorsdk.BuildConfig;

public final class Secrets {
    public static String getPusherKey() {
        return BuildConfig.IS_RELEASE_BUILD ? BuildConfig.PUSHER_KEY_PROD : BuildConfig.PUSHER_KEY_DEV;
    }

    public static String getSentryUrl() {
        return BuildConfig.SENTRY_URL;
    }

}
