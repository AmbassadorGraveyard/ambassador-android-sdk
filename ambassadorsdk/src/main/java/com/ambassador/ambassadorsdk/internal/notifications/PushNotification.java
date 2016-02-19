package com.ambassador.ambassadorsdk.internal.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
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

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), context.getApplicationInfo().icon);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setLargeIcon(icon)
                .setContentTitle("AmbassadorSDK")
                .setContentText("This is a push notification")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        int notificationId = 001;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: uncomment this when it's time to use notifications
        // notificationManager.notify(notificationId, notification);
    }

}
