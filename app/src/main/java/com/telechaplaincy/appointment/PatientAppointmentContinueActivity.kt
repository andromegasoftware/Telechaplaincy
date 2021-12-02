package com.telechaplaincy.appointment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_selection.chaplains_selection.ChaplainsListedSelectionActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import kotlinx.android.synthetic.main.activity_patient_appointment_continue.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class PatientAppointmentContinueActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var chaplainProfileFieldUserId:String = ""

    private var chaplainCollectionName: String = ""

    private var chaplainProfileImageLink:String = ""
    private var chaplainProfileFirstName:String = ""
    private var chaplainProfileLastName:String = ""
    private var chaplainProfileAddressTitle:String = ""
    private var chaplainProfileCredentialTitle:String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileFieldChaplainField:String = ""
    private var chaplainFieldArrayList = ArrayList<String>()
    private var chaplainProfileFieldChaplainFaith:String = ""
    private var faithArrayList = ArrayList<String>()
    private var chaplainProfileFieldChaplainEthnic:String = ""
    private var ethnicArrayList = ArrayList<String>()
    private var chaplainProfileFieldPreferredLanguage:String = ""
    private var chaplainProfileFieldOtherLanguage:String = ""
    private var otherLanguagesArrayList = ArrayList<String>()
    private var chaplainProfileFieldEducation:String = ""
    private var chaplainProfileFieldExperience:String = ""
    private var chaplainProfileAdditionalCridentials: String = ""
    private var chaplainProfileExplanation:String = ""
    private var chaplainCategory = ""
    private var chaplainAvailableTimesArray = ArrayList<String>()
    private var readTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_continue)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        chaplainCollectionName = getString(R.string.chaplain_collection)

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        patient_appointment_page_chaplain_select_button.setOnClickListener {
            if (readTime != ""){
                val intent = Intent(this, PatientAppointmentTimeSelectionActivity::class.java)
                intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
                intent.putExtra("chaplain_category", chaplainCategory)
                intent.putExtra("readTime", readTime)
                startActivity(intent)
                finish()
            }else{
                val toast = Toast.makeText(this, R.string.chaplain_time_empty_button_toast_message,
                Toast.LENGTH_LONG).show()
            }

        }

        readChaplainInfo()
        readChaplainEarliestDate()
    }

    private fun readChaplainInfo(){
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<ChaplainUserProfile>()
                    if (result != null) {
                        if (result.profileImageLink != null){
                            chaplainProfileImageLink = result.profileImageLink.toString()
                            Picasso.get().load(chaplainProfileImageLink).into(patient_appointment_page_chaplain_profile_image)
                        }
                        if (result.name != null){
                            chaplainProfileFirstName = result.name.toString()

                        }
                        if (result.surname != null){
                            chaplainProfileLastName = result.surname.toString()
                        }
                        if (result.addressingTitle != null){
                            chaplainProfileAddressTitle = result.addressingTitle.toString()
                        }
                        if (result.credentialsTitle != null){
                            credentialTitleArrayList = result.credentialsTitle
                        }
                        if (!credentialTitleArrayList.isNullOrEmpty()){
                            for (k in credentialTitleArrayList){
                                chaplainProfileCredentialTitle += ", $k"
                            }
                            patient_appointment_page_chaplain_name.text = chaplainProfileAddressTitle+ " " +
                                    chaplainProfileFirstName+ " "+ chaplainProfileLastName + chaplainProfileCredentialTitle
                        }

                        if (result.field != null){
                            chaplainFieldArrayList = result.field
                            if (!chaplainFieldArrayList.isNullOrEmpty()){
                                for (k in chaplainFieldArrayList){
                                    chaplainProfileFieldChaplainField += "$k "
                                }
                                patient_appointment_page_chaplain_field.text = chaplainProfileFieldChaplainField
                            }
                        }
                        if (result.faith != null){
                            faithArrayList = result.faith
                            if (!faithArrayList.isNullOrEmpty()){
                                for (k in faithArrayList){
                                    chaplainProfileFieldChaplainFaith += "$k "
                                }
                                patient_appointment_page_chaplain_faith.text = chaplainProfileFieldChaplainFaith
                            }
                        }
                        if (result.ethnic != null){
                            ethnicArrayList = result.ethnic
                            if (!ethnicArrayList.isNullOrEmpty()){
                                for (k in ethnicArrayList){
                                    chaplainProfileFieldChaplainEthnic += "$k "
                                }
                                patient_appointment_page_chaplain_ethnic_background.text = chaplainProfileFieldChaplainEthnic
                            }
                        }
                        if (result.language != null){
                            chaplainProfileFieldPreferredLanguage = result.language.toString()
                            if (chaplainProfileFieldPreferredLanguage != "null"){
                                patient_appointment_page_chaplain_main_language.text = chaplainProfileFieldPreferredLanguage
                            }
                        }
                        if (result.otherLanguages != null){
                            otherLanguagesArrayList = result.otherLanguages
                            if (!otherLanguagesArrayList.isNullOrEmpty()){
                                for (k in otherLanguagesArrayList){
                                    chaplainProfileFieldOtherLanguage += "$k "
                                }
                                patient_appointment_page_chaplain_other_language.text = chaplainProfileFieldOtherLanguage
                            }
                        }
                        if (result.education != null){
                            chaplainProfileFieldEducation = result.education.toString()
                            if (chaplainProfileFieldEducation != "null"){
                                patient_appointment_page_chaplain_education.text = chaplainProfileFieldEducation
                            }
                        }
                        if (result.experience != null){
                            chaplainProfileFieldExperience = result.experience.toString()
                            if (chaplainProfileFieldExperience != "null"){
                                patient_appointment_page_chaplain_experience.text = chaplainProfileFieldExperience
                            }
                        }
                        if (result.additionalCredential != null){
                            chaplainProfileAdditionalCridentials = result.additionalCredential.toString()
                            if (chaplainProfileAdditionalCridentials != "null"){
                                patient_appointment_page_chaplain_additional_cri.text = chaplainProfileAdditionalCridentials
                            }
                        }
                        if (result.bio != null){
                            chaplainProfileExplanation = result.bio.toString()
                            if (chaplainProfileExplanation != "null"){
                                patient_appointment_page_chaplain_bio.text = chaplainProfileExplanation
                            }
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

    private fun readChaplainEarliestDate(){
        dbSave.collection("chaplainTimes").document("availableTimes").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val chaplainAvailableTimes = document.data
                    if (chaplainAvailableTimes != null) {
                        for ((key) in chaplainAvailableTimes) {
                            chaplainAvailableTimesArray.add(key)
                        }
                        readTime = Collections.min(chaplainAvailableTimesArray)
                        //Log.d("TAG", "data: $chaplainAvailableTimes")
                        val dateFormatLocalZone = SimpleDateFormat("dd-MM-yyyy HH:mm")
                        val patientTimeZone = TimeZone.getDefault().id
                        dateFormatLocalZone.timeZone =
                            TimeZone.getTimeZone(ZoneId.of(patientTimeZone))
                        val dateLocale = dateFormatLocalZone.format(Date(readTime.toLong()))
                        patient_appointment_page_earliest_date.text =
                            getString(R.string.earliest_appointment) + dateLocale
                        //Log.d("chaplainArray", chaplainAvailableTimesArray.toString())
                        Log.d("TAG", "DocumentSnapshot data: $readTime")
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
        val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
        intent.putExtra("chaplain_category", chaplainCategory)
        startActivity(intent)
        finish()
    }
}