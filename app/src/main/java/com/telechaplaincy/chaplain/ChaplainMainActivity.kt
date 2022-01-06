package com.telechaplaincy.chaplain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_future_appointments.ChaplainFutureAppointmentDetailsActivity
import com.telechaplaincy.chaplain_future_appointments.ChaplainFutureAppointmentsAdapterClass
import com.telechaplaincy.chaplain_past_appointments.ChaplainPastAppointmentDetailsActivity
import com.telechaplaincy.chaplain_past_appointments.ChaplainPastAppointmentsAdapterClass
import com.telechaplaincy.chaplain_profile.ChaplainProfileActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainSignUpSecondPart
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentsModelClass
import com.telechaplaincy.patient_notification_page.PatientNotificationActivity
import kotlinx.android.synthetic.main.activity_chaplain_main.*
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import java.util.*
import kotlin.collections.ArrayList

class ChaplainMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference
    private var chaplainCollectionName:String = ""
    private var chaplainProfileCollectionName:String = ""
    private var chaplainProfileDocumentName:String = ""
    private var chaplainProfileFieldUserId:String = ""
    private var chaplainProfileFirstName:String = ""
    private var chaplainProfileLastName:String = ""
    private var chaplainProfileImageLink:String = ""
    private var chaplainProfileAddresTitle:String = ""
    private var chaplainProfileCredentialTitle:String = ""
    private var chaplainProfileFieldPhone:String = ""
    private var chaplainProfileFieldBirthDate:String = ""
    private var chaplainProfileFieldEducation:String = ""
    private var chaplainProfileFieldExperience:String = ""
    private var chaplainProfileFieldChaplainField:String = ""
    private var chaplainProfileFieldPreferredLanguage:String = ""
    private var chaplainProfileFieldSsn:String = ""
    var chaplainCvUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage
    var chaplainCertificateUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage
    private var chaplainAccountStatus = "1"

    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference

    private lateinit var chaplainUserProfile: ChaplainUserProfile


    private lateinit var queryRef: CollectionReference
    private var patientFutureAppointmentsListArray = ArrayList<PatientFutureAppointmentsModelClass>()
    private lateinit var chaplainFutureAppointmentsAdapterClass: ChaplainFutureAppointmentsAdapterClass
    private var patientTimeNow: String = ""

    private var patientPastAppointmentsListArray = ArrayList<PatientFutureAppointmentsModelClass>()
    private lateinit var chaplainPastAppointmentsAdapterClass: ChaplainPastAppointmentsAdapterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_main)

        queryRef = db.collection("chaplains")

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            chaplainProfileFieldUserId = user.uid
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        chaplainCollectionName = getString(R.string.chaplain_collection)
        chaplainProfileCollectionName = getString(R.string.chaplain_profile_collection)
        chaplainProfileDocumentName = getString(R.string.chaplain_profile_document)

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
        chaplainUserProfile = ChaplainUserProfile()

        readData() //to check if the chaplain profile filled in fully or not
        readAccountStatus()//to check chaplain account status

        chaplain_signUp_continue_button.setOnClickListener {
            checkSignUpData()
        }

        chaplain_enter_calendar_button.setOnClickListener {
            val intent = Intent(this, ChaplainCalendarActivity::class.java)
            startActivity(intent)
            finish()
        }

        chaplainFutureAppointmentsAdapterClass =
            ChaplainFutureAppointmentsAdapterClass(patientFutureAppointmentsListArray) { patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass ->
                futureAppointmentsListClickListener(patientFutureAppointmentsModelClass)
            }

        chaplainPastAppointmentsAdapterClass =
            ChaplainPastAppointmentsAdapterClass(patientPastAppointmentsListArray) { patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass ->
                pastAppointmentsListClickListener(patientFutureAppointmentsModelClass)
            }

        if(auth.currentUser != null){
            patientFutureAppointmentsFillRecyclerView()
            patientPastAppointmentsFillRecyclerView()
        }

        // chaplain Future Appointments lists recyclerview
        val patientFutureAppointmentsListsLayoutManager = LinearLayoutManager(this)
        patientFutureAppointmentsListsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        chaplain_future_appointments_recyclerView.layoutManager = patientFutureAppointmentsListsLayoutManager
        chaplain_future_appointments_recyclerView.adapter = chaplainFutureAppointmentsAdapterClass

        // chaplain past Appointments lists recyclerview
        val patientPastAppointmentsListsLayoutManager = LinearLayoutManager(this)
        patientPastAppointmentsListsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        chaplain_past_appointments_recyclerView.layoutManager = patientPastAppointmentsListsLayoutManager
        chaplain_past_appointments_recyclerView.adapter = chaplainPastAppointmentsAdapterClass
    }

    private fun pastAppointmentsListClickListener(patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass) {
        val intent = Intent(this, ChaplainPastAppointmentDetailsActivity::class.java)
        intent.putExtra("appointment_id", patientFutureAppointmentsModelClass.appointmentId)
        startActivity(intent)
        finish()
    }

    private fun patientPastAppointmentsFillRecyclerView(){
        patientTimeNow = System.currentTimeMillis().toString()
        val query =  queryRef.document(chaplainProfileFieldUserId).collection("appointments")
            .whereLessThanOrEqualTo("appointmentDate", patientTimeNow).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    patientPastAppointmentsListArray.add(document.toObject())
                }
                chaplainPastAppointmentsAdapterClass.submitList(patientPastAppointmentsListArray)
                chaplainPastAppointmentsAdapterClass.notifyDataSetChanged()

                if (chaplainPastAppointmentsAdapterClass.itemCount != 0){
                    chaplain_past_appointments_recyclerView.visibility = View.VISIBLE
                    chaplain_past_appointment_cardView.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun patientFutureAppointmentsFillRecyclerView(){
        patientTimeNow = (System.currentTimeMillis()-1800000).toString()
        val query =  queryRef.document(chaplainProfileFieldUserId).collection("appointments")
            .whereGreaterThanOrEqualTo("appointmentDate", patientTimeNow).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    patientFutureAppointmentsListArray.add(document.toObject())
                }
                chaplainFutureAppointmentsAdapterClass.submitList(patientFutureAppointmentsListArray)
                chaplainFutureAppointmentsAdapterClass.notifyDataSetChanged()

                if (chaplainFutureAppointmentsAdapterClass.itemCount != 0){
                    chaplain_future_appointments_recyclerView.visibility = View.VISIBLE
                    chaplain_future_appointment_cardView.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun futureAppointmentsListClickListener(patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass) {
        val intent = Intent(this, ChaplainFutureAppointmentDetailsActivity::class.java)
        intent.putExtra("appointment_id", patientFutureAppointmentsModelClass.appointmentId)
        startActivity(intent)
        finish()
    }


    private fun readAccountStatus(){
        dbSave.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("account", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                chaplainUserProfile = snapshot.toObject<ChaplainUserProfile>()!!
                chaplainAccountStatus = chaplainUserProfile.accountStatus.toString()
                Log.d("account", chaplainAccountStatus)
                if (chaplainAccountStatus == "1"){
                    lineerLayoutChaplainAppWait.visibility = View.VISIBLE
                }else{
                    lineerLayoutChaplainAppWait.visibility = View.GONE
                }
            }
        }
    }

    //this method will read the chaplain profile data from the firebase
    private fun readData(){
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                val chaplainUserProfile = document.toObject<ChaplainUserProfile>()
                if (chaplainUserProfile != null) {
                    if (chaplainUserProfile.addressingTitle != null) {
                        chaplainProfileAddresTitle = chaplainUserProfile.addressingTitle.toString()
                    }

                    chaplainProfileFirstName = chaplainUserProfile.name.toString()

                    chaplainProfileLastName = chaplainUserProfile.surname.toString()

                    chaplainProfileImageLink = chaplainUserProfile.profileImageLink.toString()

                    chaplainProfileFieldPhone = chaplainUserProfile.phone.toString()

                    chaplainProfileFieldBirthDate = chaplainUserProfile.birthDate.toString()

                    chaplainProfileFieldEducation = chaplainUserProfile.education.toString()

                    chaplainProfileFieldExperience = chaplainUserProfile.experience.toString()

                    chaplainProfileFieldPreferredLanguage = chaplainUserProfile.language.toString()

                    chaplainProfileFieldSsn = chaplainUserProfile.ssn.toString()

                    chaplainCvUrl = chaplainUserProfile.cvUrl.toString()

                    chaplainCertificateUrl = chaplainUserProfile.certificateUrl.toString()

                    if (chaplainUserProfile.credentialsTitle != null) {
                        chaplainProfileCredentialTitle = chaplainUserProfile.credentialsTitle.toString()
                    }

                    if (chaplainUserProfile.field != null) {
                        chaplainProfileFieldChaplainField = chaplainUserProfile.field.toString()
                    }
                }
            }
        }
    }

    //This method will check if the chaplain filled in the all necessary fields in the sign up page
    private fun checkSignUpData(){
        if (chaplainProfileAddresTitle != "" && chaplainProfileAddresTitle != "Nothing Selected"
            && chaplainProfileCredentialTitle != ""
            && chaplainProfileFieldPhone != "" && chaplainProfileFieldBirthDate != ""
            && chaplainProfileFieldEducation != "" && chaplainProfileFieldExperience != ""
            && chaplainProfileFieldPreferredLanguage != ""
            && chaplainProfileFieldSsn != "" && chaplainCvUrl != "" && chaplainCertificateUrl != ""
            && chaplainProfileFieldChaplainField != ""){

            chaplain_signUp_continue_button.visibility = View.GONE
            val toast = Toast.makeText(this, "You entered every necessary info. You can change your info from the profile page", Toast.LENGTH_LONG).show()
            /*Log.d("Button", "OK")
            Log.d("Button", chaplainProfileAddresTitle)*/
        }else{
            val intent = Intent(this, ChaplainSignUpSecondPart::class.java)
            startActivity(intent)
            /*Log.d("Button", "NOT OK")
            Log.d("Button a", chaplainProfileAddresTitle)
            Log.d("Button b", chaplainProfileFieldPhone)
            Log.d("Button d", chaplainProfileFieldBirthDate)
            Log.d("Button e", chaplainProfileFieldEducation)
            Log.d("Button f", chaplainProfileFieldExperience)
            Log.d("Button g", chaplainProfileFieldPreferredLanguage)
            Log.d("Button h", chaplainProfileFieldSsn)
            Log.d("Button i", chaplainCvUrl)
            Log.d("Button j", chaplainCertificateUrl)
            Log.d("Button k", chaplainProfileCredentialTitle)*/
        }
    }


    private fun addingActivitiesToBottomMenu(){

        bottomNavigation.selectedItemId = R.id.bottom_menu_home

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_menu_home -> {

                }

                R.id.bottom_menu_notification -> {
                    val intent = Intent(this, PatientNotificationActivity::class.java)
                    intent.putExtra("userType", "chaplain")
                    startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    finish()
                }
                R.id.bottom_menu_profile -> {
                    val intent = Intent(this, ChaplainProfileActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "profile", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            true
        }
    }
}