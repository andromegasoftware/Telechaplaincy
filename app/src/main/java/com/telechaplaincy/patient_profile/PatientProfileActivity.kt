package com.telechaplaincy.patient_profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_sign_activities.LoginPage
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_patient_profile.*

class PatientProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSave: DocumentReference
    private val db = Firebase.firestore
    private var patientProfileFieldUserId:String = ""
    private var patientProfileFirstName:String = ""
    private var patientProfileLastName:String = ""
    private var patientProfileImageLink:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            patientProfileFieldUserId = user.uid
        }

        dbSave = db.collection("patients").document(patientProfileFieldUserId)

        readPatientPersonalInfo()

        patient_profile_profile_button.setOnClickListener {
            val intent = Intent(this, PatientProfileDetailsActivity::class.java)
            startActivity(intent)
            finish()
        }

        patient_profile_sign_out_button.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun addingActivitiesToBottomMenu(){

        bottomNavigation.selectedItemId = R.id.bottom_menu_profile

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_menu_home -> {
                    val intent = Intent(this, PatientMainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "main", Toast.LENGTH_LONG).show()
                    finish()
                }

                R.id.bottom_menu_notification -> {
                    //val intent = Intent(this, MyListActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    //finish()
                }
                R.id.bottom_menu_profile -> {
                    //val intent = Intent(this, ProfileActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "profile", Toast.LENGTH_LONG).show()
                    //finish()
                }
            }
            true
        }
    }

    //this function will read the patient personal info from the firestore
    private fun readPatientPersonalInfo(){
        dbSave.get().addOnSuccessListener { document ->
            if (document != null){
                val userProfile = document.toObject<UserProfile>()
                if (userProfile != null) {
                    patientProfileFirstName = userProfile.name.toString()
                    if (patientProfileFirstName != "null"){
                    }
                    patientProfileLastName = userProfile.surname.toString()
                    if (patientProfileLastName != "null"){
                        patient_profile_name_textView.text = "$patientProfileFirstName $patientProfileLastName"
                    }
                    patientProfileImageLink = userProfile.profileImage.toString()
                    Log.d("image: ", "patientProfileImageLink")
                    if (patientProfileImageLink != "null" && patientProfileImageLink != ""){
                        Picasso.get().load(patientProfileImageLink).into(patient_profile_profile_image_image_view)
                    }

                }
            }else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }
}