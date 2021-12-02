package com.telechaplaincy.patient_past_appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.R
import com.telechaplaincy.patient.PatientMainActivity
import kotlinx.android.synthetic.main.activity_patient_future_appointment_detail.*
import kotlinx.android.synthetic.main.activity_patient_past_appointments_detail.*

class PatientPastAppointmentsDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_past_appointments_detail)

        textView64.text = intent.getStringExtra("appointment_id").toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}