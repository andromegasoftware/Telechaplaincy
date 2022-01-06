package com.telechaplaincy.patient_notification_page

class NotificationModelClass(
    var body: String? = null,
    var isMessageRead: Boolean? = false,
    var senderUid: String? = null,
    val title: String? = null,
    val topic: String? = null,
    val userGroup: String? = null,
    val dateSent: String? = null,
    val messageId: String? = null
)