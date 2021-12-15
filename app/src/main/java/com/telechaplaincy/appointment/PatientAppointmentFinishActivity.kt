package com.telechaplaincy.appointment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.R
import com.telechaplaincy.patient.PatientMainActivity
import kotlinx.android.synthetic.main.activity_patient_appointment_finish.*

class PatientAppointmentFinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_finish)

        patient_appointment_finish_page_go_to_main_button.setOnClickListener {
            val intent = Intent(this, PatientMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}