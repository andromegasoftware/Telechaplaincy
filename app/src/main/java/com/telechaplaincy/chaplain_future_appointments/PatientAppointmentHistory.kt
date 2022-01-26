package com.telechaplaincy.chaplain_future_appointments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentsModelClass
import com.telechaplaincy.patient_past_appointments.PatientPastAppointmentAdapterClass
import kotlinx.android.synthetic.main.activity_patient_appointment_history.*

class PatientAppointmentHistory : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var patientUserId: String = ""
    private lateinit var queryRef: CollectionReference
    private var patientTimeNow: String = ""
    private var appointmentId: String = ""

    private var patientPastAppointmentsListArray = ArrayList<PatientFutureAppointmentsModelClass>()
    private lateinit var patientPastAppointmentsAdapterClass: PatientPastAppointmentAdapterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_history)

        auth = FirebaseAuth.getInstance()
        queryRef = db.collection("patients")

        appointmentId = intent.getStringExtra("appointment_id").toString()
        patientUserId = intent.getStringExtra("patientUserId").toString()

        patientPastAppointmentsFillRecyclerView()

        patientPastAppointmentsAdapterClass =
            PatientPastAppointmentAdapterClass(patientPastAppointmentsListArray) { patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass ->
                pastAppointmentsListClickListener(patientFutureAppointmentsModelClass)
            }

        // patient past Appointments lists recyclerview
        val patientPastAppointmentsListsLayoutManager = LinearLayoutManager(this)
        //patientPastAppointmentsListsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        patient_appointment_history_recyclerView.layoutManager =
            patientPastAppointmentsListsLayoutManager
        patient_appointment_history_recyclerView.adapter = patientPastAppointmentsAdapterClass

    }

    private fun patientPastAppointmentsFillRecyclerView() {
        patientTimeNow = System.currentTimeMillis().toString()
        val query = queryRef.document(patientUserId).collection("appointments")
            .whereLessThanOrEqualTo("appointmentDate", patientTimeNow)
            .orderBy("appointmentDate", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    patientPastAppointmentsListArray.add(document.toObject())
                }
                patientPastAppointmentsAdapterClass.submitList(patientPastAppointmentsListArray)
                patientPastAppointmentsAdapterClass.notifyDataSetChanged()

                if (patientPastAppointmentsAdapterClass.itemCount != 0) {
                    patient_appointment_history_recyclerView.visibility = View.VISIBLE
                    patient_appointment_history_cardView.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun pastAppointmentsListClickListener(patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass) {
        val intent = Intent(this, PatientAppointmentHistoryDetailsActivity::class.java)
        intent.putExtra("appointment_id", patientFutureAppointmentsModelClass.appointmentId)
        intent.putExtra("patientUserId", patientUserId)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainFutureAppointmentDetailsActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        intent.putExtra("patientUserId", patientUserId)
        startActivity(intent)
        finish()
    }
}