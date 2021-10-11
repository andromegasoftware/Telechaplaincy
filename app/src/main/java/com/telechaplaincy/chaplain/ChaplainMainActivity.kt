package com.telechaplaincy.chaplain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainLogIn
import kotlinx.android.synthetic.main.activity_chaplain_main.*

class ChaplainMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_main)

        auth = FirebaseAuth.getInstance()

        chaplain_logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, ChaplainLogIn::class.java)
            startActivity(intent)
            finish()
        }
    }
}