package com.telechaplaincy.notification_page

class NotificationModelClass(
    val title: String? = null,
    var body: String? = null,
    val userGroup: String? = null,
    val topic: String? = null,
    var senderUid: String? = null,
    val dateSent: String? = null,
    val messageId: String? = null,
    var messageRead: Boolean? = false
)