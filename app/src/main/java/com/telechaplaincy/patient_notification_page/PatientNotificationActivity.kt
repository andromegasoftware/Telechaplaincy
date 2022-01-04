package com.telechaplaincy.patient_notification_page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_profile.ChaplainProfileDetailsActivity
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_profile.PatientProfileActivity
import kotlinx.android.synthetic.main.activity_patient_notification.*
import kotlinx.android.synthetic.main.activity_patient_profile.bottomNavigation

class PatientNotificationActivity : AppCompatActivity() {

    private var userId: String = ""
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: DocumentReference

    private var notificationsListArray = ArrayList<NotificationModelClass>()
    private lateinit var notificationsAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_notification)

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        userId = user.uid

        queryRef = db.collection("notifications").document(userId)

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

    }

    private fun notificationsListMethod() {
        queryRef.get()
            .addOnSuccessListener { document ->
                if (!document.data.isNullOrEmpty()) {
                    val allNotifications = document.data
                    if (!allNotifications.isNullOrEmpty()) {
                        for ((key, value) in allNotifications) {
                            val v = value as Map<*, *>
                            val title = v["notificationTitle"]
                            val message = v["notificationMessage"]
                            val timeStamp = v["notificationTimeStamp"]
                            val notificationModelClass = NotificationModelClass()
                            notificationModelClass.notificationTitle = title.toString()
                            notificationModelClass.notificationMessage = message.toString()
                            notificationModelClass.notificationTimeStamp = timeStamp.toString()

                            notificationsListArray.add(notificationModelClass)
                        }
                    }
                }

                notificationsAdapter.submitList(notificationsListArray)
                notificationsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun notificationClickListener(notificationModelClass: NotificationModelClass) {
        val intent = Intent(this, ChaplainProfileDetailsActivity::class.java)
        intent.putExtra("chaplain_id_send_by_admin", notificationModelClass.notificationTimeStamp)
        startActivity(intent)
        finish()
    }


    private fun addingActivitiesToBottomMenu() {

        bottomNavigation.selectedItemId = R.id.bottom_menu_notification

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> {
                    val intent = Intent(this, PatientMainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "main", Toast.LENGTH_LONG).show()
                    finish()
                }

                R.id.bottom_menu_notification -> {
                    /*val intent = Intent(this, PatientNotificationActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    finish()*/
                }
                R.id.bottom_menu_profile -> {
                    val intent = Intent(this, PatientProfileActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "profile", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            true
        }
    }
}