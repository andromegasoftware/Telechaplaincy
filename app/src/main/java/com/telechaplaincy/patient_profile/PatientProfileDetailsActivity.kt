package com.telechaplaincy.patient_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.R

class PatientProfileDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile_details)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}