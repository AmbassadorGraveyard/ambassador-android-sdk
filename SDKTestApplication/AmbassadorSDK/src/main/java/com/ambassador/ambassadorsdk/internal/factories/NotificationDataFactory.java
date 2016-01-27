package com.ambassador.ambassadorsdk.internal.factories;

import android.os.Bundle;

import com.ambassador.ambassadorsdk.internal.models.NotificationData;

public final class NotificationDataFactory {

    public static NotificationData decodeData(Bundle data) {
        return new NotificationData.Builder().build();
    }

}
