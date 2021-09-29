package com.telechaplaincy.patient_sign_activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        forgot_password_imageview.setImageResource(R.mipmap.box)

        forgot_password_continue_button.setOnClickListener {
            val userEmail = forgot_password_editText_Email.text.toString()
            forgot_password_continue_button.isClickable = false
            forgot_password_progressBar.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("forgot_password", "Email sent.")

                        forgot_password_continue_button.isClickable = true
                        forgot_password_progressBar.visibility = View.GONE

                        forgot_password_first_lineer_layout.visibility = View.GONE
                        forgot_password_second_lineer_layout.visibility = View.VISIBLE
                    }
                    else{
                        val toast = Toast.makeText(this,
                            R.string.forgot_password_activity_toast_message, Toast.LENGTH_SHORT).show()
                    }
                }

        }

        forgot_password_backtologin_button.setOnClickListener {
            finish()
        }
    }
}