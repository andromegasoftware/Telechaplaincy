package com.telechaplaincy.patient_profile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.birth_date_class.DateMask
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import java.io.File

class PatientProfileDetailsEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSave: DocumentReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

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
    private var patientProfileImageLink:String = ""
    private var isImageSelected:Boolean = false
    private lateinit var imageFile: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile_details_edit)

        patient_info_progress_bar.visibility = View.GONE

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            patientProfileFieldUserId = user.uid
            patientProfileEmail = user.email.toString()
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        patientCollectionName = getString(R.string.patient_collection)
        patientProfileCollectionName = getString(R.string.patient_profile_collection)
        patientProfileDocumentName = getString(R.string.patient_profile_document)

        dbSave = db.collection(patientCollectionName).document(patientProfileFieldUserId)

        patientEthnicBackgroundSelection()
        patientFaithSelection()
        patientMaritalStatusSelection()
        patientGenderSelection()

        //this part makes editText birth date format like dd/mm/yyyy
        DateMask(patient_personal_info_birth_date_editText).listen()

        patient_personal_info_next_button.setOnClickListener {
            savePatientPersonalInfo()
        }

        patient_appointment_personal_info_image_view.setOnClickListener {
            pickImage()
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
            patientGender, patientMaritalStatus, patientEthnicArrayList, patientFaithArrayList, patientProfileImageLink)
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
            patientGender, patientMaritalStatus, patientEthnicArrayList, patientFaithArrayList, patientProfileImageLink)
        dbSave.set(userProfile, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
                patient_info_progress_bar.visibility = View.GONE
                patient_personal_info_next_button.isClickable = true
                val toast = Toast.makeText(this, getString(R.string.patient_profile_details_edit_activity_button_toast_message), Toast.LENGTH_LONG).show()
                val intent = Intent(this, PatientProfileDetailsActivity::class.java)
                startActivity(intent)
                finish()
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
                    patientProfileImageLink = userProfile.profileImage.toString()
                    if (patientProfileImageLink != "null" && patientProfileImageLink != ""){
                        if (!isImageSelected) {
                            Picasso.get().load(patientProfileImageLink)
                                .placeholder(R.drawable.ic_baseline_account_circle_24)
                                .error(R.drawable.ic_baseline_account_circle_24)
                                .into(patient_appointment_personal_info_image_view)
                        }
                    }
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
                                val chipEthnicBackground = Chip(this@PatientProfileDetailsEditActivity)
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
                                val chipFaith = Chip(this@PatientProfileDetailsEditActivity)
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
                val chipPatientEthnic= Chip(this@PatientProfileDetailsEditActivity)
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
                    val builder= AlertDialog.Builder(this@PatientProfileDetailsEditActivity)
                    builder.setTitle(R.string.chaplain_sign_up_ethnic_title_textView_text)

                    // Set up the input
                    val input = EditText(this@PatientProfileDetailsEditActivity)
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
                val chipPatientFaith= Chip(this@PatientProfileDetailsEditActivity)
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
                    val builder= AlertDialog.Builder(this@PatientProfileDetailsEditActivity)
                    builder.setTitle(R.string.chaplain_sign_up_chaplaincy_faith)

                    // Set up the input
                    val input = EditText(this@PatientProfileDetailsEditActivity)
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
        val intent = Intent(this, PatientProfileDetailsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun uploadImage(){
        val ref = storageReference.child("patient").child(patientProfileFieldUserId)
            .child("profile photo").child("Patient Profile Photo")

        val uploadTask = ref.putFile(imageFile)
            .addOnFailureListener {
                Log.d("Image: ", "Image could not uploaded")
            }
            .addOnSuccessListener {
                Log.d("Image: ", "Image uploaded")
            }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                patientProfileImageLink = task.result.toString()
            } else {
                // Handle failures
                // ...
            }
        }

    }

    private fun pickImage() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            intent.putExtra("crop", "true")
            intent.putExtra("scale", true)
            intent.putExtra("aspectX", 16)
            intent.putExtra("aspectY", 16)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }
            val uri = data?.data
            if (uri != null) {
                imageFile = uri
                uploadImage()
            }
            if (uri != null) {
                val imageBitmap = uriToBitmap(uri)
                isImageSelected = true
                patient_appointment_personal_info_image_view.setImageBitmap(imageBitmap)

            }
        }

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && PICK_IMAGE_REQUEST_CODE == 1000) {
                    // pick image after request permission success
                    pickImage()
                }

            }
        }
    }

    private fun uriToImageFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val filePath = cursor.getString(columnIndex)
                cursor.close()
                return File(filePath)
            }
            cursor.close()
        }
        return null
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    }

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1000
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
    }

}