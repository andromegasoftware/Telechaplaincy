package com.telechaplaincy.appointment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.R
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.pre_assessment_questions.*
import kotlinx.android.synthetic.main.activity_patient_appointment_finish.*

class PatientAppointmentFinishActivity : AppCompatActivity() {

    private var chaplainCategory: String = ""
    private var appointmentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_finish)


        chaplainCategory = intent.getStringExtra("chaplainCategory").toString()
        appointmentId = intent.getStringExtra("appointmentId").toString()

        patient_appointment_finish_page_answer_question_button.setOnClickListener {
            if (chaplainCategory == "HumanistChaplains") {
                val intent = Intent(this, CommunityChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "HealthChaplains") {
                val intent = Intent(this, HealthCareChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "HospiceChaplains") {
                val intent = Intent(this, HospiceChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "MentalChaplains") {
                val intent = Intent(this, MentalHealthChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            //crisis chaplain and mental health Chaplain assessment questions are same
            else if (chaplainCategory == "CrisisChaplains") {
                val intent = Intent(this, MentalHealthChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "PalliativeChaplains") {
                val intent = Intent(this, PalliativeCareChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "PetChaplains") {
                val intent = Intent(this, PetChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "PrisonChaplains") {
                val intent = Intent(this, PrisonChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "PediatricChaplains") {
                val intent = Intent(this, PediatricChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "CollegeChaplains") {
                val intent = Intent(this, CollegeChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "CorporateChaplains") {
                val intent = Intent(this, CorporateChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "MilitaryChaplains") {
                val intent = Intent(this, MilitaryLawChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "LawChaplains") {
                val intent = Intent(this, MilitaryLawChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
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