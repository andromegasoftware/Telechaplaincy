package com.telechaplaincy.patient_notification_page

class NotificationModelClass(
    var notificationTitle: String? = null,
    var notificationMessage: String? = null,
    var notificationTimeStamp: String? = null,
    val notificationReceiver: String? = null
)