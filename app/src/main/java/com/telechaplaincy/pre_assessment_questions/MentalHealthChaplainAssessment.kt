package com.telechaplaincy.pre_assessment_questions

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.R
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentDetailActivity

class MentalHealthChaplainAssessment : AppCompatActivity() {

    private var appointmentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mental_health_chaplain_assessment)

        appointmentId = intent.getStringExtra("appointment_id").toString()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }

}