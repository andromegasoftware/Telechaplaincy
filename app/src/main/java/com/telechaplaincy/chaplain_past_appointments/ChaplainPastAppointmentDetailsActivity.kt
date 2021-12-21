package com.telechaplaincy.chaplain_past_appointments

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.patient.PatientMainActivity
import kotlinx.android.synthetic.main.activity_chaplain_past_appointment_details.*
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*

class ChaplainPastAppointmentDetailsActivity : AppCompatActivity() {

    private var appointmentId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_past_appointment_details)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        //call edittext scrollable method here
        editTextBioScrollForPatient()
        editTextBioScrollForChaplain()
    }

    //this method is for the edittext chaplain note for patient. it makes edittext scrollable.
    @SuppressLint("ClickableViewAccessibility")
    private fun editTextBioScrollForPatient() {
        chaplain_past_appointment_note_for_patient_editText.isVerticalScrollBarEnabled = true
        chaplain_past_appointment_note_for_patient_editText.overScrollMode = View.OVER_SCROLL_ALWAYS
        chaplain_past_appointment_note_for_patient_editText.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        chaplain_past_appointment_note_for_patient_editText.movementMethod = ScrollingMovementMethod.getInstance()
        chaplain_past_appointment_note_for_patient_editText.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event?.action) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    //this method is for the edittext chaplain note for chaplain. it makes edittext scrollable.
    @SuppressLint("ClickableViewAccessibility")
    private fun editTextBioScrollForChaplain() {
        chaplain_past_appointment_note_for_chaplain_editText.isVerticalScrollBarEnabled = true
        chaplain_past_appointment_note_for_chaplain_editText.overScrollMode = View.OVER_SCROLL_ALWAYS
        chaplain_past_appointment_note_for_chaplain_editText.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        chaplain_past_appointment_note_for_chaplain_editText.movementMethod = ScrollingMovementMethod.getInstance()
        chaplain_past_appointment_note_for_chaplain_editText.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event?.action) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}