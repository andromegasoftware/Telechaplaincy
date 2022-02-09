package com.telechaplaincy.mail_and_message_send_package;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.telechaplaincy.R;

public class LocalNotificationClass extends BroadcastReceiver {

    public final String CHANNEL_ID = "1";
    public String title = "";
    public String body = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        title = context.getString(R.string.local_notification_title);
        body = context.getString(R.string.local_notification_body);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "1", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo).setContentTitle(title)
                .setContentText(body);

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(1, builder.build());

    }
}
