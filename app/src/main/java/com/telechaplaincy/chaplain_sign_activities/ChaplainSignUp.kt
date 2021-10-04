package com.telechaplaincy.chaplain_sign_activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_chaplain_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.sign_up_page_go_back_login
import java.io.File
import java.util.jar.Manifest

class ChaplainSignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_sign_up)

        auth = FirebaseAuth.getInstance()

        chaplainCollectionName = getString(R.string.chaplain_collection)
        chaplainProfileCollectionName = getString(R.string.chaplain_profile_collection)
        chaplainProfileDocumentName = getString(R.string.chaplain_profile_document)

        //go back login page textView click listener
        chaplain_sign_up_page_go_back_login.setOnClickListener {
            finish()
        }

        //chaplain profile image selection
        chaplain_sign_up_profile_image.setOnClickListener {
            pickImage()
            if (chaplain_sign_up_profile_image.drawable != null){
                isImageEmpty = true
            }
        }
        chaplain_sign_up_page_back_button.setOnClickListener {
            chaplain_profile_first_step_layout.visibility = View.VISIBLE
            chaplain_profile_second_step_layout.visibility = View.GONE
        }

        //chaplain profile second page next button and profile info taking
        chaplain_sign_up_page_next_button.setOnClickListener {
            takeProfileInfo()

        }

        //sign up button click listener
        chaplain_sign_up_page_signUp_button.setOnClickListener {
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
                chaplain_profile_first_step_layout.visibility = View.GONE
                chaplain_profile_second_step_layout.visibility = View.VISIBLE
                scrollView.smoothScrollTo(0,0)

            }
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

        if (chaplainProfileFieldPhone == ""){
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
        else if (chaplainProfileFieldChaplainField == ""){
            val toast = Toast.makeText(
                this,
                R.string.sign_up_toast_message_enter_chaplain_field,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun pickImage() {
        if (ActivityCompat.checkSelfPermission(this,
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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