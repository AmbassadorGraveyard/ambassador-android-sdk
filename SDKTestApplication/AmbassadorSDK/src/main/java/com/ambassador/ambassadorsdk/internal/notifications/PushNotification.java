package com.ambassador.ambassadorsdk.internal.notifications;

import com.ambassador.ambassadorsdk.internal.models.NotificationData;

public final class PushNotification {

    private NotificationData notificationData;

    public PushNotification(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public void execute() {
        // show notification
    }

}
