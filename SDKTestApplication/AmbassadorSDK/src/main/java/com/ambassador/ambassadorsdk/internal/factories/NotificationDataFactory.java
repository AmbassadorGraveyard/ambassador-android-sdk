package com.ambassador.ambassadorsdk.internal.factories;

import com.ambassador.ambassadorsdk.internal.models.NotificationData;

public final class NotificationDataFactory {

    public static NotificationData decodeData() {
        return new NotificationData.Builder().build();
    }

}
