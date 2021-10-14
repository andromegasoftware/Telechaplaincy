package com.telechaplaincy.chaplain_sign_activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import kotlinx.android.synthetic.main.activity_chaplain_sign_up.*
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

    private var chaplainProfileFieldUserId:String = ""
    private var isImageEmpty:Boolean = false
    private lateinit var imageFile: Uri
    private var imageLink: String = ""

      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_sign_up)

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

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

        //sign up button click listener
        chaplain_sign_up_page_signUp_button.setOnClickListener {
            takeProfileInfoFirstPart()

        }
    }

    private fun uploadImage(){
        if (auth.currentUser != null){
            user = auth.currentUser!!
            chaplainProfileFieldUserId = user.uid

        }
        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId)
            .child("profile photo").child("Chaplain Profile Photo")

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
                imageLink = task.result.toString()
                updateUI()

                Log.d("ImageLink: ", imageLink)
            } else {
                // Handle failures
                // ...
            }
        }

    }
    private fun updateUI(){

        //profile info saved to the firestore
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        val toast = Toast.makeText(this, R.string.sign_up_toast_account_created, Toast.LENGTH_SHORT).show()
        chaplain_signUp_page_progressBar.visibility = View.GONE
        chaplain_sign_up_page_signUp_button.isClickable = true

        val profile = ChaplainUserProfile(userName, userSurName, userEmail, chaplainProfileFieldUserId, imageLink)

        db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
           .collection(chaplainProfileCollectionName).document(chaplainProfileDocumentName)
           .set(profile).addOnCompleteListener {
                Log.d("user complete: ", "added")
                Log.d("user complete i: ", imageLink)
            }
            .addOnFailureListener {
                Log.d("user complete: ", "not added")
            }

        finish()
        val chaplainLogIn = ChaplainLogIn()
        chaplainLogIn.finish()

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
                    uploadImage()
                }
                else{
                    Toast.makeText(
                        baseContext, task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    chaplain_sign_up_page_signUp_button.isClickable = true
                    chaplain_signUp_page_progressBar.visibility = View.GONE
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

            }
            if (uri != null) {
                val imageBitmap = uriToBitmap(uri)

                chaplain_sign_up_profile_image.setImageBitmap(imageBitmap)
                isImageEmpty = true
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