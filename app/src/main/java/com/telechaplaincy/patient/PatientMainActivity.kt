package com.telechaplaincy.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.patient_profile.PatientAppointmentPersonalInfo
import com.telechaplaincy.patient_profile.PatientProfileActivity
import com.telechaplaincy.patient_sign_activities.LoginPage
import kotlinx.android.synthetic.main.activity_chaplain_main.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import kotlinx.android.synthetic.main.activity_patient_main.*
import kotlinx.android.synthetic.main.activity_patient_main.bottomNavigation

class PatientMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var userRole:String = ""
    private var userId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_main)
        progressBarPatientMainActivity.bringToFront()

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()

        patient_new_appointment_button.setOnClickListener {
            val intent = Intent(this, PatientAppointmentPersonalInfo::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            checkUserRole()
            //Log.d("TAG", "patient main not null")
        }else{
            val intent = Intent(this, MainEntry::class.java)
            startActivity(intent)
            finish()
           // Log.d("TAG", "patient main null")
        }
    }

    private fun checkUserRole(){
        userId = auth.currentUser?.uid ?: userId
        val docRef = db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                progressBarPatientMainActivity.visibility = View.GONE
                if (document != null) {
                    if (document.data?.containsKey("userRole") == true){
                        val data = document.data as Map<String, String>
                        userRole = data["userRole"].toString()
                        if (userRole == "2"){
                            val intent = Intent(this, ChaplainMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    Log.d("TAG", "DocumentSnapshot data: $userRole")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //progressBarPatientMain.visibility = View.GONE
                Log.d("TAG", "get failed with ", exception)
            }

    }

    private fun addingActivitiesToBottomMenu(){

        bottomNavigation.selectedItemId = R.id.bottom_menu_home

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_menu_home -> {
                    //val intent = Intent(this, PatientMainActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "main", Toast.LENGTH_LONG).show()
                    //finish()
                }

                R.id.bottom_menu_notification -> {
                    //val intent = Intent(this, MyListActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    //finish()
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