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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.birth_date_class.DateMask
import com.telechaplaincy.chaplain.ChaplainMainActivity
import kotlinx.android.synthetic.main.activity_chaplain_sign_up.*
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import java.util.*
import kotlin.collections.ArrayList

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

    private var chaplainProfileFirstName:String = ""
    private var chaplainProfileLastName:String = ""
    private var chaplainProfileEmail:String = ""
    private var chaplainProfileImageLink:String = ""
    private var chaplainProfileFieldUserId:String = ""
    private var chaplainProfileAddresTitle:String = ""
    private var chaplainProfileCredentialTitle:String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileFieldPhone:String = ""
    private var chaplainProfileFieldBirthDate:String = ""
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
    private var chaplainAccountStatus = "1"

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
            chaplainProfileEmail = user.email.toString()
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        chaplainCollectionName = getString(R.string.chaplain_collection)
        chaplainProfileCollectionName = getString(R.string.chaplain_profile_collection)
        chaplainProfileDocumentName = getString(R.string.chaplain_profile_document)

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        //this part makes editText birth date format like dd/mm/yyyy
        DateMask(chaplainSignUpeditTextBirthDate).listen()

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

        chaplain_sign_up_page_cancel_button.setOnClickListener {
            val intent = Intent(this, ChaplainMainActivity::class.java)
            startActivity(intent)
            finish()
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
        chaplainProfileFirstName = chaplainSignUpeditTextName.text.toString()
        chaplainProfileLastName = chaplainSignUpeditTextLastName.text.toString()
        chaplainProfileFieldPhone = chaplainSignUpeditTextPhone.text.toString()
        chaplainProfileFieldBirthDate = chaplainSignUpeditTextBirthDate.text.toString()
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

    //to save chaplain profile info when the user click the save button
    private fun saveData() {
        chaplain_cv_progressBarUploadData.visibility = View.VISIBLE
        chaplain_sign_up_page_next_button.isClickable = false

        val chaplainUserProfile = ChaplainUserProfile(
            chaplainProfileFirstName,
            chaplainProfileLastName,
            chaplainProfileEmail,
            chaplainProfileFieldUserId,
            chaplainProfileImageLink,
            chaplainProfileAddresTitle,
            chaplainProfileFieldPhone,
            chaplainProfileFieldBirthDate,
            chaplainProfileFieldEducation,
            chaplainProfileFieldExperience,
            chaplainProfileFieldPreferredLanguage,
            chaplainProfileFieldSsn,
            chaplainProfileOrdained,
            chaplainProfileOrdainedPeriod,
            chaplainProfileAddCridentials,
            chaplainProfileExplanation,
            chaplainCvUrl,
            chaplainCertificateUrl,
            chaplainCertificateName,
            chaplainResumeName,
            credentialTitleArrayList,
            faithArrayList,
            ethnicArrayList,
            otherLanguagesArrayList,
            chaplainFieldArrayList,
            chaplainAccountStatus
        )

        dbSave.set(chaplainUserProfile, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
                chaplain_cv_progressBarUploadData.visibility = View.GONE
                chaplain_sign_up_page_next_button.isClickable = true
                chaplain_sign_up_page_next_button.text =
                    getString(R.string.chaplain_sign_up_next_button_after_save)
                val toast = Toast.makeText(
                    this,
                    R.string.sign_up_toast_message_data_uploaded,
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener { e ->
                Log.w("data upload", "Error writing document", e)
                chaplain_cv_progressBarUploadData.visibility = View.GONE
                chaplain_sign_up_page_next_button.isClickable = true
                val toast = Toast.makeText(
                    this,
                    R.string.sign_up_toast_message_data_not_uploaded,
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
    public fun readData(){
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                val chaplainUserProfile = document.toObject<ChaplainUserProfile>()
                if (chaplainUserProfile != null) {

                    chaplainAccountStatus = chaplainUserProfile.accountStatus.toString()

                    if (chaplainUserProfile.addressingTitle != null) {
                        chaplainProfileAddresTitle = chaplainUserProfile.addressingTitle.toString()
                        val optionAddress = resources.getStringArray(R.array.addressingTitleArray)
                        spinnerAdressingTitle.setSelection(optionAddress.indexOf(chaplainProfileAddresTitle))
                    }

                    chaplainProfileFirstName = chaplainUserProfile.name.toString()
                    if (chaplainProfileFirstName != "null") {
                        chaplainSignUpeditTextName.setText(chaplainProfileFirstName)
                    }

                    chaplainProfileLastName = chaplainUserProfile.surname.toString()
                    if (chaplainProfileLastName != "null") {
                        chaplainSignUpeditTextLastName.setText(chaplainProfileLastName)
                    }

                    chaplainProfileImageLink = chaplainUserProfile.profileImageLink.toString()
                    if (chaplainProfileImageLink != "null") {
                        val picasso = Picasso.get().load(chaplainProfileImageLink).placeholder(R.drawable.ic_baseline_account_circle_24).error(R.drawable.ic_baseline_account_circle_24).into(chaplain_sign_up_second_profile_image)
                    }

                    chaplainProfileFieldPhone = chaplainUserProfile.phone.toString()
                    if (chaplainProfileFieldPhone != "null") {
                        chaplainSignUpeditTextPhone.setText(chaplainProfileFieldPhone)
                    }

                    chaplainProfileFieldBirthDate = chaplainUserProfile.birthDate.toString()
                    if (chaplainProfileFieldBirthDate != "null") {
                        chaplainSignUpeditTextBirthDate.setText(chaplainProfileFieldBirthDate)
                    }

                    chaplainProfileFieldEducation = chaplainUserProfile.education.toString()
                    if (chaplainProfileFieldEducation != "null") {
                        chaplainSignUpeditTextEducation.setText(chaplainProfileFieldEducation)
                    }

                    chaplainProfileFieldExperience = chaplainUserProfile.experience.toString()
                    if (chaplainProfileFieldExperience != "null") {
                        chaplainSignUpeditTextExperience.setText(chaplainProfileFieldExperience)
                    }

                    chaplainProfileFieldPreferredLanguage = chaplainUserProfile.language.toString()
                    if (chaplainProfileFieldPreferredLanguage != "null") {
                        chaplainSignUpeditTextPreferredLang.setText(chaplainProfileFieldPreferredLanguage)
                    }

                    chaplainProfileFieldSsn = chaplainUserProfile.ssn.toString()
                    if (chaplainProfileFieldSsn != "null") {
                        chaplainSignUpeditTextSsn.setText(chaplainProfileFieldSsn)
                    }

                    chaplainProfileOrdained = chaplainUserProfile.ordainedName.toString()
                    if (chaplainProfileOrdained != "null") {
                        chaplainSignUpeditTextOrdeined.setText(chaplainProfileOrdained)
                    }

                    chaplainProfileOrdainedPeriod = chaplainUserProfile.ordainedPeriod.toString()
                    if (chaplainProfileOrdainedPeriod != "null") {
                        chaplainSignUpeditTextOrdPeriod.setText(chaplainProfileOrdainedPeriod)
                    }

                    chaplainProfileAddCridentials = chaplainUserProfile.additionalCredential.toString()
                    if (chaplainProfileAddCridentials != "null") {
                        chaplainSignUpeditAddCrident.setText(chaplainProfileAddCridentials)
                    }

                    chaplainProfileExplanation = chaplainUserProfile.bio.toString()
                    if (chaplainProfileExplanation != "null") {
                        chaplainSignUpeditTextExp.setText(chaplainProfileExplanation)
                    }

                    chaplainCertificateName = chaplainUserProfile.certificateName.toString()
                    if (chaplainCertificateName != "null") {
                        if (chaplainCertificateName != "") {
                            chaplain_certificate_name_lineer_layout.visibility = View.VISIBLE
                            certificateFileNameTextView.text = chaplainCertificateName
                        }
                    }

                    chaplainResumeName = chaplainUserProfile.cvName.toString()
                    if (chaplainResumeName != "null") {
                        if (chaplainResumeName != "") {
                            chaplain_cv_name_show_lineer_layout.visibility = View.VISIBLE
                            resumeFileNameTextView.text = chaplainResumeName
                        }
                    }

                    chaplainCvUrl = chaplainUserProfile.cvUrl.toString()

                    chaplainCertificateUrl = chaplainUserProfile.certificateUrl.toString()

                    if (chaplainUserProfile.credentialsTitle != null) {
                        credentialTitleArrayList = chaplainUserProfile.credentialsTitle

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

                    if (chaplainUserProfile.faith != null) {
                        faithArrayList = chaplainUserProfile.faith

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

                    if (chaplainUserProfile.ethnic != null) {
                        ethnicArrayList = chaplainUserProfile.ethnic

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

                    if (chaplainUserProfile.otherLanguages != null) {
                        otherLanguagesArrayList = chaplainUserProfile.otherLanguages

                        for (k in otherLanguagesArrayList.indices) {
                            val chipChaplainOtherLanguage = Chip(this@ChaplainSignUpSecondPart)
                            chipChaplainOtherLanguage.isCloseIconVisible = true
                            chipChaplainOtherLanguage.setChipBackgroundColorResource(R.color.colorAccent)
                            chipChaplainOtherLanguage.setTextColor(resources.getColor(R.color.colorPrimary))
                            chipChaplainOtherLanguage.text = otherLanguagesArrayList[k]
                            chaplain_other_languages_chip_group.addView(
                                chipChaplainOtherLanguage
                            )

                            chipChaplainOtherLanguage.setOnClickListener {
                                chaplain_other_languages_chip_group.removeView(
                                    chipChaplainOtherLanguage
                                )
                                otherLanguagesArrayList.remove(chipChaplainOtherLanguage.text)
                            }
                        }
                    }

                    if (chaplainUserProfile.field != null) {
                        chaplainFieldArrayList = chaplainUserProfile.field

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
                } else {
                    Log.d("TAG", "No such document")
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }

    fun saveDataInBackground() {
        val chaplainUserProfile = ChaplainUserProfile(
            chaplainProfileFirstName,
            chaplainProfileLastName,
            chaplainProfileEmail,
            chaplainProfileFieldUserId,
            chaplainProfileImageLink,
            chaplainProfileAddresTitle,
            chaplainProfileFieldPhone,
            chaplainProfileFieldBirthDate,
            chaplainProfileFieldEducation,
            chaplainProfileFieldExperience,
            chaplainProfileFieldPreferredLanguage,
            chaplainProfileFieldSsn,
            chaplainProfileOrdained,
            chaplainProfileOrdainedPeriod,
            chaplainProfileAddCridentials,
            chaplainProfileExplanation,
            chaplainCvUrl,
            chaplainCertificateUrl,
            chaplainCertificateName,
            chaplainResumeName,
            credentialTitleArrayList,
            faithArrayList,
            ethnicArrayList,
            otherLanguagesArrayList,
            chaplainFieldArrayList,
            chaplainAccountStatus
        )

        dbSave.set(chaplainUserProfile, SetOptions.merge())
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
            .addOnFailureListener { e ->
                Log.w("data upload", "Error writing document", e)
            }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}