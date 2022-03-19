package com.telechaplaincy.mail_and_message_send_package

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DeviceToDeviceNotificationClass {
    private val db = Firebase.firestore

    //topic will be user id for a specific user. That means uid.
    fun sendNotificationToAnotherDevice(topic: String, body: String, title: String) {
        val notificationWillBeSent = hashMapOf(
            "topic" to topic,
            "title" to title,
            "body" to body
        )
        db.collection("deviceToDeviceNotification").document()
            .set(notificationWillBeSent, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }
    }
}