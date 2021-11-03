package com.telechaplaincy.chaplain_sign_activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Html
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import kotlinx.android.synthetic.main.activity_chaplain_sign_up.*
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChaplainSignUpSecondPart : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference

    private val db = Firebase.firestore
    private lateinit var dbSave:DocumentReference
    private lateinit var dbChaplainFieldSave:DocumentReference
    private var chaplainCollectionName:String = ""
    private var chaplainProfileCollectionName:String = ""
    private var chaplainProfileDocumentName:String = ""

    private var chaplainProfileFieldUserId:String = ""
    private var chaplainProfileAddresTitle:String = ""
    private var chaplainProfileCredentialTitle:String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileFieldPhone:String = ""
    private var chaplainProfileFieldBirthDate:String = "0"
    private var chaplainProfileFieldEducation:String = ""
    private var chaplainProfileFieldExperience:String = ""
    private var chaplainProfileFieldChaplainField:String = ""
    private var chaplainFieldArrayList = ArrayList<String>()
    private var chaplainProfileFieldChaplainFaith:String = ""
    private var faithArrayList = ArrayList<String>()
    private var chaplainProfileFieldChaplainEthnic:String = ""
    private var ethnicArrayList = ArrayList<String>()
    private var chaplainProfileFieldPreferredLanguage:String = ""
    private var chaplainProfileFieldOtherLanguage:String = ""
    private var otherLanguagesArrayList = ArrayList<String>()
    private var chaplainProfileFieldSsn:String = ""
    private var chaplainProfileOrdained: String = ""
    private var chaplainProfileOrdainedPeriod: String = ""
    private var chaplainProfileAddCridentials: String = ""
    private var chaplainProfileExplanation:String = ""
    private lateinit var pdfUri: Uri
    var chaplainCvUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage
    var chaplainCertificateUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage
    var cvOrCertficate:Int = 1
    private var chaplainCertificateName = ""
    private var chaplainResumeName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_sign_up_second_part)
        //supportActionBar?.title = Html.fromHtml("<font color='#FBF3FE'>Tele Chaplaincy</font>")

        //this is for the progress bar visibility in the chaplain cv upload
        chaplain_cv_progressBar.visibility = View.GONE
        chaplain_certificate_progressBar.visibility = View.GONE
        chaplain_cv_progressBarUploadData.visibility = View.GONE

        //call edittext scrollable method here
        editTextBioScroll()

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
        val chaplainField = "chaplain field"

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
            .collection(chaplainProfileCollectionName).document(chaplainProfileDocumentName)
        dbChaplainFieldSave = db.collection(chaplainCollectionName)
            .document(chaplainProfileFieldUserId).collection(chaplainField).document("field")

        chaplain_birth_date_select_button.setOnClickListener {
            takeChaplainBirthDate()
        }

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

        chaplain_sign_up_page_resume_button.setOnClickListener {
            cvOrCertficate = 1
            takePermissionForPdf()
        }
        chaplain_sign_up_page_certificate_button.setOnClickListener {
            cvOrCertficate = 2
            takePermissionForPdf()
        }


        //chaplain profile second page next button and profile info taking
        chaplain_sign_up_page_next_button.setOnClickListener {
            takeProfileInfo()
            saveData()
        }

        //this is for the deleting chaplain cv file from storage when the user click the delete image
        imageViewResumeDelete.setOnClickListener {
            deleteCv()
        }
        imageViewCertificateDelete.setOnClickListener {
            deleteCertificate()
        }

    }

    //this method is for the edittext chaplain short bio. it makes edittext scrollable.
    @SuppressLint("ClickableViewAccessibility")
    private fun editTextBioScroll() {
        chaplainSignUpeditTextExp.isVerticalScrollBarEnabled = true
        chaplainSignUpeditTextExp.overScrollMode = View.OVER_SCROLL_ALWAYS
        chaplainSignUpeditTextExp.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        chaplainSignUpeditTextExp.movementMethod = ScrollingMovementMethod.getInstance()
        chaplainSignUpeditTextExp.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event?.action) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    // this func will take info from the second part of sign up ui
    private fun takeProfileInfo(){
        chaplainProfileFieldPhone = chaplainSignUpeditTextPhone.text.toString()
        chaplainProfileFieldEducation = chaplainSignUpeditTextEducation.text.toString()
        chaplainProfileFieldExperience = chaplainSignUpeditTextExperience.text.toString()
        chaplainProfileFieldPreferredLanguage = chaplainSignUpeditTextPreferredLang.text.toString()
        chaplainProfileFieldOtherLanguage = chaplainSignUpeditTextOtherLang.text.toString()
        chaplainProfileFieldSsn = chaplainSignUpeditTextSsn.text.toString()
        chaplainProfileOrdained = chaplainSignUpeditTextOrdeined.text.toString()
        chaplainProfileOrdainedPeriod = chaplainSignUpeditTextOrdPeriod.text.toString()
        chaplainProfileAddCridentials = chaplainSignUpeditAddCrident.text.toString()
        chaplainProfileExplanation = chaplainSignUpeditTextExp.text.toString()
    }

    private fun takeChaplainBirthDate(){
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener {
            chaplainProfileFieldBirthDate = datePicker.selection.toString()
            textView_chaplain_birth_date.text = getString(R.string.chaplain_sign_up_birth_date) + datePicker.headerText
            Log.d("date positive", chaplainProfileFieldBirthDate)
        }
        datePicker.addOnNegativeButtonClickListener {
            //cancel button works
            Log.d("date negative", datePicker.headerText)
        }
        datePicker.addOnCancelListener {
            //if the user click the outside of the date picker
            Log.d("date cancel", "date picker canceled")
        }

    }

    //to save chaplain profile info when the user click the save button
    fun saveData(){
        chaplain_cv_progressBarUploadData.visibility = View.VISIBLE
        chaplain_sign_up_page_next_button.isClickable = false
        val data = hashMapOf(
            "addressing title" to chaplainProfileAddresTitle,
            "phone" to chaplainProfileFieldPhone,
            "birth date" to Timestamp(Date(chaplainProfileFieldBirthDate.toLong())),
            "education" to chaplainProfileFieldEducation,
            "experiance" to chaplainProfileFieldExperience,
            "language" to chaplainProfileFieldPreferredLanguage,
            "ssn" to chaplainProfileFieldSsn,
            "ordained name" to chaplainProfileOrdained,
            "ordained period" to chaplainProfileOrdainedPeriod,
            "additional cridential" to chaplainProfileAddCridentials,
            "bio" to chaplainProfileExplanation,
            "cv url" to chaplainCvUrl,
            "certificate url" to chaplainCertificateUrl,
            "certificate name" to chaplainCertificateName,
            "cv name" to chaplainResumeName,
            "cridentials title" to credentialTitleArrayList.joinToString("/"),
            "faith" to faithArrayList.joinToString("/"),
            "ethnic" to ethnicArrayList.joinToString("/"),
            "other languages" to otherLanguagesArrayList.joinToString("/")
        )
        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
                chaplain_cv_progressBarUploadData.visibility = View.GONE
                chaplain_sign_up_page_next_button.isClickable = true
                chaplain_sign_up_page_next_button.text = getString(R.string.chaplain_sign_up_next_button_after_save)
                val toast = Toast.makeText(
                    this,
                    R.string.sign_up_toast_message_data_uploaded,
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener {
                    e -> Log.w("data upload", "Error writing document", e)
                chaplain_cv_progressBarUploadData.visibility = View.GONE
                chaplain_sign_up_page_next_button.isClickable = true
                val toast = Toast.makeText(
                    this,
                    R.string.sign_up_toast_message_data_not_uploaded,
                    Toast.LENGTH_SHORT
                ).show()
            }

        val field = HashMap<String, String>()
        var a = 1
        for (k in chaplainFieldArrayList){
            field[a.toString()] = k
            Log.d("liste", k)
            a += 1
        }
        dbChaplainFieldSave.set(field).addOnCompleteListener {
            Log.d("field", "uploaded")
        }.addOnFailureListener {
            Log.d("field", "failed")
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
                    credentialTitleArrayList.add(chaplainProfileCredentialTitle)
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
                            chaplainProfileCredentialTitle = selection
                            credentials_chip_group.addView(chipCredentialTitle)
                            Log.d("otherSelection: ", selection)
                            credentialTitleArrayList.add(chaplainProfileCredentialTitle)
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
                    credentialTitleArrayList.remove(chipCredentialTitle.text)
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
                    chaplainFieldArrayList.add(chaplainProfileFieldChaplainField)
                    chipChaplainFieldTitle.setOnClickListener {
                        chaplain_field_chip_group.removeView(chipChaplainFieldTitle)
                        chaplainFieldArrayList.remove(chipChaplainFieldTitle.text)
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
                    faithArrayList.add(chaplainProfileFieldChaplainFaith)
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
                            chaplainProfileFieldChaplainFaith = selection
                            chaplain_faith_chip_group.addView(chipChaplainFaith)
                            Log.d("otherSelection: ", selection)
                            faithArrayList.add(chaplainProfileFieldChaplainFaith)
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
                    faithArrayList.remove(chipChaplainFaith.text)
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
                    ethnicArrayList.add(chaplainProfileFieldChaplainEthnic)
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
                            chaplainProfileFieldChaplainEthnic = selection
                            chaplain_ethnic_chip_group.addView(chipChaplainEthnic)
                            Log.d("otherSelection: ", selection)
                            ethnicArrayList.add(chaplainProfileFieldChaplainEthnic)
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
                    ethnicArrayList.remove(chipChaplainEthnic.text)
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
        otherLanguagesArrayList.add(chaplainProfileFieldOtherLanguage)
        chipChaplainOtherLanguage.setOnClickListener {
            chaplain_other_languages_chip_group.removeView(chipChaplainOtherLanguage)
            otherLanguagesArrayList.remove(chipChaplainOtherLanguage.text)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            pdfUri = data.data!! //this is the pdf file path on the device
            //Log.d("pdfUri: ", pdfUri.toString())

            if (pdfUri != null){
                if (cvOrCertficate == 1){
                    uploadFileResume(pdfUri)
                    Log.d("cvOrCertficate", cvOrCertficate.toString())
                }

                else{
                    uploadFileCertificate(pdfUri)
                    Log.d("cvOrCertficate", cvOrCertficate.toString())
                }


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

    private fun uploadFileResume(pdfUri: Uri){

        chaplain_cv_progressBar.progress = 0

        chaplain_sign_up_page_resume_button.isClickable = false

        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId)
            .child("resume").child("Chaplain Cv")
       val uploadTask =  ref.putFile(pdfUri)
            .addOnFailureListener {
                // Handle unsuccessful uploads
                // Log.d("file: ", "error")
                chaplain_cv_progressBar.visibility = View.GONE
                chaplain_cv_name_show_lineer_layout.visibility = View.VISIBLE
                resumeFileNameTextView.text = chaplainResumeName
                imageViewResumeOk.setImageResource(R.drawable.ic_baseline_cancel_24)
                chaplain_sign_up_page_resume_button.isClickable = true
            }
            .addOnSuccessListener {

                //for taking the file name
                if (pdfUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = applicationContext.contentResolver.query(pdfUri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            chaplainResumeName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            Log.d("name", chaplainResumeName)
                        }
                    } finally {
                        cursor?.close()
                    }
                }
                //until here

                chaplain_cv_progressBar.visibility = View.GONE
                // Log.d("file: ", "file uploaded")
                chaplain_cv_name_show_lineer_layout.visibility = View.VISIBLE
                resumeFileNameTextView.text = chaplainResumeName
                Log.d("namea", chaplainResumeName)
                chaplain_sign_up_page_resume_button.isClickable = true

            }
            .addOnProgressListener { task ->
                chaplain_cv_progressBar.visibility = View.VISIBLE
                val currentProgress:Int = ((100*task.bytesTransferred)/task.totalByteCount).toInt()
                chaplain_cv_progressBar.progress = currentProgress
                //Log.d("progress: ", currentProgress.toString())
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
                chaplainCvUrl = task.result.toString()

                Log.d("pdfLink: ", chaplainCvUrl)
            } else {
                // Handle failures
                // ...
            }
        }


    }

    private fun deleteCv(){
        chaplain_cv_name_show_lineer_layout.visibility = View.GONE
        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId)
            .child("resume").child("Chaplain Cv")

        ref.delete().addOnSuccessListener {
            // File deleted successfully
            Log.d("pdfLinkDelete: ", "deleted")
            chaplainCvUrl = ""
            chaplainResumeName = ""
        }.addOnFailureListener {
            // Uh-oh, an error occurred!
            Log.d("pdfLinkDelete: ", "not deleted")
        }

    }
    private fun deleteCertificate(){
        chaplain_certificate_name_lineer_layout.visibility = View.GONE
        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId)
            .child("certificate").child("Chaplain Certificate")

        ref.delete().addOnSuccessListener {
            // File deleted successfully
            Log.d("pdfLinkDelete: ", "deleted")
            chaplainCertificateUrl = ""
            chaplainCertificateName = ""
        }.addOnFailureListener {
            // Uh-oh, an error occurred!
            Log.d("pdfLinkDelete: ", "not deleted")
        }
    }

    private fun uploadFileCertificate(pdfUri: Uri){

        chaplain_certificate_progressBar.progress = 0

        chaplain_sign_up_page_certificate_button.isClickable = false

        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId)
            .child("certificate").child("Chaplain Certificate")
        val uploadTask =  ref.putFile(pdfUri)
            .addOnFailureListener {
                // Handle unsuccessful uploads
                // Log.d("file: ", "error")
                chaplain_certificate_progressBar.visibility = View.GONE
                chaplain_certificate_name_lineer_layout.visibility = View.VISIBLE
                certificateFileNameTextView.text = chaplainCertificateName
                Log.d("namea", chaplainCertificateName)
                imageViewCertificateOk.setImageResource(R.drawable.ic_baseline_cancel_24)
                chaplain_sign_up_page_certificate_button.isClickable = true
            }
            .addOnSuccessListener {

                //for taking the file name
                if (pdfUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = applicationContext.contentResolver.query(pdfUri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            chaplainCertificateName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            Log.d("name", chaplainCertificateName)
                        }
                    } finally {
                        cursor?.close()
                    }
                }
                //until here

                chaplain_certificate_progressBar.visibility = View.GONE
                // Log.d("file: ", "file uploaded")
                chaplain_certificate_name_lineer_layout.visibility = View.VISIBLE
                certificateFileNameTextView.text = chaplainCertificateName
                chaplain_sign_up_page_certificate_button.isClickable = true

            }
            .addOnProgressListener { task ->
                chaplain_certificate_progressBar.visibility = View.VISIBLE
                val currentProgress:Int = ((100*task.bytesTransferred)/task.totalByteCount).toInt()
                chaplain_certificate_progressBar.progress = currentProgress
                //Log.d("progress: ", currentProgress.toString())
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
                chaplainCertificateUrl = task.result.toString()

                Log.d("pdfLink: ", chaplainCertificateUrl)
            } else {
                // Handle failures
                // ...
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && PICK_PDF_REQUEST_CODE == 1002) {
                    // pick pdf after request permission success
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

    override fun onPause() {
        super.onPause()
        takeProfileInfo()
        saveDataInBackground()
    }

    override fun onStart() {
        super.onStart()
        readData()
    }
    private fun readData(){
        dbSave.get().addOnSuccessListener { document ->
            if (document != null){
                if (document.data?.containsKey("addressing title") == true){
                    chaplainProfileAddresTitle = document.data?.get("addressing title").toString() //if it is empty, it will return "Nothing Selected"
                        val optionAddresing = resources.getStringArray(R.array.addressingTitleArray)
                        spinnerAdressingTitle.setSelection(optionAddresing.indexOf(chaplainProfileAddresTitle))
                }
                if (document.data?.containsKey("phone") == true){
                    chaplainProfileFieldPhone = document.data?.get("phone").toString()
                }

                var birthDate = Timestamp(Date(chaplainProfileFieldBirthDate.toLong()))
                if (document.data?.containsKey("birth date") == true){
                    birthDate = (document.data?.get("birth date") as? com.google.firebase.Timestamp)!!
                }
                val millisecondsLong = birthDate.seconds * 1000 + birthDate.nanoseconds / 1000000
                val sdf = SimpleDateFormat("MMM dd, yyyy")
                val netDate = Date(millisecondsLong)
                val date = sdf.format(netDate).toString()
                chaplainProfileFieldBirthDate = millisecondsLong.toString()

                if (document.data?.containsKey("education") == true){
                    chaplainProfileFieldEducation = document.data?.get("education").toString()
                    chaplainSignUpeditTextEducation.setText(chaplainProfileFieldEducation)
                }

                if (document.data?.containsKey("experiance") == true){
                    chaplainProfileFieldExperience = document.data?.get("experiance").toString()
                    chaplainSignUpeditTextExperience.setText(chaplainProfileFieldExperience)
                }

                if (document.data?.containsKey("language") == true){
                    chaplainProfileFieldPreferredLanguage = document.data?.get("language").toString()
                    chaplainSignUpeditTextPreferredLang.setText(chaplainProfileFieldPreferredLanguage)
                }

                if (document.data?.containsKey("ssn") == true){
                    chaplainProfileFieldSsn = document.data?.get("ssn").toString()
                    chaplainSignUpeditTextSsn.setText(chaplainProfileFieldSsn)
                }

                if (document.data?.containsKey("ordained name") == true){
                    chaplainProfileOrdained = document.data?.get("ordained name").toString()
                    chaplainSignUpeditTextOrdeined.setText(chaplainProfileOrdained)
                }

                if (document.data?.containsKey("ordained period") == true){
                    chaplainProfileOrdainedPeriod = document.data?.get("ordained period").toString()
                    chaplainSignUpeditTextOrdPeriod.setText(chaplainProfileOrdainedPeriod)
                }

                if (document.data?.containsKey("additional cridential") == true){
                    chaplainProfileAddCridentials = document.data?.get("additional cridential").toString()
                    chaplainSignUpeditAddCrident.setText(chaplainProfileAddCridentials)
                }

                if (document.data?.containsKey("bio") == true){
                    chaplainProfileExplanation = document.data?.get("bio").toString()
                    chaplainSignUpeditTextExp.setText(chaplainProfileExplanation)
                }

                if (document.data?.containsKey("certificate name") == true){
                    chaplainCertificateName = document.data?.get("certificate name").toString()
                    if (chaplainCertificateName != ""){
                        chaplain_certificate_name_lineer_layout.visibility = View.VISIBLE
                        certificateFileNameTextView.text = chaplainCertificateName
                    }
                }

                if (document.data?.containsKey("cv name") == true){
                    chaplainResumeName = document.data?.get("cv name").toString()
                    if(chaplainResumeName != ""){
                        chaplain_cv_name_show_lineer_layout.visibility = View.VISIBLE
                        resumeFileNameTextView.text = chaplainResumeName
                    }
                }

                if (document.data?.containsKey("cv url") == true){
                    chaplainCvUrl = document.data?.get("cv url").toString()
                }

                if (document.data?.containsKey("certificate url") == true){
                    chaplainCertificateUrl = document.data?.get("certificate url").toString()
                }

                chaplainSignUpeditTextPhone.setText(chaplainProfileFieldPhone)
                if (date != "Jan 01, 1970"){
                    textView_chaplain_birth_date.text = getString(R.string.chaplain_sign_up_birth_date) + date
                }

                if (document.data?.containsKey("cridentials title") == true ) {
                    val cridentials = document.data?.get("cridentials title").toString()
                    if (cridentials != "") {
                        if (cridentials.contains("/")) {
                            credentialTitleArrayList = cridentials.split("/") as ArrayList<String>
                        } else {
                            credentialTitleArrayList.add(cridentials)
                        }

                        for (k in credentialTitleArrayList.indices) {
                            if (credentialTitleArrayList[k] != "Nothing Selected") {
                                val chipCredentialTitle = Chip(this@ChaplainSignUpSecondPart)
                                chipCredentialTitle.isCloseIconVisible = true
                                chipCredentialTitle.setChipBackgroundColorResource(R.color.colorAccent)
                                chipCredentialTitle.setTextColor(resources.getColor(R.color.colorPrimary))
                                chipCredentialTitle.text = credentialTitleArrayList[k]
                                credentials_chip_group.addView(chipCredentialTitle)

                                chipCredentialTitle.setOnClickListener {
                                    credentials_chip_group.removeView(chipCredentialTitle)
                                    credentialTitleArrayList.remove(chipCredentialTitle.text)
                                }
                            }
                        }
                    }
                }

                    if (document.data?.containsKey("faith") == true ) {
                        val chaplainFaith = document.data?.get("faith").toString()
                        if (chaplainFaith != "") {
                            if (chaplainFaith.contains("/")) {
                                faithArrayList =
                                    chaplainFaith.split("/") as ArrayList<String>
                            } else {
                                faithArrayList.add(chaplainFaith)
                            }

                            for (k in faithArrayList.indices) {
                                if (faithArrayList[k] != "Nothing Selected") {
                                    val chipChaplainFaith = Chip(this@ChaplainSignUpSecondPart)
                                    chipChaplainFaith.isCloseIconVisible = true
                                    chipChaplainFaith.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipChaplainFaith.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipChaplainFaith.text = faithArrayList[k]
                                    chaplain_faith_chip_group.addView(chipChaplainFaith)

                                    chipChaplainFaith.setOnClickListener {
                                        chaplain_faith_chip_group.removeView(chipChaplainFaith)
                                        faithArrayList.remove(chipChaplainFaith.text)
                                    }
                                }
                            }
                        }
                    }

                if (document.data?.containsKey("ethnic") == true ) {
                    val ethnicBackground = document.data?.get("ethnic").toString()
                    if (ethnicBackground != "") {
                        if (ethnicBackground.contains("/")) {
                            ethnicArrayList = ethnicBackground.split("/") as ArrayList<String>
                        } else {
                            ethnicArrayList.add(ethnicBackground)
                        }

                        for (k in ethnicArrayList.indices) {
                            if (ethnicArrayList[k] != "Nothing Selected") {
                                val chipChaplainEthnic = Chip(this@ChaplainSignUpSecondPart)
                                chipChaplainEthnic.isCloseIconVisible = true
                                chipChaplainEthnic.setChipBackgroundColorResource(R.color.colorAccent)
                                chipChaplainEthnic.setTextColor(resources.getColor(R.color.colorPrimary))
                                chipChaplainEthnic.text = ethnicArrayList[k]
                                chaplain_ethnic_chip_group.addView(chipChaplainEthnic)

                                chipChaplainEthnic.setOnClickListener {
                                    chaplain_ethnic_chip_group.removeView(chipChaplainEthnic)
                                    ethnicArrayList.remove(chipChaplainEthnic.text)
                                }
                            }
                        }
                    }
                }

                if (document.data?.containsKey("other languages") == true ) {
                    val chaplainOtherLanguages = document.data?.get("other languages").toString()
                    if (chaplainOtherLanguages != "") {
                        if (chaplainOtherLanguages.contains("/")) {
                            otherLanguagesArrayList =
                                chaplainOtherLanguages.split("/") as ArrayList<String>
                        } else {
                            otherLanguagesArrayList.add(chaplainOtherLanguages)
                        }

                        for (k in otherLanguagesArrayList.indices) {
                                val chipChaplainOtherLanguage = Chip(this@ChaplainSignUpSecondPart)
                                chipChaplainOtherLanguage.isCloseIconVisible = true
                                chipChaplainOtherLanguage.setChipBackgroundColorResource(R.color.colorAccent)
                                chipChaplainOtherLanguage.setTextColor(resources.getColor(R.color.colorPrimary))
                                chipChaplainOtherLanguage.text = otherLanguagesArrayList[k]
                            chaplain_other_languages_chip_group.addView(chipChaplainOtherLanguage)

                                chipChaplainOtherLanguage.setOnClickListener {
                                    chaplain_other_languages_chip_group.removeView(chipChaplainOtherLanguage)
                                    otherLanguagesArrayList.remove(chipChaplainOtherLanguage.text)
                                }
                        }
                    }
                }
            }
            else {
                Log.d("TAG", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }

        dbChaplainFieldSave.get().addOnSuccessListener { document ->
            if (document != null) {
                if (document.data?.containsKey("1") == true ){
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    val result = document.data!!
                    for ((k, v) in result){
                        chaplainFieldArrayList.add(v.toString())
                    }
                    Log.d("TA", result.toString())
                }

                for (k in chaplainFieldArrayList.indices) {
                    val chipChaplainFieldTitle = Chip(this@ChaplainSignUpSecondPart)
                    chipChaplainFieldTitle.isCloseIconVisible = true
                    chipChaplainFieldTitle.setChipBackgroundColorResource(R.color.colorAccent)
                    chipChaplainFieldTitle.setTextColor(resources.getColor(R.color.colorPrimary))
                    chipChaplainFieldTitle.text = chaplainFieldArrayList[k]
                    chaplain_field_chip_group.addView(chipChaplainFieldTitle)

                    chipChaplainFieldTitle.setOnClickListener {
                        chaplain_field_chip_group.removeView(chipChaplainFieldTitle)
                        chaplainFieldArrayList.remove(chipChaplainFieldTitle.text)
                    }
                }
            }
            else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)

            }
    }

    fun saveDataInBackground(){
        val data = hashMapOf(
            "addressing title" to chaplainProfileAddresTitle,
            "phone" to chaplainProfileFieldPhone,
            "birth date" to Timestamp(Date(chaplainProfileFieldBirthDate.toLong())),
            "education" to chaplainProfileFieldEducation,
            "experiance" to chaplainProfileFieldExperience,
            "language" to chaplainProfileFieldPreferredLanguage,
            "ssn" to chaplainProfileFieldSsn,
            "ordained name" to chaplainProfileOrdained,
            "ordained period" to chaplainProfileOrdainedPeriod,
            "additional cridential" to chaplainProfileAddCridentials,
            "bio" to chaplainProfileExplanation,
            "cv url" to chaplainCvUrl,
            "certificate url" to chaplainCertificateUrl,
            "certificate name" to chaplainCertificateName,
            "cv name" to chaplainResumeName,
            "cridentials title" to credentialTitleArrayList.joinToString("/"),
            "faith" to faithArrayList.joinToString("/"),
            "ethnic" to ethnicArrayList.joinToString("/"),
            "other languages" to otherLanguagesArrayList.joinToString("/")
        )
        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
                credentials_chip_group.removeAllViews()
                credentialTitleArrayList.clear()

                chaplain_faith_chip_group.removeAllViews()
                faithArrayList.clear()

                chaplain_ethnic_chip_group.removeAllViews()
                ethnicArrayList.clear()

                chaplain_other_languages_chip_group.removeAllViews()
                otherLanguagesArrayList.clear()

                chaplain_field_chip_group.removeAllViews()
                chaplainFieldArrayList.clear()

            }
            .addOnFailureListener {
                    e -> Log.w("data upload", "Error writing document", e)
            }

        val field = HashMap<String, String>()
        var a = 1
        for (k in chaplainFieldArrayList){
            field[a.toString()] = k
            Log.d("liste", k)
            a += 1
        }
        dbChaplainFieldSave.set(field).addOnCompleteListener {
            Log.d("field", "uploaded")
        }.addOnFailureListener {
            Log.d("field", "failed")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}