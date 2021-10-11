package com.telechaplaincy.patient_sign_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_log_in.*

class LoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var userEmail : String = ""
    private var userPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()

        //sign up activity click listener
        login_page_sign_up_textView.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        })

        //forgot password textView click listener
        login_activity_forgot_password_textview.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        //login button click listener
        login_page_login_button.setOnClickListener {
            login_page_progressBar.visibility = View.VISIBLE
            login_page_login_button.isClickable = false
            userEmail = login_editText_Email.text.toString()
            userPassword = login_editText_Password.text.toString()

            if(userEmail == ""){
                val toast = Toast.makeText(this, R.string.sign_up_toast_message_enter_email, Toast.LENGTH_SHORT).show()
            }
            else if(userPassword == ""){
                val toast = Toast.makeText(this,
                    R.string.sign_up_toast_message_enter_password, Toast.LENGTH_SHORT).show()
             }
            else {
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
                            login_page_login_button.isClickable = true
                            login_page_progressBar.visibility = View.GONE
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, PatientMainActivity::class.java)
            startActivity(intent)
        }
    }

    //when sign in ok, what will happen
    private fun updateUI(){
        val intent = Intent(this, PatientMainActivity::class.java)
        startActivity(intent)
        //val toast = Toast.makeText(this, R.string.sign_up_toast_account_created, Toast.LENGTH_SHORT).show()
        login_page_progressBar.visibility = View.GONE
        login_page_login_button.isClickable = true
        finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainEntry::class.java)
        startActivity(intent)
        finish()
    }
}