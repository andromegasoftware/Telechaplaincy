package com.telechaplaincy.appointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_patient_appointment_credit_card.*
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

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
    private var patientName = ""
    private var patientSurname = ""
    private var chaplainName = ""
    private var chaplainSurname = ""
    private var chaplainProfileImageLink = ""
    private var chaplainAddressingTitle = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainFieldArrayList = ArrayList<String>()
    private var patientTimeZone:String = ""
    private var chaplainUniqueUserId:String = ""
    private var patientUniqueUserId:String = ""

    private var isPaymentDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_credit_card)

        chaplainUniqueUserId = String.format("%010d", BigInteger(UUID.randomUUID().toString().replace("-", ""), 16))
        chaplainUniqueUserId = chaplainUniqueUserId.substring(chaplainUniqueUserId.length - 9)
        patientUniqueUserId = String.format("%010d", BigInteger(UUID.randomUUID().toString().replace("-", ""), 16))
        patientUniqueUserId = patientUniqueUserId.substring(patientUniqueUserId.length - 9)

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
        patientTimeZone = intent.getStringExtra("appointmentTimeZone").toString()

        dbSaveAppointmentForPatient =
            db.collection("patients").document(patientUserId)

        dbSaveAppointmentForChaplain =
            db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        dbSaveAppointmentForMainCollection = db.collection("appointment").document()

        readPatientData()
        readChaplainData()

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
        val appointmentModelClass = AppointmentModelClass(
            appointmentId,
            patientUserId,
            chaplainProfileFieldUserId,
            appointmentPrice,
            patientSelectedTime,
            patientName,
            patientSurname,
            chaplainName,
            chaplainSurname,
            chaplainProfileImageLink,
            chaplainAddressingTitle,
            credentialTitleArrayList,
            chaplainFieldArrayList,
            patientTimeZone,
            chaplainUniqueUserId,
            patientUniqueUserId
        )

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

    private fun readPatientData(){
        dbSaveAppointmentForPatient.get().addOnSuccessListener { document ->
            val result = document.toObject<UserProfile>()
            if (result != null) {
                patientName = result.name.toString()
                patientSurname = result.surname.toString()
            }
        }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }
    private fun readChaplainData(){
        dbSaveAppointmentForChaplain.get().addOnSuccessListener { document ->
            val result = document.toObject<ChaplainUserProfile>()
            if (result != null) {
                chaplainName = result.name.toString()
                chaplainSurname = result.surname.toString()
                chaplainProfileImageLink = result.profileImageLink.toString()
                chaplainAddressingTitle = result.addressingTitle.toString()
                if (!result.credentialsTitle.isNullOrEmpty()){
                    credentialTitleArrayList = result.credentialsTitle
                }
                if (!result.field.isNullOrEmpty()){
                    chaplainFieldArrayList = result.field
                }

            }
        }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentSummaryActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        intent.putExtra("readTime", chaplainEarliestDate)
        intent.putExtra("patientSelectedTime", patientSelectedTime)
        intent.putExtra("appointmentTimeZone", patientTimeZone)
        startActivity(intent)
        finish()
    }
}