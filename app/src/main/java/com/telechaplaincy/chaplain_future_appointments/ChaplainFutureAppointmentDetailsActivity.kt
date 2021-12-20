package com.telechaplaincy.chaplain_future_appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.patient.PatientMainActivity
import kotlinx.android.synthetic.main.activity_chaplain_future_appointment_details.*

class ChaplainFutureAppointmentDetailsActivity : AppCompatActivity() {

    private var appointmentId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_future_appointment_details)

        appointmentId = intent.getStringExtra("appointment_id").toString()
        textView64future.text = "future: " + appointmentId
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}