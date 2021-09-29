package com.telechaplaincy.patient_sign_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.MainActivity
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var userName: String = ""
    private var userSurName: String = ""
    private var userEmail : String = ""
    private var userPassword: String = ""
    private var userPasswordAgain: String = ""
    private var termsChecked: Boolean = false

    private val db = Firebase.firestore
    private var patientCollectionName:String = ""
    private var patientUidDocumentName:String = ""
    private var patientProfileCollectionName:String = ""
    private var patientProfileDocumentName:String = ""
    private var patientProfileFieldName:String = ""
    private var patientProfileFieldSurname:String = ""
    private var patientProfileFieldEmail:String = ""
    private var patientProfileFieldUserId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        patientCollectionName = getString(R.string.patient_collection)
        patientProfileCollectionName = getString(R.string.patient_profile_collection)
        patientProfileDocumentName = getString(R.string.patient_profile_document)


        //go back login page textView click listener
        sign_up_page_go_back_login.setOnClickListener {
            finish()
        }

        //sign up button click listener
        sign_up_page_signUp_button.setOnClickListener {
            userName = sign_up_page_name_editText.text.toString()
            userSurName = sign_up_page_surname_editText.text.toString()
            userEmail = sign_up_page_email_editText.text.toString()
            userPassword = sign_up_page_password_editText.text.toString()
            userPasswordAgain = sign_up_page_password_again_editText.text.toString()
            termsChecked = sign_up_agree_checkBox.isChecked

            if(userName == ""){
                val toast = Toast.makeText(this, R.string.sign_up_toast_message_enter_name, Toast.LENGTH_SHORT).show()
            }
            else if(userSurName == ""){
                val toast = Toast.makeText(this,
                    R.string.sign_up_toast_message_enter_sur_name, Toast.LENGTH_SHORT).show()
            }
            else if(userEmail == ""){
                val toast = Toast.makeText(this, R.string.sign_up_toast_message_enter_email, Toast.LENGTH_SHORT).show()
            }
            else if(userPassword == ""){
                val toast = Toast.makeText(this,
                    R.string.sign_up_toast_message_enter_password, Toast.LENGTH_SHORT).show()
            }
            else if(userPasswordAgain == ""){
                val toast = Toast.makeText(this,
                    R.string.sign_up_toast_message_enter_password_again, Toast.LENGTH_SHORT).show()
            }
            else if(userPassword != userPasswordAgain){
                val toast = Toast.makeText(this,
                    R.string.sign_up_toast_message_password_match, Toast.LENGTH_SHORT).show()
            }
            else if(userPassword.length < 6 ){
                val toast = Toast.makeText(this, R.string.sign_up_toast_message_password_long, Toast.LENGTH_SHORT).show()
            }
            else if(!termsChecked){
                val toast = Toast.makeText(this,
                    R.string.sign_up_toast_message_checkBox_isChecked, Toast.LENGTH_SHORT).show()
            }
            else{
                sign_up_page_signUp_button.isClickable = false
                sign_up_page_progressBar.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d("userIsCreated", "createUserWithEmail:success")
                           // val user = auth.currentUser
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w("userIsNotCreated", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                            sign_up_page_signUp_button.isClickable = true
                            sign_up_page_progressBar.visibility = View.GONE
                        }
                    }
            }
        }
    }
    private fun updateUI(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        val toast = Toast.makeText(this, R.string.sign_up_toast_account_created, Toast.LENGTH_SHORT).show()
        sign_up_page_progressBar.visibility = View.GONE

        val userId = auth.currentUser?.uid
        val profile = userId?.let { UserProfile(userName, userSurName, userEmail, it) }
        if (profile != null) {
            db.collection(patientCollectionName).document(userId)
                .collection(patientProfileCollectionName).document(patientProfileDocumentName)
                .set(profile)
        }

        finish()

    }

}