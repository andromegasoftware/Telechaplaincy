package com.telechaplaincy.chaplain_sign_activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_chaplain_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.File


class ChaplainSignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user:FirebaseUser
    private lateinit var storageReference: StorageReference

    private var userName: String = ""
    private var userSurName: String = ""
    private var userEmail : String = ""
    private var userPassword: String = ""
    private var userPasswordAgain: String = ""
    private var termsChecked: Boolean = false

    private val db = Firebase.firestore
    private var chaplainCollectionName:String = ""
    private var chaplainProfileCollectionName:String = ""
    private var chaplainProfileDocumentName:String = ""

    private var chaplainProfileFieldName:String = ""
    private var chaplainProfileFieldSurname:String = ""
    private var chaplainProfileFieldEmail:String = ""
    private var chaplainProfileFieldUserId:String = ""
    private var isImageEmpty:Boolean = false
    private var chaplainProfileAddresTitle:String = ""
    private var chaplainProfileCredentialTitle:String = ""
    private var chaplainProfileFieldPhone:String = ""
    private var chaplainProfileFieldBirthDateDay:String = ""
    private var chaplainProfileFieldBirthDateMonth:String = ""
    private var chaplainProfileFieldBirthDateYear:String = ""
    private var chaplainProfileFieldEducation:String = ""
    private var chaplainProfileFieldExperience:String = ""
    private var chaplainProfileFieldChaplainField:String = ""
    private var chaplainProfileFieldChaplainFaith:String = ""
    private var chaplainProfileFieldChaplainEthnic:String = ""
    private var chaplainProfileFieldPreferredLanguage:String = ""
    private var chaplainProfileFieldOtherLanguage:String = ""
    private var chaplainProfileFieldSsn:String = ""
    private lateinit var pdfUri:Uri
    private var chaplainCvName: String = "" //this is for give a name to the chaplain cv when uploading cv to the firestore storage
    private var chaplainCvUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_sign_up)
        supportActionBar?.title = Html.fromHtml("<font color='#FBF3FE'>Tele Chaplaincy</font>")

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        chaplainCollectionName = getString(R.string.chaplain_collection)
        chaplainProfileCollectionName = getString(R.string.chaplain_profile_collection)
        chaplainProfileDocumentName = getString(R.string.chaplain_profile_document)

        //spinner addressing title selection
        addressingTitleSelection()
        //credentials title spinner item selection function
        credentialsTitleSelection()
        //chaplaincy Field spinner item selection function
        chaplaincyFieldSelection()
        //chaplaincy faith spinner item selection function
        chaplaincyFaithSelection()
        //chaplaincy Ethnic Background spinner item selection function
        chaplainEthnicBackgroundSelection()

        //chaplain other language chip adding method
        chaplain_other_language_add_imageView.setOnClickListener {
            addOtherLanguage()
            chaplainSignUpeditTextOtherLang.text.clear()
        }

        //go back login page textView click listener
        chaplain_sign_up_page_go_back_login.setOnClickListener {
            finish()
        }

        chaplain_sign_up_page_resume_button.setOnClickListener {
            takePermissionForPdf()

        }
        chaplain_sign_up_page_certificate_button.setOnClickListener {

        }

        //chaplain profile image selection
        chaplain_sign_up_profile_image.setOnClickListener {
            pickImage()
            if (chaplain_sign_up_profile_image.drawable != null){
                isImageEmpty = true
            }
        }
        chaplain_sign_up_page_back_button.setOnClickListener {
            chaplain_sign_up_page_back_button.isClickable = false
            chaplain_profile_first_step_layout.visibility = View.VISIBLE
            chaplain_profile_second_step_layout.visibility = View.GONE
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("user delete: ", "User account deleted.")
                        chaplain_sign_up_page_back_button.isClickable = true
                    }
                }
            chaplain_sign_up_page_resume_button.isClickable = true    //if back button is pressed, resume button will be active again
            chaplain_cv_name_show_lineer_layout.visibility = View.GONE
        }

        //chaplain profile second page next button and profile info taking
        chaplain_sign_up_page_next_button.setOnClickListener {
            takeProfileInfo()

        }

        //sign up button click listener
        chaplain_sign_up_page_signUp_button.setOnClickListener {
            takeProfileInfoFirstPart()
            chaplain_cv_progressBar.visibility = View.GONE
        }
    }

    private fun signUpChaplainToFirebase(){
        chaplain_sign_up_page_signUp_button.isClickable = false
        chaplain_signUp_page_progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    // Log.d("userIsCreated", "createUserWithEmail:success")
                    // val user = auth.currentUser
                    //   lse {
                    // If sign in fails, display a message to the user.
                    //Log.w("userIsNotCreated", "createUserWithEmail:failure", task.exception)
                    chaplain_signUp_page_progressBar.visibility = View.GONE
                    chaplain_sign_up_page_signUp_button.isClickable = true

                    //this is for the going to the second part of the sign up
                    chaplain_profile_first_step_layout.visibility = View.GONE
                    chaplain_profile_second_step_layout.visibility = View.VISIBLE
                    scrollView.smoothScrollTo(0, 0)

                    if (auth.currentUser != null){
                        user = auth.currentUser!!
                        chaplainProfileFieldUserId = user.uid
                    }
                }
                else{
                    Toast.makeText(
                        baseContext, task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    chaplain_signUp_page_progressBar.visibility = View.GONE
                    chaplain_sign_up_page_signUp_button.isClickable = true
                }
            }
    }

    //chaplain sign up first part taking user info and checking the empty parts
    private fun takeProfileInfoFirstPart(){
        userName = chaplain_sign_up_page_name_editText.text.toString()
        userSurName = chaplain_sign_up_page_surname_editText.text.toString()
        userEmail = chaplain_sign_up_page_email_editText.text.toString()
        userPassword = chaplain_sign_up_page_password_editText.text.toString()
        userPasswordAgain = chaplain_sign_up_page_password_again_editText.text.toString()
        termsChecked = chaplain_sign_up_agree_checkBox.isChecked

        if (!isImageEmpty){
            val toast = Toast.makeText(
                this,
                R.string.chaplain_sign_up_profile_image_toast_message,
                Toast.LENGTH_SHORT
            ).show()
        }

        else if (userName == "") {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_name,
                Toast.LENGTH_SHORT
            ).show()
        } else if (userSurName == "") {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_sur_name, Toast.LENGTH_SHORT
            ).show()
        } else if (userEmail == "") {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_email,
                Toast.LENGTH_SHORT
            ).show()
        } else if (userPassword == "") {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_password, Toast.LENGTH_SHORT
            ).show()
        } else if (userPasswordAgain == "") {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_password_again, Toast.LENGTH_SHORT
            ).show()
        } else if (userPassword != userPasswordAgain) {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_password_match, Toast.LENGTH_SHORT
            ).show()
        } else if (userPassword.length < 6) {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_password_long,
                Toast.LENGTH_SHORT
            ).show()
        } else if (!termsChecked) {
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_checkBox_isChecked, Toast.LENGTH_SHORT
            ).show()
        } else {
            signUpChaplainToFirebase()

        }

}

    // this func will take info from the second part of sign up ui
    private fun takeProfileInfo(){
        chaplainProfileFieldPhone = chaplainSignUpeditTextPhone.text.toString()
        chaplainProfileFieldBirthDateDay = chaplainSignUpeditTextDateDay.text.toString()
        chaplainProfileFieldBirthDateMonth = chaplainSignUpeditTextDateMonth.text.toString()
        chaplainProfileFieldBirthDateYear = chaplainSignUpeditTextDateYear.text.toString()
        chaplainProfileFieldEducation = chaplainSignUpeditTextEducation.text.toString()
        chaplainProfileFieldExperience = chaplainSignUpeditTextExperience.text.toString()
        chaplainProfileFieldPreferredLanguage = chaplainSignUpeditTextPreferredLang.text.toString()
        chaplainProfileFieldOtherLanguage = chaplainSignUpeditTextOtherLang.text.toString()
        chaplainProfileFieldSsn = chaplainSignUpeditTextSsn.text.toString()

        if (chaplainProfileAddresTitle == "Nothing Selected"){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_chaplain_addressing_title,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileCredentialTitle == "Nothing Selected"){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_chaplain_credentials_title,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldPhone == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_phone,
                Toast.LENGTH_SHORT
            ).show()
        }

        else if (chaplainProfileFieldBirthDateDay == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_birth_day,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldBirthDateMonth == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_birth_month,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldBirthDateYear == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_birth_year,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldEducation == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_edu,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldExperience == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_exp,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldChaplainField == "Nothing Selected"){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_chaplain_field,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldPreferredLanguage == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_preferred_lang,
                Toast.LENGTH_SHORT
            ).show()
        }
        else if (chaplainProfileFieldSsn == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_ssn,
                Toast.LENGTH_SHORT
            ).show()
        }


    }
    //addressing title spinner item selection function
    private fun addressingTitleSelection(){
        val optionAddresing = resources.getStringArray(R.array.addressingTitleArray)
        spinnerAdressingTitle.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionAddresing
        )
        spinnerAdressingTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainProfileAddresTitle = optionAddresing[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    //credentials title spinner item selection function
    private fun credentialsTitleSelection(){
        val optionCredential = resources.getStringArray(R.array.credentialsTitleArray)
        spinnerCredentialsTitle.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionCredential
        )
        spinnerCredentialsTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainProfileCredentialTitle = optionCredential[position]
                Log.d("chapProCreTitle: ", chaplainProfileCredentialTitle)
                val chipCredentialTitle = Chip(this@ChaplainSignUp)
                chipCredentialTitle.isCloseIconVisible = true
                chipCredentialTitle.setChipBackgroundColorResource(R.color.colorAccent)
                chipCredentialTitle.setTextColor(resources.getColor(R.color.colorPrimary))
                if (chaplainProfileCredentialTitle != "Nothing Selected" && chaplainProfileCredentialTitle != "Other"){
                    chipCredentialTitle.text = chaplainProfileCredentialTitle
                    credentials_chip_group.addView(chipCredentialTitle)
                }
                else if (chaplainProfileCredentialTitle == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@ChaplainSignUp)
                    builder.setTitle(R.string.chaplain_sign_up_credential_title_textView_text)

                    // Set up the input
                    val input = EditText(this@ChaplainSignUp)
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.hint = getString(R.string.chaplain_sign_up_credential_title_textView_text)
                    input.inputType = InputType.TYPE_CLASS_TEXT
                    builder.setView(input)

                    // Set up the buttons
                    builder.setPositiveButton(
                        getString(R.string.chaplain_sign_up_alert_ok_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            // Here you get get input text from the Edittext
                            val selection = input.text.toString()

                            //assign input to chip and add chip
                            chipCredentialTitle.text = selection
                            credentials_chip_group.addView(chipCredentialTitle)
                            Log.d("otherSelection: ", selection)
                        })
                    builder.setNegativeButton(
                        getString(R.string.chaplain_sign_up_alert_cancel_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })

                    builder.show()
                }

                chipCredentialTitle.setOnClickListener {
                    credentials_chip_group.removeView(chipCredentialTitle)
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    //chaplaincy Field spinner item selection function
    private fun chaplaincyFieldSelection(){
        val optionField = resources.getStringArray(R.array.chaplaincyFieldArray)
        spinner_chaplain_field.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionField
        )
        spinner_chaplain_field.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainProfileFieldChaplainField = optionField[position]
                if (chaplainProfileFieldChaplainField != "Nothing Selected"){
                    val chipChaplainFieldTitle = Chip(this@ChaplainSignUp)
                    chipChaplainFieldTitle.isCloseIconVisible = true
                    chipChaplainFieldTitle.setChipBackgroundColorResource(R.color.colorAccent)
                    chipChaplainFieldTitle.setTextColor(resources.getColor(R.color.colorPrimary))
                    chipChaplainFieldTitle.text = chaplainProfileFieldChaplainField
                    chaplain_field_chip_group.addView(chipChaplainFieldTitle)

                    chipChaplainFieldTitle.setOnClickListener {
                        chaplain_field_chip_group.removeView(chipChaplainFieldTitle)
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    //chaplaincy faith spinner item selection function
    private fun chaplaincyFaithSelection(){
        val optionFaith = resources.getStringArray(R.array.faithAffiliationArray)
        spinner_chaplain_faith.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionFaith
        )
        spinner_chaplain_faith.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainProfileFieldChaplainFaith = optionFaith[position]
                val chipChaplainFaith= Chip(this@ChaplainSignUp)
                chipChaplainFaith.isCloseIconVisible = true
                chipChaplainFaith.setChipBackgroundColorResource(R.color.colorAccent)
                chipChaplainFaith.setTextColor(resources.getColor(R.color.colorPrimary))
                if (chaplainProfileFieldChaplainFaith != "Nothing Selected" && chaplainProfileFieldChaplainFaith != "Other"){
                    chipChaplainFaith.text = chaplainProfileFieldChaplainFaith
                    chaplain_faith_chip_group.addView(chipChaplainFaith)
                }
                else if (chaplainProfileFieldChaplainFaith == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@ChaplainSignUp)
                    builder.setTitle(R.string.chaplain_sign_up_chaplaincy_faith)

                    // Set up the input
                    val input = EditText(this@ChaplainSignUp)
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
                            chipChaplainFaith.text = selection
                            chaplain_faith_chip_group.addView(chipChaplainFaith)
                            Log.d("otherSelection: ", selection)
                        })
                    builder.setNegativeButton(
                        getString(R.string.chaplain_sign_up_alert_cancel_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })

                    builder.show()
                }
                chipChaplainFaith.setOnClickListener {
                    chaplain_faith_chip_group.removeView(chipChaplainFaith)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    //chaplaincy Ethnic Background spinner item selection function
    private fun chaplainEthnicBackgroundSelection(){
        val optionEthnic = resources.getStringArray(R.array.ethnicBackgroundArray)
        spinner_chaplain_ethnic.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionEthnic
        )
        spinner_chaplain_ethnic.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainProfileFieldChaplainEthnic = optionEthnic[position]
                val chipChaplainEthnic= Chip(this@ChaplainSignUp)
                chipChaplainEthnic.isCloseIconVisible = true
                chipChaplainEthnic.setChipBackgroundColorResource(R.color.colorAccent)
                chipChaplainEthnic.setTextColor(resources.getColor(R.color.colorPrimary))
                if (chaplainProfileFieldChaplainEthnic != "Nothing Selected" && chaplainProfileFieldChaplainEthnic != "Other"){
                    chipChaplainEthnic.text = chaplainProfileFieldChaplainEthnic
                    chaplain_ethnic_chip_group.addView(chipChaplainEthnic)
                }
                else if (chaplainProfileFieldChaplainEthnic == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@ChaplainSignUp)
                    builder.setTitle(R.string.chaplain_sign_up_ethnic_title_textView_text)

                    // Set up the input
                    val input = EditText(this@ChaplainSignUp)
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
                            chipChaplainEthnic.text = selection
                            chaplain_ethnic_chip_group.addView(chipChaplainEthnic)
                            Log.d("otherSelection: ", selection)
                        })
                    builder.setNegativeButton(
                        getString(R.string.chaplain_sign_up_alert_cancel_button),
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })

                    builder.show()
                }
                chipChaplainEthnic.setOnClickListener {
                    chaplain_ethnic_chip_group.removeView(chipChaplainEthnic)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }
    //adding chaplain other languages to the screen using chips
    private fun addOtherLanguage(){
        chaplainProfileFieldOtherLanguage = chaplainSignUpeditTextOtherLang.text.toString()
        val chipChaplainOtherLanguage= Chip(this@ChaplainSignUp)
        chipChaplainOtherLanguage.text = chaplainProfileFieldOtherLanguage
        chipChaplainOtherLanguage.isCloseIconVisible = true
        chipChaplainOtherLanguage.setChipBackgroundColorResource(R.color.colorAccent)
        chipChaplainOtherLanguage.setTextColor(resources.getColor(R.color.colorPrimary))

        chaplain_other_languages_chip_group.addView(chipChaplainOtherLanguage)
        chipChaplainOtherLanguage.setOnClickListener {
            chaplain_other_languages_chip_group.removeView(chipChaplainOtherLanguage) }
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
                val imageFile = uriToImageFile(uri)

            }
            if (uri != null) {
                val imageBitmap = uriToBitmap(uri)
                // todo do something with bitmap

                chaplain_sign_up_profile_image.setImageBitmap(imageBitmap)
                isImageEmpty = true
            }
        }

        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            pdfUri = data.data!! //this is the pdf file path on the device
            //Log.d("pdfUri: ", pdfUri.toString())
            chaplainCvName = data.data!!.lastPathSegment.toString()

            if (pdfUri.toString().startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = applicationContext.contentResolver.query(pdfUri, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        chaplainCvName =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }

            if (pdfUri != null){

                uploadFile(pdfUri)

            }
            else{
                val toast = Toast.makeText(
                    this,
                    R.string.sign_up_toast_message_pdf_file_is_not_selected,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun uploadFile(pdfUri: Uri){
        chaplain_cv_progressBar.progress = 0

        chaplain_sign_up_page_resume_button.isClickable = false

        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId).child("resume").child(
            "Chaplain Cv"
        )
        ref.putFile(pdfUri)
            .addOnFailureListener {
            // Handle unsuccessful uploads
           // Log.d("file: ", "error")
            chaplain_cv_progressBar.visibility = View.GONE
            chaplain_cv_name_show_lineer_layout.visibility = View.VISIBLE
            resumeFileNameTextView.text = chaplainCvName
            imageViewResumeOk.setImageResource(R.drawable.ic_baseline_cancel_24)
            chaplain_sign_up_page_resume_button.isClickable = true
            }
            .addOnSuccessListener {

                chaplain_cv_progressBar.visibility = View.GONE
               // Log.d("file: ", "file uploaded")
                chaplain_cv_name_show_lineer_layout.visibility = View.VISIBLE
                resumeFileNameTextView.text = chaplainCvName

        }
            .addOnProgressListener { task ->
                chaplain_cv_progressBar.visibility = View.VISIBLE
                val currentProgress:Int = ((100*task.bytesTransferred)/task.totalByteCount).toInt()
                chaplain_cv_progressBar.progress = currentProgress
                //Log.d("progress: ", currentProgress.toString())
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && PICK_PDF_REQUEST_CODE == 1002) {
                    // pick image after request permission success
                    selectPdf()
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
        const val PICK_PDF_REQUEST_CODE = 1002
    }

    private fun takePermissionForPdf() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {

            selectPdf()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }
    }

    private fun selectPdf(){
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_PDF_REQUEST_CODE)
    }


}