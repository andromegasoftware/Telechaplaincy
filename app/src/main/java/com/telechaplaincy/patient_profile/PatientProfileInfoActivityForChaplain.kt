package com.telechaplaincy.patient_profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_future_appointments.ChaplainFutureAppointmentDetailsActivity
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_patient_profile_info_for_chaplain.*

class PatientProfileInfoActivityForChaplain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbSave: DocumentReference

    private val db = Firebase.firestore
    private var patientCollectionName:String = ""
    private var patientProfileCollectionName:String = ""
    private var patientProfileDocumentName:String = ""
    private var patientProfileFieldUserId:String = ""

    private var patientProfileFirstName:String = ""
    private var patientProfileLastName:String = ""
    private var patientProfileEmail:String = ""
    private var patientProfilePhone:String = ""
    private var patientProfileBirthDate:String = ""
    private var patientEthnicArrayList = ArrayList<String>()
    private var patientFaithArrayList = ArrayList<String>()
    private var patientProfileOccupation:String = ""
    private var patientProfileEducation:String = ""
    private var patientProfileLanguage:String = ""
    private var patientProfileCountry:String = ""
    private var patientGender:String = ""
    private var patientMaritalStatus:String = ""
    private var patientEthnic:String = ""
    private var patientFaith:String = ""
    private var patientProfileImageLink:String = ""

    private var appointmentId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile_info_for_chaplain)

        patientProfileFieldUserId = intent.getStringExtra("patientUserId").toString()
        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()

        patientCollectionName = getString(R.string.patient_collection)
        patientProfileCollectionName = getString(R.string.patient_profile_collection)
        patientProfileDocumentName = getString(R.string.patient_profile_document)

        dbSave = db.collection(patientCollectionName).document(patientProfileFieldUserId)

        readPatientPersonalInfo()

    }

    //this function will read the patient personal info from the firestore when the user opening the profile page
    private fun readPatientPersonalInfo(){
        dbSave.get().addOnSuccessListener { document ->
            if (document != null){
                val userProfile = document.toObject<UserProfile>()
                if (userProfile != null) {
                    patientProfileImageLink = userProfile.profileImage.toString()
                    if (patientProfileImageLink != "null" && patientProfileImageLink != "") {
                        Picasso.get().load(patientProfileImageLink)
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .error(R.drawable.ic_baseline_account_circle_24)
                            .into(patient_info_activity_profile_image)
                    }
                    patientProfileFirstName = userProfile.name.toString()

                    patientProfileLastName = userProfile.surname.toString()
                    if (patientProfileLastName != "null"){
                        patient_info_activity_patient_name_textView.text = "$patientProfileFirstName $patientProfileLastName"
                    }

                    patientProfileBirthDate = userProfile.birthDate.toString()
                    if (patientProfileBirthDate != "null"){
                        patient_info_activity_patient_birth_date_textView.text = patientProfileBirthDate
                    }

                    patientGender = userProfile.gender.toString()
                    patient_info_activity_patient_gender_textView.text = patientGender

                    patientMaritalStatus = userProfile.maritalStatus.toString()
                    patient_info_activity_patient_marital_status_textView.text = patientMaritalStatus

                    if (userProfile.ethnic != null) {
                        patientEthnicArrayList = userProfile.ethnic
                        for (k in patientEthnicArrayList.indices) {
                            if (patientEthnicArrayList[k] != "Nothing Selected") {
                                patientEthnic += patientEthnicArrayList[k] + " "
                            }
                        }
                        patient_info_activity_patient_ethnicity_textView.text = patientEthnic
                    }

                    if (userProfile.faith != null) {
                        patientFaithArrayList = userProfile.faith
                        for (k in patientFaithArrayList.indices) {
                            if (patientFaithArrayList[k] != "Nothing Selected") {
                                patientFaith += patientFaithArrayList[k] + " "
                            }
                        }
                        patient_info_activity_patient_faith_textView.text = patientFaith
                    }

                    patientProfileOccupation = userProfile.occupation.toString()
                    if (patientProfileOccupation != "null"){
                        patient_info_activity_patient_occupation_textView.text = patientProfileOccupation
                    }

                    patientProfileEducation = userProfile.education.toString()
                    if (patientProfileEducation != "null"){
                        patient_info_activity_patient_education_textView.text = patientProfileEducation
                    }

                    patientProfileLanguage = userProfile.language.toString()
                    if (patientProfileLanguage != "null"){
                        patient_info_activity_patient_language_textView.text =
                            patientProfileLanguage
                    }

                    patientProfileCountry = userProfile.country.toString()
                    if (patientProfileCountry != "null"){
                        patient_info_activity_patient_country_textView.text = patientProfileCountry
                    }
                }
            }else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainFutureAppointmentDetailsActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }
}