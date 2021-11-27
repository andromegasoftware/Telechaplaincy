package com.telechaplaincy.appointment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_selection.chaplains_selection.ChaplainsListedSelectionActivity
import kotlinx.android.synthetic.main.activity_patient_appointment_time_selection.*

class PatientAppointmentTimeSelectionActivity : AppCompatActivity() {

    private var chaplainCategory = ""
    private var chaplainProfileFieldUserId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_time_selection)

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()
        textViewchaplainId.text = chaplainProfileFieldUserId

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentContinueActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        startActivity(intent)
        finish()
    }
}