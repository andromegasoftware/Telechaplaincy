package com.telechaplaincy.cloud_message

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.telechaplaincy.R
import com.telechaplaincy.SplashScreen

const val channelId = "notification_channel"
const val channelName = "com.telechaplaincy.notification"
var notificationTitle: String = ""
var notificationMessage: String = ""

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.telechaplaincy", R.layout.notification_layout)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.logo, R.drawable.logo)

        return remoteView
    }

    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, SplashScreen::class.java)
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivities(this, 0, arrayOf(intent), PendingIntent.FLAG_ONE_SHOT)

        //channel id, channel name
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setLargeIcon(bitmap)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.logo)


        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())

    }

}