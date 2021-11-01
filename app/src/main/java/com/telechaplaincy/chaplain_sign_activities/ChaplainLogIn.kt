package com.telechaplaincy.chaplain_sign_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_sign_activities.ForgotPassword
import kotlinx.android.synthetic.main.activity_chaplain_log_in.*

class ChaplainLogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private var userEmail : String = ""
    private var userPassword: String = ""
    private var chaplainProfileFieldUserId:String = ""
    private var userRole:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_log_in)

        auth = FirebaseAuth.getInstance()

        //sign up activity click listener
        chaplain_login_page_sign_up_textView.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ChaplainSignUp::class.java)
            startActivity(intent)
        })

        //forgot password textView click listener
        chaplain_login_activity_forgot_password_textView.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        //login button click listener
        chaplain_login_page_login_button.setOnClickListener {
            userEmail = chaplain_login_editText_Email.text.toString()
            userPassword = chaplain_login_editText_Password.text.toString()

            if (userEmail == "") {
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
            } else {
                chaplain_login_page_progressBar.visibility = View.VISIBLE
                chaplain_login_page_login_button.isClickable = false

                auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.exception)
                            //Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            Toast.makeText(
                                baseContext, task.exception.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            chaplain_login_page_login_button.isClickable = true
                            chaplain_login_page_progressBar.visibility = View.GONE
                        }
                    }
            }
        }
    }
        //when sign in ok, what will happen
        private fun updateUI(){
            //val toast = Toast.makeText(this, R.string.sign_up_toast_account_created, Toast.LENGTH_SHORT).show()
            chaplain_login_page_progressBar.visibility = View.GONE
            chaplain_login_page_login_button.isClickable = true
            checkUserRole()

        }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainEntry::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        /*// Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            checkUserRole()
        }*/
    }
    private fun checkUserRole(){
        chaplainProfileFieldUserId = auth.currentUser?.uid ?: chaplainProfileFieldUserId
        val docRef = db.collection("users").document(chaplainProfileFieldUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.data?.containsKey("userRole") == true){
                        val data = document.data as Map<String, String>
                        userRole = data["userRole"].toString()
                        if (userRole == "2"){
                            val intent = Intent(this, ChaplainMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else if (userRole == "1"){
                            val intent = Intent(this, PatientMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    Log.d("TAG", "DocumentSnapshot data: $userRole")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

    }
   /* private fun alertDialogMessage(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sorry")
        builder.setMessage(R.string.sign_up_toast_message_user_role)
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, which ->
            //Toast.makeText(baseContext, R.string.sign_up_toast_message_user_role, Toast.LENGTH_SHORT).show()
            auth.signOut()
            val intent = Intent(this, MainEntry::class.java)
            startActivity(intent)
            finish()
        }
        builder.show()
    }*/
}