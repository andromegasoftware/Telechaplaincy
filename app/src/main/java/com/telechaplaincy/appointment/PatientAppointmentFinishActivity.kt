package com.telechaplaincy.appointment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.R
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.pre_assessment_questions.CommunityChaplainAssessmentActivity
import kotlinx.android.synthetic.main.activity_patient_appointment_finish.*

class PatientAppointmentFinishActivity : AppCompatActivity() {

    private var chaplainCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_finish)


        chaplainCategory = intent.getStringExtra("chaplainCategory").toString()

        patient_appointment_finish_page_answer_question_button.setOnClickListener {
            if (chaplainCategory == "HumanistChaplains") {
                val intent = Intent(this, CommunityChaplainAssessmentActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

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