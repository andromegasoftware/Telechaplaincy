package com.telechaplaincy.appointment

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
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_patient_appointment_continue.*
import kotlinx.android.synthetic.main.activity_patient_appointment_summary.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class PatientAppointmentSummaryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAvailableTimes: DocumentReference
    private lateinit var dbDatabaseChaplain: DocumentReference
    private lateinit var dbDatabasePatient: DocumentReference
    private lateinit var dbDatabaseAppointmentPrice: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId: String = ""
    private var chaplainCollectionName: String = ""

    private var chaplainCategory = ""
    private var chaplainProfileFieldUserId: String = ""
    private var chaplainEarliestDate: String = ""
    private var patientSelectedTime = ""

    private var chaplainProfileFirstName = ""
    private var chaplainProfileLastName = ""
    private var chaplainProfileAddressTitle = ""

    private var patientProfileFirstName = ""
    private var patientProfileLastName = ""

    private var appointmentPrice = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_summary)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()
        chaplainEarliestDate = intent.getStringExtra("readTime").toString()
        patientSelectedTime = intent.getStringExtra("patientSelectedTime").toString()

        dbSaveAvailableTimes =
            db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
                .collection("chaplainTimes").document("availableTimes")

        dbDatabaseChaplain =
            db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
        dbDatabasePatient = db.collection("patients").document(patientUserId)
        dbDatabaseAppointmentPrice = db.collection("appointment").document("price")

        val dateFormatLocalZone = SimpleDateFormat("EEE HH:mm aaa, dd-MM-yyyy")
        dateFormatLocalZone.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        val chipTimeLocale = dateFormatLocalZone.format(Date(patientSelectedTime.toLong()))
        patient_appointment_summary_page_appointment_date_textView.text = chipTimeLocale

        chaplainInfoTake()
        patientInfoTake()
        appointmentInfoTake()

        patient_appointment_summary_page_continue_button.setOnClickListener {
            goToCreditCardPage()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        appointmentTimeTakeBack()
        val intent = Intent(this, PatientAppointmentTimeSelectionActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        intent.putExtra("readTime", chaplainEarliestDate)
        intent.putExtra("patientSelectedTime", patientSelectedTime)
        startActivity(intent)
        finish()
    }

    private fun goToCreditCardPage(){
        val intent = Intent(this, PatientAppointmentCreditCardActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        intent.putExtra("readTime", chaplainEarliestDate)
        intent.putExtra("patientSelectedTime", patientSelectedTime)
        startActivity(intent)
        finish()
    }

    private fun appointmentTimeTakeBack() {
        val map = HashMap<String, Any>()
        map["time"] = patientSelectedTime
        map["isBooked"] = false
        map["patientUid"] = "null"
        dbSaveAvailableTimes.update((patientSelectedTime), map)
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("data upload", "Error writing document", e)
            }
    }

    private fun chaplainInfoTake() {
        dbDatabaseChaplain.get().addOnSuccessListener { document ->
            if (document != null) {
                val result = document.toObject<ChaplainUserProfile>()
                if (result != null) {
                    if (result.name != null) {
                        chaplainProfileFirstName = result.name.toString()

                    }
                    if (result.surname != null) {
                        chaplainProfileLastName = result.surname.toString()
                    }
                    if (result.addressingTitle != null) {
                        chaplainProfileAddressTitle = result.addressingTitle.toString()

                        patient_appointment_summary_page_chaplain_name_textView.text =
                            "$chaplainProfileAddressTitle $chaplainProfileFirstName $chaplainProfileLastName"
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

    private fun patientInfoTake() {
        dbDatabasePatient.get().addOnSuccessListener { document ->
            if (document != null) {
                val result = document.toObject<UserProfile>()
                if (result != null) {
                    if (result.name != null) {
                        patientProfileFirstName = result.name.toString()

                    }
                    if (result.surname != null) {
                        patientProfileLastName = result.surname.toString()

                        patient_appointment_summary_page_patient_name_textView.text =
                            "$patientProfileFirstName $patientProfileLastName"
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

    private fun appointmentInfoTake() {
        dbDatabaseAppointmentPrice.get().addOnSuccessListener { document ->
            if (document != null) {
                val result = document.data
                if (result != null){
                    appointmentPrice = result["appointmentPrice"].toString()
                    patient_appointment_summary_page_price_textView.text = "$appointmentPrice $"
                }
            } else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }
}