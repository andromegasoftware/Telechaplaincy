package com.telechaplaincy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.telechaplaincy.chaplain_sign_activities.ChaplainLogIn
import com.telechaplaincy.patient_sign_activities.ForgotPassword
import com.telechaplaincy.patient_sign_activities.LoginPage
import kotlinx.android.synthetic.main.activity_main_entry.*

class MainEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_entry)

        patient_entry_button.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
        chaplain_entry_button.setOnClickListener {
            val intent = Intent(this, ChaplainLogIn::class.java)
            startActivity(intent)
            finish()
        }
    }


}