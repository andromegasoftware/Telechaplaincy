package com.telechaplaincy.appointment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import java.util.HashMap

class PatientAppointmentSummaryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAvailableTimes: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId:String = ""
    private var chaplainCollectionName:String = ""

    private var chaplainCategory = ""
    private var chaplainProfileFieldUserId:String = ""
    private var chaplainEarliestDate: String = ""
    private var patientSelectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_summary)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()
        chaplainEarliestDate = intent.getStringExtra("readTime").toString()
        patientSelectedTime = intent.getStringExtra("patientSelectedTime").toString()

        dbSaveAvailableTimes = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
            .collection("chaplainTimes").document("availableTimes")
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
    private fun appointmentTimeTakeBack(){
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
}