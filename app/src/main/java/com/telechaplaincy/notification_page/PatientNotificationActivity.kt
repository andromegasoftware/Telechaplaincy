package com.telechaplaincy.notification_page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.admin.AdminMainActivity
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.chaplain_profile.ChaplainProfileActivity
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_profile.PatientProfileActivity
import kotlinx.android.synthetic.main.activity_patient_notification.*
import kotlinx.android.synthetic.main.activity_patient_profile.bottomNavigation

class PatientNotificationActivity : AppCompatActivity() {

    private var userId: String = ""
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: CollectionReference
    private lateinit var dbRef: DocumentReference
    private var userType: String = ""
    private var messageId: String = ""

    private var notificationsListArray = ArrayList<NotificationModelClass>()
    private lateinit var notificationsAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_notification)

        userType = intent.getStringExtra("userType").toString()
        //Log.d("userType", userType)

        if (userType != "admin") {
            addingActivitiesToBottomMenu()
            imageButtonMessageSend.visibility = View.GONE
        } else {
            bottomNavigation.visibility = View.GONE
        }

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        userId = user.uid

        if (userType == "patient") {
            queryRef = db.collection("patients").document(userId).collection("inbox")
        } else if (userType == "chaplain") {
            queryRef = db.collection("chaplains").document(userId).collection("inbox")
        } else {
            queryRef = db.collection("general_outbox")
        }

        notificationsAdapter =
            NotificationAdapter(notificationsListArray) { notificationModelClassItem: NotificationModelClass ->
                notificationClickListener(notificationModelClassItem)
            }

        notificationsListMethod()

        // chaplain lists recyclerview
        val notificationsLayoutManager = LinearLayoutManager(this)
        notificationsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        notification_page_recyclerView.layoutManager = notificationsLayoutManager
        notification_page_recyclerView.adapter = notificationsAdapter

        imageButtonMessageSend.setOnClickListener {
            val intent = Intent(this, AdminMessageSendActivity::class.java)
            intent.putExtra("userType", userType)
            startActivity(intent)
            finish()
        }

    }

    private fun notificationsListMethod() {
        queryRef.orderBy("dateSent", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    notificationsListArray.add(document.toObject())
                }
                notificationsAdapter.submitList(notificationsListArray)
                notificationsAdapter.notifyDataSetChanged()

                if (notificationsAdapter.itemCount == 0) {
                    no_message_lineer_layout.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun notificationClickListener(notificationModelClass: NotificationModelClass) {
        messageId = notificationModelClass.messageId.toString()
        markMessageRead(messageId)
    }

    private fun markMessageRead(mMessageId: String) {
        if (userType == "patient") {
            dbRef =
                db.collection("patients").document(userId).collection("inbox").document(mMessageId)
        } else if (userType == "chaplain") {
            dbRef =
                db.collection("chaplains").document(userId).collection("inbox").document(mMessageId)
        } else {
            dbRef = db.collection("general_outbox").document(mMessageId)
        }
        dbRef.update("messageRead", true)
            .addOnSuccessListener {
                val intent = Intent(this, NotificationDetailsActivity::class.java)
                intent.putExtra("messageId", messageId)
                intent.putExtra("userType", userType)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e -> Log.w("userType", "Error updating document", e) }
    }


    private fun addingActivitiesToBottomMenu() {

        bottomNavigation.selectedItemId = R.id.bottom_menu_notification

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> {
                    Log.d("userType", userType)
                    if (userType == "patient") {
                        val intent = Intent(this, PatientMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (userType == "chaplain") {
                        val intent = Intent(this, ChaplainMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                R.id.bottom_menu_notification -> {
                    /*val intent = Intent(this, PatientNotificationActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    finish()*/
                }
                R.id.bottom_menu_profile -> {
                    Log.d("userType", userType)
                    if (userType == "patient") {
                        val intent = Intent(this, PatientProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (userType == "chaplain") {
                        val intent = Intent(this, ChaplainProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (userType == "admin") {
            val intent = Intent(this, AdminMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}