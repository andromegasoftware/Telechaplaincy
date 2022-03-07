package com.telechaplaincy.patient_sign_activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainSignUpSecondPart
import kotlinx.android.synthetic.main.activity_profile_image_edit.*
import java.io.File

class ProfileImageEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference

    private val db = Firebase.firestore
    private var chaplainCollectionName: String = ""
    private var chaplainProfileCollectionName: String = ""
    private var chaplainProfileDocumentName: String = ""

    private var chaplainProfileFieldUserId: String = ""
    private var isImageEmpty: Boolean = false
    private lateinit var imageFile: Uri
    private var imageLink: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_image_edit)

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        if (auth.currentUser != null) {
            user = auth.currentUser!!
            chaplainProfileFieldUserId = user.uid

        }

        chaplainCollectionName = getString(R.string.chaplain_collection)
        chaplainProfileCollectionName = getString(R.string.chaplain_profile_collection)
        chaplainProfileDocumentName = getString(R.string.chaplain_profile_document)

        setCurrentImageToImageView()

        //chaplain profile image selection
        profile_image_edit_activity_select_image_button.setOnClickListener {
            pickImage()
            if (profile_image_edit_activity_imageView.drawable != null) {
                isImageEmpty = true
            }
        }
    }

    private fun setCurrentImageToImageView() {
        imageLink = intent.getStringExtra("chaplainProfileImageLink").toString()
        if (imageLink != "null" && imageLink != "") {
            Picasso.get().load(imageLink)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)
                .into(profile_image_edit_activity_imageView)
        }
    }

    private fun saveImageLinkToDatabase() {
        val profileImageLink = hashMapOf("profileImageLink" to imageLink)
        db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
            .set(profileImageLink, SetOptions.merge()).addOnCompleteListener {

                profile_image_edit_activity_progressBar.visibility = View.GONE
                profile_image_edit_activity_select_image_button.isClickable = true
            }
            .addOnFailureListener {
                Log.d("user complete: ", "not added")
                profile_image_edit_activity_progressBar.visibility = View.GONE
                profile_image_edit_activity_select_image_button.isClickable = true
            }
    }

    private fun uploadImage() {
        profile_image_edit_activity_progressBar.visibility = View.VISIBLE
        profile_image_edit_activity_select_image_button.isClickable = false
        val ref = storageReference.child("chaplain").child(chaplainProfileFieldUserId)
            .child("profile photo").child("Chaplain Profile Photo")

        val uploadTask = ref.putFile(imageFile)
            .addOnFailureListener {
                Log.d("Image: ", "Image could not uploaded")
            }
            .addOnSuccessListener {
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
                saveImageLinkToDatabase()

                Log.d("ImageLink: ", imageLink)
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
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

                profile_image_edit_activity_imageView.setImageBitmap(imageBitmap)
                isImageEmpty = true
                uploadImage()
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainSignUpSecondPart::class.java)
        startActivity(intent)
        finish()
    }
}