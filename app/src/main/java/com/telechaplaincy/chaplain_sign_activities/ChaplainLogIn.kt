package com.telechaplaincy.chaplain_sign_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.telechaplaincy.ChaplainMainActivity
import com.telechaplaincy.MainActivity
import com.telechaplaincy.R
import com.telechaplaincy.patient_sign_activities.ForgotPassword
import com.telechaplaincy.patient_sign_activities.LoginPage
import com.telechaplaincy.patient_sign_activities.SignUpActivity
import kotlinx.android.synthetic.main.activity_chaplain_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.*

class ChaplainLogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var userEmail : String = ""
    private var userPassword: String = ""

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

        //patient login page opening click listener
        chaplain_login_activity_patient_textView.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
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
            val intent = Intent(this, ChaplainMainActivity::class.java)
            startActivity(intent)
            //val toast = Toast.makeText(this, R.string.sign_up_toast_account_created, Toast.LENGTH_SHORT).show()
            chaplain_login_page_progressBar.visibility = View.GONE
            chaplain_login_page_login_button.isClickable = true
            finish()

        }
}