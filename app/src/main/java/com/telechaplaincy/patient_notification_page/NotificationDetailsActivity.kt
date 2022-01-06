package com.telechaplaincy.patient_notification_page

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_notification_details.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationDetailsActivity : AppCompatActivity() {

    private var userId: String = ""
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var dbRef: DocumentReference

    private var userType: String = ""
    private var messageId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)

        userType = intent.getStringExtra("userType").toString()
        messageId = intent.getStringExtra("messageId").toString()
        //Log.d("userType", userType)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        userId = user.uid

        if (userType == "patient") {
            dbRef =
                db.collection("patients").document(userId).collection("inbox").document(messageId)
        } else if (userType == "chaplain") {
            dbRef =
                db.collection("chaplains").document(userId).collection("inbox").document(messageId)
        } else {
            dbRef = db.collection("general_outbox").document(messageId)
        }

        dbRef.get().addOnSuccessListener { documentSnapshot ->
            val message = documentSnapshot.toObject<NotificationModelClass>()
            if (message != null) {
                textViewMessageDetailActivityTitle.text = message.title

                val messageTime = message.dateSent
                val dateFormatLocalZone = SimpleDateFormat("HH:mm - MMM dd, yyyy")
                dateFormatLocalZone.timeZone = TimeZone.getDefault()
                val timeRangeFirst = dateFormatLocalZone.format(Date(messageTime!!.toLong()))
                textViewMessageDetailActivityDate.text = timeRangeFirst

                textViewMessageDetailActivityBody.text = message.body
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientNotificationActivity::class.java)
        intent.putExtra("userType", userType)
        startActivity(intent)
        finish()
    }
}