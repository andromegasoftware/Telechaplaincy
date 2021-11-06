package com.telechaplaincy.patient_profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.birth_date_class.DateMask
import com.telechaplaincy.patient.CategorySelection
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*

class PatientAppointmentPersonalInfo : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSave: DocumentReference

    private var userName: String = ""
    private var userSurName: String = ""

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_personal_info)

        patient_info_progress_bar.visibility = View.GONE

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            patientProfileFieldUserId = user.uid
            patientProfileEmail = user.email.toString()
        }

        patientCollectionName = getString(R.string.patient_collection)
        patientProfileCollectionName = getString(R.string.patient_profile_collection)
        patientProfileDocumentName = getString(R.string.patient_profile_document)

        dbSave = db.collection(patientCollectionName).document(patientProfileFieldUserId)
            .collection(patientProfileCollectionName).document(patientProfileDocumentName)

        patientEthnicBackgroundSelection()
        patientFaithSelection()
        patientMaritalStatusSelection()
        patientGenderSelection()

        //this part makes editText birth date format like dd/mm/yyyy
        DateMask(patient_personal_info_birth_date_editText).listen()

        patient_personal_info_cancel_button.setOnClickListener {
            val intent = Intent(this, PatientMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        patient_personal_info_next_button.setOnClickListener {
            savePatientPersonalInfo()
            val intent = Intent(this, CategorySelection::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        savePatientPersonalInfoOnPause()
    }

    override fun onStart() {
        super.onStart()
        readPatientPersonalInfo()
    }

    //this function will save the patient personal info to the firestore when the user making the appointment
    private fun savePatientPersonalInfoOnPause(){
        takePatientProfileInfo()//first we will take data what the user entered, then save it to the firestore
        val userProfile = UserProfile(patientProfileFirstName, patientProfileLastName,
            patientProfileEmail, patientProfileFieldUserId, patientProfileBirthDate, patientProfilePhone,
            patientProfileOccupation, patientProfileEducation, patientProfileLanguage, patientProfileCountry,
            patientGender, patientMaritalStatus, patientEthnicArrayList, patientFaithArrayList)
        dbSave.set(userProfile, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")

                patientEthnicArrayList.clear()
                patientFaithArrayList.clear()
                patient_ethnic_chip_group.removeAllViews()
                patient_faith_chip_group.removeAllViews()
            }
            .addOnFailureListener {
                    e -> Log.w("data upload", "Error writing document", e)
            }
    }

    //this function will save the patient personal info to the firestore when the user making the appointment
    private fun savePatientPersonalInfo(){
        patient_info_progress_bar.visibility = View.VISIBLE
        patient_personal_info_next_button.isClickable = false
        takePatientProfileInfo()//first we will take data what the user entered, then save it to the firestore
        val userProfile = UserProfile(patientProfileFirstName, patientProfileLastName,
            patientProfileEmail, patientProfileFieldUserId, patientProfileBirthDate, patientProfilePhone,
            patientProfileOccupation, patientProfileEducation, patientProfileLanguage, patientProfileCountry,
            patientGender, patientMaritalStatus, patientEthnicArrayList, patientFaithArrayList)
        dbSave.set(userProfile, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
                patient_info_progress_bar.visibility = View.GONE
                patient_personal_info_next_button.isClickable = true
            }
            .addOnFailureListener {
                    e -> Log.w("data upload", "Error writing document", e)
                patient_info_progress_bar.visibility = View.GONE
                patient_personal_info_next_button.isClickable = true
            }
    }

    //this function will read the patient personal info from the firestore when the user opening the appointment page
    private fun readPatientPersonalInfo(){
        progressBarPersonalInfoPageMain.visibility = View.VISIBLE
        dbSave.get().addOnSuccessListener { document ->
            progressBarPersonalInfoPageMain.visibility = View.GONE
            if (document != null){
                val userProfile = document.toObject<UserProfile>()
                if (userProfile != null) {
                    patientProfileFirstName = userProfile.name.toString()
                    if (patientProfileFirstName != "null"){
                        patient_personal_info_first_name_editText.setText(patientProfileFirstName)
                    }
                    patientProfileLastName = userProfile.surname.toString()
                    if (patientProfileLastName != "null"){
                        patient_personal_info_last_name_editText.setText(patientProfileLastName)
                    }

                    patientProfileBirthDate = userProfile.birthDate.toString()
                    if (patientProfileBirthDate != "null"){
                        patient_personal_info_birth_date_editText.setText(patientProfileBirthDate)
                    }

                    patientProfilePhone = userProfile.phone.toString()
                    if (patientProfilePhone != "null"){
                        patient_personal_info_phone_number_editText.setText(patientProfilePhone)
                    }

                    patientProfileOccupation = userProfile.occupation.toString()
                    if (patientProfileOccupation != "null"){
                        patient_personal_info_occupation_editText.setText(patientProfileOccupation)
                    }

                    patientProfileEducation = userProfile.education.toString()
                    if (patientProfileEducation != "null"){
                        patient_personal_info_education_editText.setText(patientProfileEducation)
                    }

                    patientProfileLanguage = userProfile.language.toString()
                    if (patientProfileLanguage != "null"){
                        patient_personal_info_language_editText.setText(patientProfileLanguage)
                    }

                    patientProfileCountry = userProfile.country.toString()
                    if (patientProfileCountry != "null"){
                        patient_personal_info_country_editText.setText(patientProfileCountry)
                    }

                    patientGender = userProfile.gender.toString()
                    val optionGender = resources.getStringArray(R.array.genderArray)
                    spinner_patient_gender.setSelection(optionGender.indexOf(patientGender))

                    patientMaritalStatus = userProfile.maritalStatus.toString()
                    val optionMaritalStatus = resources.getStringArray(R.array.maritalStatusArray)
                    spinner_patient_marital_status.setSelection(
                        optionMaritalStatus.indexOf(
                            patientMaritalStatus
                        )
                    )

                    if (userProfile.ethnic != null) {
                        patientEthnicArrayList = userProfile.ethnic
                        for (k in patientEthnicArrayList.indices) {
                            if (patientEthnicArrayList[k] != "Nothing Selected") {
                                val chipEthnicBackground = Chip(this@PatientAppointmentPersonalInfo)
                                chipEthnicBackground.isCloseIconVisible = true
                                chipEthnicBackground.setChipBackgroundColorResource(R.color.colorAccent)
                                chipEthnicBackground.setTextColor(resources.getColor(R.color.colorPrimary))
                                chipEthnicBackground.text = patientEthnicArrayList[k]
                                patient_ethnic_chip_group.addView(chipEthnicBackground)

                                chipEthnicBackground.setOnClickListener {
                                    patient_ethnic_chip_group.removeView(chipEthnicBackground)
                                    patientEthnicArrayList.remove(chipEthnicBackground.text)
                                }
                            }
                        }
                    }

                    if (userProfile.faith != null) {
                        patientFaithArrayList = userProfile.faith
                        for (k in patientFaithArrayList.indices) {
                            if (patientFaithArrayList[k] != "Nothing Selected") {
                                val chipFaith = Chip(this@PatientAppointmentPersonalInfo)
                                chipFaith.isCloseIconVisible = true
                                chipFaith.setChipBackgroundColorResource(R.color.colorAccent)
                                chipFaith.setTextColor(resources.getColor(R.color.colorPrimary))
                                chipFaith.text = patientFaithArrayList[k]
                                patient_faith_chip_group.addView(chipFaith)

                                chipFaith.setOnClickListener {
                                    patient_faith_chip_group.removeView(chipFaith)
                                    patientFaithArrayList.remove(chipFaith.text)
                                }
                            }
                        }
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

    //patient Ethnic Background spinner item selection function
    private fun patientEthnicBackgroundSelection(){
        val optionEthnic = resources.getStringArray(R.array.ethnicBackgroundArray)
        spinner_patient_ethnic.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionEthnic
        )
        spinner_patient_ethnic.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                patientEthnic = optionEthnic[position]
                val chipPatientEthnic= Chip(this@PatientAppointmentPersonalInfo)
                chipPatientEthnic.isCloseIconVisible = true
                chipPatientEthnic.setChipBackgroundColorResource(R.color.colorAccent)
                chipPatientEthnic.setTextColor(resources.getColor(R.color.colorPrimary))
                if (patientEthnic != "Nothing Selected" && patientEthnic != "Other"){
                    chipPatientEthnic.text = patientEthnic
                    patient_ethnic_chip_group.addView(chipPatientEthnic)
                    patientEthnicArrayList.add(patientEthnic)
                }
                else if (patientEthnic == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@PatientAppointmentPersonalInfo)
                    builder.setTitle(R.string.chaplain_sign_up_ethnic_title_textView_text)

                    // Set up the input
                    val input = EditText(this@PatientAppointmentPersonalInfo)
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.hint = getString(R.string.chaplain_sign_up_ethnic_title_textView_text)
                    input.inputType = InputType.TYPE_CLASS_TEXT
                    builder.setView(input)

                    // Set up the buttons
                    builder.setPositiveButton(
                        getString(R.string.chaplain_sign_up_alert_ok_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            // Here you get get input text from the Edittext
                            val selection = input.text.toString()

                            //assign input to chip and add chip
                            chipPatientEthnic.text = selection
                            patientEthnic = selection
                            patient_ethnic_chip_group.addView(chipPatientEthnic)
                            Log.d("otherSelection: ", selection)
                            patientEthnicArrayList.add(patientEthnic)
                        })
                    builder.setNegativeButton(
                        getString(R.string.chaplain_sign_up_alert_cancel_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })

                    builder.show()
                }
                chipPatientEthnic.setOnClickListener {
                    patient_ethnic_chip_group.removeView(chipPatientEthnic)
                    patientEthnicArrayList.remove(chipPatientEthnic.text)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    //patient faith spinner item selection function
    private fun patientFaithSelection(){
        val optionFaith = resources.getStringArray(R.array.faithAffiliationArray)
        spinner_patient_faith.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionFaith
        )
        spinner_patient_faith.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                patientFaith = optionFaith[position]
                val chipPatientFaith= Chip(this@PatientAppointmentPersonalInfo)
                chipPatientFaith.isCloseIconVisible = true
                chipPatientFaith.setChipBackgroundColorResource(R.color.colorAccent)
                chipPatientFaith.setTextColor(resources.getColor(R.color.colorPrimary))
                if (patientFaith != "Nothing Selected" && patientFaith != "Other"){
                    chipPatientFaith.text = patientFaith
                    patient_faith_chip_group.addView(chipPatientFaith)
                    patientFaithArrayList.add(patientFaith)
                }
                else if (patientFaith == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@PatientAppointmentPersonalInfo)
                    builder.setTitle(R.string.chaplain_sign_up_chaplaincy_faith)

                    // Set up the input
                    val input = EditText(this@PatientAppointmentPersonalInfo)
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.hint = getString(R.string.chaplain_sign_up_chaplaincy_faith)
                    input.inputType = InputType.TYPE_CLASS_TEXT
                    builder.setView(input)

                    // Set up the buttons
                    builder.setPositiveButton(
                        getString(R.string.chaplain_sign_up_alert_ok_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            // Here you get get input text from the Edittext
                            val selection = input.text.toString()

                            //assign input to chip and add chip
                            chipPatientFaith.text = selection
                            patientFaith = selection
                            patient_faith_chip_group.addView(chipPatientFaith)
                            Log.d("otherSelection: ", selection)
                            patientFaithArrayList.add(patientFaith)
                        })
                    builder.setNegativeButton(
                        getString(R.string.chaplain_sign_up_alert_cancel_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })

                    builder.show()
                }
                chipPatientFaith.setOnClickListener {
                    patient_faith_chip_group.removeView(chipPatientFaith)
                    patientFaithArrayList.remove(chipPatientFaith.text)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    //marital status spinner item selection function
    private fun patientMaritalStatusSelection(){
        val optionPatientMaritalStatus = resources.getStringArray(R.array.maritalStatusArray)
        spinner_patient_marital_status.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionPatientMaritalStatus
        )
        spinner_patient_marital_status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                patientMaritalStatus = optionPatientMaritalStatus[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    //sex/gender spinner item selection function
    private fun patientGenderSelection(){
        val optionPatientGender = resources.getStringArray(R.array.genderArray)
        spinner_patient_gender.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionPatientGender
        )
        spinner_patient_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                patientGender = optionPatientGender[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun takePatientProfileInfo(){
        patientProfileFirstName = patient_personal_info_first_name_editText.text.toString()
        patientProfileLastName = patient_personal_info_last_name_editText.text.toString()
        patientProfilePhone = patient_personal_info_phone_number_editText.text.toString()
        patientProfileOccupation = patient_personal_info_occupation_editText.text.toString()
        patientProfileEducation = patient_personal_info_education_editText.text.toString()
        patientProfileLanguage = patient_personal_info_language_editText.text.toString()
        patientProfileCountry = patient_personal_info_country_editText.text.toString()
        patientProfileBirthDate = patient_personal_info_birth_date_editText.text.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}