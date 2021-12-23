package com.telechaplaincy.chaplain_profile

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
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.chaplain_bank_account_info.ChaplainBankAccountInfoActivity
import com.telechaplaincy.chaplain_reports.ChaplainReportsActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainLogIn
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import kotlinx.android.synthetic.main.activity_chaplain_main.bottomNavigation
import kotlinx.android.synthetic.main.activity_chaplain_profile.*

class ChaplainProfileActivity : AppCompatActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var dbSave: DocumentReference
    private val db = Firebase.firestore
    private var chaplainProfileFieldUserId: String = ""
    private var chaplainProfileFirstName: String = ""
    private var chaplainProfileLastName: String = ""
    private var chaplainProfileImageLink: String = ""

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_profile)

        auth = FirebaseAuth.getInstance()

        addingActivitiesToBottomMenu()

        if (auth.currentUser != null) {
            user = auth.currentUser!!
            chaplainProfileFieldUserId = user.uid
        }

        dbSave = db.collection("chaplains").document(chaplainProfileFieldUserId)

        readChaplainPersonalInfo()

        chaplain_profile_button.setOnClickListener {
            val intent = Intent(this, ChaplainProfileDetailsActivity::class.java)
            startActivity(intent)
            finish()
        }

        chaplain_profile_bank_account_button.setOnClickListener {
            val intent = Intent(this, ChaplainBankAccountInfoActivity::class.java)
            startActivity(intent)
            finish()
        }

        chaplain_profile_reports_button.setOnClickListener {
            val intent = Intent(this, ChaplainReportsActivity::class.java)
            startActivity(intent)
            finish()
        }

        chaplain_logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, ChaplainLogIn::class.java)
            startActivity(intent)
            finish()
        }

    }

    //this function will read the chaplain personal info from the firestore
    private fun readChaplainPersonalInfo() {
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                val userProfile = document.toObject<ChaplainUserProfile>()
                if (userProfile != null) {
                    chaplainProfileFirstName = userProfile.name.toString()
                    if (chaplainProfileFirstName != "null") {
                    }
                    chaplainProfileLastName = userProfile.surname.toString()
                    if (chaplainProfileLastName != "null") {
                        chaplain_profile_name_textView.text =
                            "$chaplainProfileFirstName $chaplainProfileLastName"
                    }
                    chaplainProfileImageLink = userProfile.profileImageLink.toString()
                    Log.d("image: ", "patientProfileImageLink")
                    if (chaplainProfileImageLink != "null" && chaplainProfileImageLink != "") {
                        Picasso.get().load(chaplainProfileImageLink)
                            .into(chaplain_profile_image_view)
                    }

                }
            } else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun addingActivitiesToBottomMenu() {

        bottomNavigation.selectedItemId = R.id.bottom_menu_profile

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> {
                    val intent = Intent(this, ChaplainMainActivity::class.java)
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
}