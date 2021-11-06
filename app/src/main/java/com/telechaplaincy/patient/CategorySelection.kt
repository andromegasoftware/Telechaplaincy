package com.telechaplaincy.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.patient_profile.PatientAppointmentPersonalInfo
import kotlinx.android.synthetic.main.activity_category_selection.*

class CategorySelection : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        auth = FirebaseAuth.getInstance()

        chaplain_field_back_button.setOnClickListener {
            val intent = Intent(this, PatientAppointmentPersonalInfo::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentPersonalInfo::class.java)
        startActivity(intent)
        finish()
    }
}