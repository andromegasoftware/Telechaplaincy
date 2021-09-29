package com.telechaplaincy.chaplain_sign_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.R
import com.telechaplaincy.patient_sign_activities.ForgotPassword
import com.telechaplaincy.patient_sign_activities.LoginPage
import kotlinx.android.synthetic.main.activity_chaplain_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.*

class ChaplainLogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_log_in)

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
    }
}