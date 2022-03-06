package com.telechaplaincy.reach_us_page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_profile.ChaplainProfileActivity
import com.telechaplaincy.patient_profile.PatientProfileActivity
import kotlinx.android.synthetic.main.activity_reach_us.*

class ReachUsActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private var userType: String = ""
    private var userId: String = ""
    private var userName: String = ""
    private var userSurname: String = ""
    private var userMail: String = ""
    private var mailTopic: String = ""
    private var userMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reach_us)

        userType = intent.getStringExtra("user_type").toString()
        userId = intent.getStringExtra("user_id").toString()

        get_in_touch_page_button.setOnClickListener {
            takeSenderInfoFromScreen()
        }

    }

    private fun takeSenderInfoFromScreen() {
        userName = get_in_touch_page_name.text.toString()
        userMail = get_in_touch_page_mail.text.toString()
        mailTopic = get_in_touch_page_topic.text.toString()
        userMessage = get_in_touch_page_message.text.toString()
        if (userName == "") {
            Toast.makeText(this, R.string.get_in_touch_page_name_toast_message, Toast.LENGTH_LONG)
                .show()
        } else if (userMail == "") {
            Toast.makeText(this, R.string.get_in_touch_page_mail_toast_message, Toast.LENGTH_LONG)
                .show()
        } else if (mailTopic == "") {
            Toast.makeText(this, R.string.get_in_touch_page_topic_toast_message, Toast.LENGTH_LONG)
                .show()
        } else if (userMessage == "") {
            Toast.makeText(
                this,
                R.string.get_in_touch_page_message_toast_message,
                Toast.LENGTH_LONG
            ).show()
        } else {
            sendMailMethod()
        }
    }

    private fun sendMailMethod() {
        get_in_touch_page_button.isClickable = false
        get_in_touch_page_progress_bar.visibility = View.VISIBLE
        val body =
            "A $userType send a message. \nSender Name: \n$userName \nSender Id: \n$userId \nSender Mail: \n$userMail \nUser Message: \n$userMessage. \nWarning: Please do not answer this message. You can reach the message sender by mail address or by phone."
        val message = hashMapOf(
            "body" to body,
            "mailAddress" to getString(R.string.app_mail_address),
            "subject" to mailTopic
        )
        db.collection("mailSend").document()
            .set(message, SetOptions.merge())
            .addOnSuccessListener {
                get_in_touch_page_button.isClickable = true
                get_in_touch_page_progress_bar.visibility = View.GONE
                get_in_touch_page_button.text =
                    getString(R.string.get_in_touch_page_button_title_clicked)
                Toast.makeText(
                    this,
                    R.string.get_in_touch_page_message_sent_toast_message,
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { e ->
                get_in_touch_page_button.isClickable = true
                get_in_touch_page_progress_bar.visibility = View.GONE
                Toast.makeText(
                    this,
                    R.string.get_in_touch_page_message_unsent_toast_message,
                    Toast.LENGTH_LONG
                ).show()
                Log.w("TAG", "Error writing document", e)
            }
    }

    override fun onStart() {
        super.onStart()
        if (userType == "patient") {
            takePatientInfo()
        } else {
            takeChaplainInfo()
        }
    }

    private fun writeUserInfoToScreen() {
        get_in_touch_page_name.setText("$userName $userSurname")
        get_in_touch_page_mail.setText(userMail)
    }

    private fun takeChaplainInfo() {
        db.collection("chaplains").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName = document["name"].toString()
                    userSurname = document["surname"].toString()
                    userMail = document["email"].toString()
                    writeUserInfoToScreen()
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun takePatientInfo() {
        db.collection("patients").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName = document["name"].toString()
                    userSurname = document["surname"].toString()
                    userMail = document["email"].toString()
                    writeUserInfoToScreen()
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (userType == "patient") {
            val intent = Intent(this, PatientProfileActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, ChaplainProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}