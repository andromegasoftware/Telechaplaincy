package com.telechaplaincy.notification_page

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_admin_mesage_send.*

class AdminMessageSendActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: DocumentReference

    private var userType: String = ""
    private var userGroup: String = ""
    private var body: String = ""
    private var title: String = ""
    private var dateSent: String = ""
    private var messageRead: Boolean = false
    private var messageId: String = ""
    private var senderUid: String = ""
    private var topic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_mesage_send)

        userType = intent.getStringExtra("userType").toString()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        senderUid = user.uid

        adminMessageReceiverSelectionMethod()

        message_send_activity_send_message_button.setOnClickListener {

            if (userGroup == "Choose a receiver") {
                Toast.makeText(
                    this,
                    getString(R.string.send_message_activity_send_message_button_toast_message),
                    Toast.LENGTH_LONG
                ).show()
            } else if (userGroup == "All Users") {
                userGroup = "patients"
                topic = "patients"
                messageSendMethod()

                userGroup = "chaplains"
                topic = "chaplains"
                messageSendMethod()
            } else {
                messageSendMethod()
            }
        }

    }

    private fun messageSendMethod() {
        progressBarMessageSend.visibility = View.VISIBLE
        message_send_activity_send_message_button.isClickable = false

        title = editText_message_title.text.toString()
        body = editText_message_body.text.toString()
        dateSent = System.currentTimeMillis().toString()
        queryRef = db.collection("general_outbox").document()
        messageId = queryRef.id

        val message = NotificationModelClass(
            title, body, userGroup, topic, senderUid,
            dateSent, messageId, messageRead
        )

        queryRef.set(message)
            .addOnSuccessListener {
                progressBarMessageSend.visibility = View.GONE
                message_send_activity_send_message_button.isClickable = true
                message_send_activity_send_message_button.text =
                    getString(R.string.send_message_activity_send_message_button_text_clicked)
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun adminMessageReceiverSelectionMethod() {
        val optionsReceivers = resources.getStringArray(R.array.message_receivers_array)
        spinner_message_receiver.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionsReceivers
        )
        spinner_message_receiver.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    userGroup = optionsReceivers[position]
                    if (userGroup == "All Patients") {
                        userGroup = "patients"
                        topic = "patients"
                    } else if (userGroup == "All Chaplains") {
                        userGroup = "chaplains"
                        topic = "chaplains"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
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