package com.telechaplaincy.patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import com.telechaplaincy.admin.AdminMainActivity
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentDetailActivity
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentsAdapterClass
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentsModelClass
import com.telechaplaincy.patient_notification_page.PatientNotificationActivity
import com.telechaplaincy.patient_past_appointments.PatientPastAppointmentAdapterClass
import com.telechaplaincy.patient_past_appointments.PatientPastAppointmentsDetailActivity
import com.telechaplaincy.patient_profile.PatientAppointmentPersonalInfo
import com.telechaplaincy.patient_profile.PatientProfileActivity
import kotlinx.android.synthetic.main.activity_patient_main.*

class PatientMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var userRole:String = ""
    private var userId:String = ""
    private lateinit var queryRef: CollectionReference
    private var patientFutureAppointmentsListArray = ArrayList<PatientFutureAppointmentsModelClass>()
    private lateinit var patientFutureAppointmentsAdapterClass: PatientFutureAppointmentsAdapterClass
    private var patientTimeNow: String = ""

    private var patientPastAppointmentsListArray = ArrayList<PatientFutureAppointmentsModelClass>()
    private lateinit var patientPastAppointmentsAdapterClass: PatientPastAppointmentAdapterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_main)
        progressBarPatientMainActivity.bringToFront()

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: userId

        queryRef = db.collection("patients")

        patientFutureAppointmentsAdapterClass =
            PatientFutureAppointmentsAdapterClass(patientFutureAppointmentsListArray) { patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass ->
                futureAppointmentsListClickListener(patientFutureAppointmentsModelClass)
            }

        patientPastAppointmentsAdapterClass =
            PatientPastAppointmentAdapterClass(patientPastAppointmentsListArray) { patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass ->
                pastAppointmentsListClickListener(patientFutureAppointmentsModelClass)
            }
        if(auth.currentUser != null){
            patientFutureAppointmentsFillRecyclerView()
            patientPastAppointmentsFillRecyclerView()
        }

        // patient Future Appointments lists recyclerview
        val patientFutureAppointmentsListsLayoutManager = LinearLayoutManager(this)
        patientFutureAppointmentsListsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        patient_future_appointments_recyclerView.layoutManager = patientFutureAppointmentsListsLayoutManager
        patient_future_appointments_recyclerView.adapter = patientFutureAppointmentsAdapterClass

        // patient past Appointments lists recyclerview
        val patientPastAppointmentsListsLayoutManager = LinearLayoutManager(this)
        patientPastAppointmentsListsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        patient_past_appointments_recyclerView.layoutManager = patientPastAppointmentsListsLayoutManager
        patient_past_appointments_recyclerView.adapter = patientPastAppointmentsAdapterClass

        patient_new_appointment_button.setOnClickListener {
            val intent = Intent(this, PatientAppointmentPersonalInfo::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun patientPastAppointmentsFillRecyclerView(){
        patientTimeNow = System.currentTimeMillis().toString()
        val query =  queryRef.document(userId).collection("appointments")
            .whereLessThanOrEqualTo("appointmentDate", patientTimeNow).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    patientPastAppointmentsListArray.add(document.toObject())
                }
                patientPastAppointmentsAdapterClass.submitList(patientPastAppointmentsListArray)
                patientPastAppointmentsAdapterClass.notifyDataSetChanged()

                if (patientPastAppointmentsAdapterClass.itemCount != 0){
                    patient_past_appointments_recyclerView.visibility = View.VISIBLE
                    past_appointment_cardView.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun patientFutureAppointmentsFillRecyclerView(){
        patientTimeNow = (System.currentTimeMillis()-1800000).toString()
           val query =  queryRef.document(userId).collection("appointments")
               .whereGreaterThanOrEqualTo("appointmentDate", patientTimeNow).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        patientFutureAppointmentsListArray.add(document.toObject())
                    }
                    patientFutureAppointmentsAdapterClass.submitList(patientFutureAppointmentsListArray)
                    patientFutureAppointmentsAdapterClass.notifyDataSetChanged()

                    if (patientFutureAppointmentsAdapterClass.itemCount != 0){
                        patient_future_appointments_recyclerView.visibility = View.VISIBLE
                        future_appointment_cardView.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("deneme", "Error getting documents: ", exception)
                }
    }

    private fun futureAppointmentsListClickListener(patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass) {
        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", patientFutureAppointmentsModelClass.appointmentId)
        startActivity(intent)
        finish()
    }

    private fun pastAppointmentsListClickListener(patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass) {
        val intent = Intent(this, PatientPastAppointmentsDetailActivity::class.java)
        intent.putExtra("appointment_id", patientFutureAppointmentsModelClass.appointmentId)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            checkUserRole()
            //Log.d("TAG", "patient main not null")
        }else{
            val intent = Intent(this, MainEntry::class.java)
            startActivity(intent)
            finish()
           // Log.d("TAG", "patient main null")
        }
    }

    private fun checkUserRole(){
        userId = auth.currentUser?.uid ?: userId
        val docRef = db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                progressBarPatientMainActivity.visibility = View.GONE
                if (document != null) {
                    if (document.data?.containsKey("userRole") == true){
                        val data = document.data as Map<String, String>
                        userRole = data["userRole"].toString()
                        if (userRole == "2") {
                            val intent = Intent(this, ChaplainMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (userRole == "3") {
                            val intent = Intent(this, AdminMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    Log.d("TAG", "DocumentSnapshot data: $userRole")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //progressBarPatientMain.visibility = View.GONE
                Log.d("TAG", "get failed with ", exception)
            }

    }

    private fun addingActivitiesToBottomMenu(){

        bottomNavigation.selectedItemId = R.id.bottom_menu_home

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_menu_home -> {
                    //val intent = Intent(this, PatientMainActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "main", Toast.LENGTH_LONG).show()
                    //finish()
                }

                R.id.bottom_menu_notification -> {
                    val intent = Intent(this, PatientNotificationActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    finish()
                }
                R.id.bottom_menu_profile -> {
                    val intent = Intent(this, PatientProfileActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "profile", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            true
        }
    }
}