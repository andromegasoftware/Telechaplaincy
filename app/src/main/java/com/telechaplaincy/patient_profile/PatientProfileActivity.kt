package com.telechaplaincy.patient_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_sign_activities.LoginPage
import kotlinx.android.synthetic.main.activity_chaplain_main.*
import kotlinx.android.synthetic.main.activity_patient_profile.*
import kotlinx.android.synthetic.main.activity_patient_profile.bottomNavigation

class PatientProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()

        logOut.setOnClickListener {
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
}