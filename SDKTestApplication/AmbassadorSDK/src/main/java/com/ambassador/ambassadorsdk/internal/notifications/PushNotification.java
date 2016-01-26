package com.ambassador.ambassadorsdk.internal.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.models.NotificationData;

public final class PushNotification {

    private NotificationData notificationData;
    private Context context;

    public PushNotification(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    @NonNull
    public PushNotification withContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public void execute() {
        Intent result = new Intent(context, AmbassadorActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, result, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_map)
                .setContentTitle("AmbassadorSDK")
                .setContentText("This is a push notification")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();


        int notificationId = 001;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

}
