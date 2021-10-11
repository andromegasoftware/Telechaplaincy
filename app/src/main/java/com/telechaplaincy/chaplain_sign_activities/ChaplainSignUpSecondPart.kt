package com.telechaplaincy.chaplain_sign_activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import java.io.File

class ChaplainSignUpSecondPart : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference

    private val db = Firebase.firestore
    private var chaplainCollectionName:String = ""
    private var chaplainProfileCollectionName:String = ""
    private var chaplainProfileDocumentName:String = ""

    private var chaplainProfileFieldUserId:String = ""
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
    private lateinit var pdfUri: Uri
    private var chaplainCvName: String = "" //this is for give a name to the chaplain cv when uploading cv to the firestore storage
    private var chaplainCvUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_sign_up_second_part)
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


        //chaplain profile second page next button and profile info taking
        chaplain_sign_up_page_next_button.setOnClickListener {
            takeProfileInfo()

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
                val chipCredentialTitle = Chip(this@ChaplainSignUpSecondPart)
                chipCredentialTitle.isCloseIconVisible = true
                chipCredentialTitle.setChipBackgroundColorResource(R.color.colorAccent)
                chipCredentialTitle.setTextColor(resources.getColor(R.color.colorPrimary))
                if (chaplainProfileCredentialTitle != "Nothing Selected" && chaplainProfileCredentialTitle != "Other"){
                    chipCredentialTitle.text = chaplainProfileCredentialTitle
                    credentials_chip_group.addView(chipCredentialTitle)
                }
                else if (chaplainProfileCredentialTitle == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@ChaplainSignUpSecondPart)
                    builder.setTitle(R.string.chaplain_sign_up_credential_title_textView_text)

                    // Set up the input
                    val input = EditText(this@ChaplainSignUpSecondPart)
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
                    val chipChaplainFieldTitle = Chip(this@ChaplainSignUpSecondPart)
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
                val chipChaplainFaith= Chip(this@ChaplainSignUpSecondPart)
                chipChaplainFaith.isCloseIconVisible = true
                chipChaplainFaith.setChipBackgroundColorResource(R.color.colorAccent)
                chipChaplainFaith.setTextColor(resources.getColor(R.color.colorPrimary))
                if (chaplainProfileFieldChaplainFaith != "Nothing Selected" && chaplainProfileFieldChaplainFaith != "Other"){
                    chipChaplainFaith.text = chaplainProfileFieldChaplainFaith
                    chaplain_faith_chip_group.addView(chipChaplainFaith)
                }
                else if (chaplainProfileFieldChaplainFaith == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@ChaplainSignUpSecondPart)
                    builder.setTitle(R.string.chaplain_sign_up_chaplaincy_faith)

                    // Set up the input
                    val input = EditText(this@ChaplainSignUpSecondPart)
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
                val chipChaplainEthnic= Chip(this@ChaplainSignUpSecondPart)
                chipChaplainEthnic.isCloseIconVisible = true
                chipChaplainEthnic.setChipBackgroundColorResource(R.color.colorAccent)
                chipChaplainEthnic.setTextColor(resources.getColor(R.color.colorPrimary))
                if (chaplainProfileFieldChaplainEthnic != "Nothing Selected" && chaplainProfileFieldChaplainEthnic != "Other"){
                    chipChaplainEthnic.text = chaplainProfileFieldChaplainEthnic
                    chaplain_ethnic_chip_group.addView(chipChaplainEthnic)
                }
                else if (chaplainProfileFieldChaplainEthnic == "Other"){

                    //alert dialog create
                    val builder= AlertDialog.Builder(this@ChaplainSignUpSecondPart)
                    builder.setTitle(R.string.chaplain_sign_up_ethnic_title_textView_text)

                    // Set up the input
                    val input = EditText(this@ChaplainSignUpSecondPart)
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
        val chipChaplainOtherLanguage= Chip(this@ChaplainSignUpSecondPart)
        chipChaplainOtherLanguage.text = chaplainProfileFieldOtherLanguage
        chipChaplainOtherLanguage.isCloseIconVisible = true
        chipChaplainOtherLanguage.setChipBackgroundColorResource(R.color.colorAccent)
        chipChaplainOtherLanguage.setTextColor(resources.getColor(R.color.colorPrimary))

        chaplain_other_languages_chip_group.addView(chipChaplainOtherLanguage)
        chipChaplainOtherLanguage.setOnClickListener {
            chaplain_other_languages_chip_group.removeView(chipChaplainOtherLanguage) }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && PICK_PDF_REQUEST_CODE == 1002) {
                    // pick image after request permission success
                    selectPdf()
                }
            }
        }
    }

    companion object {
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