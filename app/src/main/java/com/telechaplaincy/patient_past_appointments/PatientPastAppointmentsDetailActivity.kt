package com.telechaplaincy.patient_past_appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import com.telechaplaincy.appointment.PatientAppointmentContinueActivity
import com.telechaplaincy.patient.PatientMainActivity
import kotlinx.android.synthetic.main.activity_patient_future_appointment_detail.*
import kotlinx.android.synthetic.main.activity_patient_past_appointments_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PatientPastAppointmentsDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainProfileFieldUserId:String = ""
    private var chaplainProfileImageLink:String = ""
    private var chaplainProfileFirstName:String = ""
    private var chaplainProfileLastName:String = ""
    private var chaplainProfileAddressTitle:String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileCredentialTitle:String = ""
    private var chaplainCategory:String = ""
    private var appointmentTime:String = ""
    private var patientUserId:String = ""
    private var doctorNoteForPatient:String = ""
    private var appointmentId:String = ""
    private var patientAppointmentTimeZone:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_past_appointments_detail)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        patientUserId = user.uid

        dbSave = db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)

        takeAppointmentInfo()

        past_appointments_details_make_another_appointment_button.setOnClickListener {
            val intent = Intent(this, PatientAppointmentContinueActivity::class.java)
            intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
            intent.putExtra("chaplain_category", chaplainCategory)
            startActivity(intent)
            finish()
        }
    }

    private fun takeAppointmentInfo(){
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<AppointmentModelClass>()
                    if (result != null) {
                        if (result.chaplainProfileImageLink != null){
                            chaplainProfileImageLink = result.chaplainProfileImageLink.toString()
                            Picasso.get().load(chaplainProfileImageLink).into(past_appointment_imageView_chaplain_image)
                        }
                        if (result.chaplainName != null){
                            chaplainProfileFirstName = result.chaplainName.toString()

                        }
                        if (result.chaplainSurname != null){
                            chaplainProfileLastName = result.chaplainSurname.toString()
                        }
                        if (result.chaplainAddressingTitle != null){
                            chaplainProfileAddressTitle = result.chaplainAddressingTitle.toString()
                        }
                        if (result.chaplainCredentialsTitle != null){
                            credentialTitleArrayList = result.chaplainCredentialsTitle
                        }
                        if (!credentialTitleArrayList.isNullOrEmpty()){
                            for (k in credentialTitleArrayList){
                                chaplainProfileCredentialTitle += ", $k"
                            }
                            past_appointment_chaplain_name_textView.text = chaplainProfileAddressTitle+ " " +
                                    chaplainProfileFirstName+ " "+ chaplainProfileLastName + chaplainProfileCredentialTitle
                        }
                        if (result.patientAppointmentTimeZone != null){
                            patientAppointmentTimeZone = result.patientAppointmentTimeZone!!
                        }

                        if (result.appointmentDate != null){
                            appointmentTime = result.appointmentDate
                            val dateFormatLocalZone = SimpleDateFormat("EEE, dd.MMM.yyyy")
                            dateFormatLocalZone.timeZone = TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentDate = dateFormatLocalZone.format(Date(appointmentTime.toLong()))
                            past_appointment_date_textView.text = appointmentDate

                            val dateFormatTime = SimpleDateFormat("HH:mm aaa z")
                            dateFormatTime.timeZone = TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentTime = dateFormatTime.format(Date(appointmentTime.toLong()))
                            past_appointment_time_textView.text = "$appointmentTime $patientAppointmentTimeZone"
                        }
                        if (result.chaplainNoteForPatient != null){
                            doctorNoteForPatient = result.chaplainNoteForPatient.toString()
                            past_appointment_doctor_note_textView.text = doctorNoteForPatient
                        }
                        if (result.chaplainId != null){
                            chaplainProfileFieldUserId = result.chaplainId.toString()
                        }
                        if (result.chaplainCategory != null){
                            chaplainCategory = result.chaplainCategory.toString()
                        }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}