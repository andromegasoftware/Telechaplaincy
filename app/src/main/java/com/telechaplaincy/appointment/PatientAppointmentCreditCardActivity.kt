package com.telechaplaincy.appointment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_patient_appointment_credit_card.*

class PatientAppointmentCreditCardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAppointmentForPatient: DocumentReference
    private lateinit var dbSaveAppointmentForChaplain: DocumentReference
    private lateinit var dbSaveAppointmentForMainCollection: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId: String = ""
    private var chaplainCollectionName: String = ""

    private var chaplainCategory = ""
    private var chaplainProfileFieldUserId: String = ""
    private var chaplainEarliestDate: String = ""
    private var patientSelectedTime = ""
    private var appointmentPrice = ""

    private var appointmentId = ""

    private var isPaymentDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_credit_card)

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
        appointmentPrice = intent.getStringExtra("appointmentPrice").toString()

        dbSaveAppointmentForPatient =
            db.collection("patients").document(patientUserId)

        dbSaveAppointmentForChaplain =
            db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        dbSaveAppointmentForMainCollection = db.collection("appointment").document()

        payButton.setOnClickListener {
            isPaymentDone = true
            if (isPaymentDone){
                saveAppointmentInfoFireStore()

            }
        }

    }

    private fun saveAppointmentInfoFireStore(){

        payButton.isClickable = false

        appointmentId = dbSaveAppointmentForMainCollection.id
        val appointmentModelClass = AppointmentModelClass(appointmentId, patientUserId,
            chaplainProfileFieldUserId, appointmentPrice, patientSelectedTime)

        dbSaveAppointmentForMainCollection.set(appointmentModelClass, SetOptions.merge())
            .addOnSuccessListener {

                dbSaveAppointmentForPatient
                    .collection("appointments").document(appointmentId)
                    .set(appointmentModelClass, SetOptions.merge())
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

                dbSaveAppointmentForChaplain
                    .collection("appointments").document(appointmentId)
                    .set(appointmentModelClass, SetOptions.merge())
                    .addOnSuccessListener {

                        payButton.isClickable = true
                        val intent = Intent(this, PatientAppointmentFinishActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentSummaryActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        intent.putExtra("readTime", chaplainEarliestDate)
        intent.putExtra("patientSelectedTime", patientSelectedTime)
        startActivity(intent)
        finish()
    }
}