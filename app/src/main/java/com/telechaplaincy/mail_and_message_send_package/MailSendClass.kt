package com.telechaplaincy.mail_and_message_send_package

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MailSendClass {

    private val db = Firebase.firestore

    fun textMessageSendMethod(phoneNumber: String, message: String) {
        val message = hashMapOf(
            "to" to phoneNumber,
            "body" to message
        )

        db.collection("text_messages").document()
            .set(message, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }
    }

    fun sendMailToChaplain(subject: String, body: String, mailAddress: String) {
        val mailWillBeSent = hashMapOf(
            "to" to arrayListOf(mailAddress),
            "message" to hashMapOf(
                "subject" to subject,
                "text" to body
            )
        )
        db.collection("mail").document()
            .set(mailWillBeSent, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }
    }
}