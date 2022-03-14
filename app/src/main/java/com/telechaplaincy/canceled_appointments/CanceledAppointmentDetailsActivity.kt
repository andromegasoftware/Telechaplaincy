package com.telechaplaincy.canceled_appointments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import kotlinx.android.synthetic.main.activity_canceled_appointment_details.*
import java.text.SimpleDateFormat
import java.util.*

class CanceledAppointmentDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var appointmentId: String = ""
    private var patientUserId: String = ""
    private var patientFirstName: String = ""
    private var patientLastName: String = ""
    private var patientMailAddress: String = ""
    private var chaplainFirstName: String = ""
    private var chaplainLastName: String = ""
    private var chaplainUserId: String = ""
    private var patientAppointmentTimeZone: String = ""
    private var appointmentTime: String = ""
    private var appointmentStatus: String = ""
    private var appointmentPrice: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canceled_appointment_details)

        appointmentId = intent.getStringExtra("appointmentId").toString()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        dbSave = db.collection("appointment").document(appointmentId)

        canceled_appointment_details_page_appointment_number_textView.text = appointmentId

        takeAppointmentInfo()

        canceled_appointment_details_mark_fee_repaid_button.setOnClickListener {
            markAppointmentFeeRepaid()
            markAppointmentFeeRepaidForChaplainCollection()
            markAppointmentFeeRepaidForPatientCollection()
        }
    }

    //this method will mark appointment fee is repaid under the main appointment collection
    private fun markAppointmentFeeRepaid() {
        val data = hashMapOf("appointmentFeeRepaid" to "true")
        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                //Log.d(TAG, "DocumentSnapshot successfully written!")
                val toast = Toast.makeText(
                    this,
                    getString(R.string.canceled_appointments_details_mark_fee_repaid_button_toast_message),
                    Toast.LENGTH_LONG
                ).show()

                canceled_appointment_details_mark_fee_repaid_button.text =
                    getString(R.string.canceled_appointments_details_mark_fee_repaid_button_clicked)
            }
            .addOnFailureListener {
                //e -> Log.w(TAG, "Error writing document", e)
            }
    }

    //this method will mark appointment fee is repaid under the chaplain appointment document
    private fun markAppointmentFeeRepaidForChaplainCollection() {
        val data = hashMapOf("appointmentFeeRepaid" to "true")
        db.collection("chaplains").document(chaplainUserId)
            .collection("appointments").document(appointmentId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                //Log.d("isSuccess", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener {
                //e -> Log.w(TAG, "Error writing document", e)
            }
    }

    //this method will mark appointment fee is repaid under the patient appointment document
    private fun markAppointmentFeeRepaidForPatientCollection() {
        val data = hashMapOf("appointmentFeeRepaid" to "true")
        db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                //Log.d("isSuccess", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener {
                //e -> Log.w(TAG, "Error writing document", e)
            }
    }

    private fun takeAppointmentInfo() {
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<AppointmentModelClass>()
                    if (result != null) {
                        if (result.chaplainId != null) {
                            chaplainUserId = result.chaplainId.toString()
                        }
                        if (result.chaplainName != null) {
                            chaplainFirstName = result.chaplainName.toString()
                        }
                        if (result.chaplainSurname != null) {
                            chaplainLastName = result.chaplainSurname.toString()
                            canceled_appointment_details_page_chaplain_name_textView.text =
                                "$chaplainFirstName $chaplainLastName"
                        }

                        if (result.patientName != null) {
                            patientFirstName = result.patientName
                        }
                        if (result.patientSurname != null) {
                            patientLastName = result.patientSurname
                            canceled_appointment_details_page_patient_name_textView.text =
                                "$patientFirstName $patientLastName"
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
                            val patientAppointmentDate = appointmentDate.toString()

                            canceled_appointment_details_page_appointment_date_textView
                                .text = patientAppointmentDate
                        }
                        if (result.appointmentPrice != null) {
                            appointmentPrice = result.appointmentPrice
                            canceled_appointment_details_page_price_textView.text =
                                getString(R.string.appointment_price) + appointmentPrice
                        }
                        if (result.patientId != null) {
                            patientUserId = result.patientId.toString()
                            //this method will get the patient mail using the patient id
                            getPatientMailAddress()
                        }
                        if (result.appointmentStatus != null) {
                            appointmentStatus = result.appointmentStatus.toString()
                            canceled_appointment_details_page_appointment_status_textView
                                .text = appointmentStatus
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

    private fun getPatientMailAddress() {
        db.collection("patients").document(patientUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    patientMailAddress =
                        document.data?.get("email")?.toString() ?: patientMailAddress
                    canceled_appointment_details_page_patient_email_textView.text =
                        patientMailAddress
                } else {
                    //Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "get failed with ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CanceledAppointmentsListActivity::class.java)
        startActivity(intent)
        finish()
    }
}