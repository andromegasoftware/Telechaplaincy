package com.telechaplaincy.chaplain_future_appointments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import kotlinx.android.synthetic.main.activity_patient_appointment_history_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PatientAppointmentHistoryDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var patientUserId: String = ""
    private lateinit var dbSave: DocumentReference
    private var appointmentId: String = ""

    private var chaplainProfileFirstName: String = ""
    private var chaplainProfileLastName: String = ""
    private var chaplainProfileAddressTitle: String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileCredentialTitle: String = ""
    private var appointmentTime: String = ""
    private var doctorNoteForPatient: String = ""
    private var doctorNoteForChaplain: String = ""
    private var patientAppointmentTimeZone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_history_details)

        appointmentId = intent.getStringExtra("appointment_id").toString()
        patientUserId = intent.getStringExtra("patientUserId").toString()

        auth = FirebaseAuth.getInstance()
        dbSave = db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)

        takeAppointmentInfo()

    }

    private fun takeAppointmentInfo() {
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<AppointmentModelClass>()
                    if (result != null) {
                        if (result.chaplainName != null) {
                            chaplainProfileFirstName = result.chaplainName.toString()

                        }
                        if (result.chaplainSurname != null) {
                            chaplainProfileLastName = result.chaplainSurname.toString()
                        }
                        if (result.chaplainAddressingTitle != null) {
                            chaplainProfileAddressTitle = result.chaplainAddressingTitle.toString()
                        }
                        if (result.chaplainCredentialsTitle != null) {
                            credentialTitleArrayList = result.chaplainCredentialsTitle
                        }
                        if (!credentialTitleArrayList.isNullOrEmpty()) {
                            for (k in credentialTitleArrayList) {
                                chaplainProfileCredentialTitle += ", $k"
                            }
                            patient_appointment_history_chaplain_name_textView.text =
                                chaplainProfileAddressTitle + " " +
                                        chaplainProfileFirstName + " " + chaplainProfileLastName + chaplainProfileCredentialTitle
                        }
                        if (result.patientAppointmentTimeZone != null) {
                            patientAppointmentTimeZone = result.patientAppointmentTimeZone!!
                        }

                        if (result.appointmentDate != null) {
                            appointmentTime = result.appointmentDate
                            val dateFormatLocalZone = SimpleDateFormat("EEE, dd.MMM.yyyy")
                            dateFormatLocalZone.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentDate =
                                dateFormatLocalZone.format(Date(appointmentTime.toLong()))
                            patient_appointment_history_date_textView.text = appointmentDate

                            val dateFormatTime = SimpleDateFormat("HH:mm aaa z")
                            dateFormatTime.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentTime =
                                dateFormatTime.format(Date(appointmentTime.toLong()))
                            patient_appointment_history_time_textView.text =
                                "$appointmentTime $patientAppointmentTimeZone"
                        }
                        if (result.chaplainNoteForPatient != null) {
                            doctorNoteForPatient = result.chaplainNoteForPatient.toString()
                            patient_appointment_history_doctor_note_for_patient_textView.text =
                                doctorNoteForPatient
                        }
                        if (result.chaplainNoteForDoctors != null) {
                            doctorNoteForChaplain = result.chaplainNoteForDoctors.toString()
                            patient_appointment_history_doctor_note_for_chaplains_textView.text =
                                doctorNoteForChaplain
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
        val intent = Intent(this, PatientAppointmentHistory::class.java)
        intent.putExtra("appointment_id", appointmentId)
        intent.putExtra("patientUserId", patientUserId)
        startActivity(intent)
        finish()
    }
}