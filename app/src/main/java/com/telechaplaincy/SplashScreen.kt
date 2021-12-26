package com.telechaplaincy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.patient.PatientMainActivity
import kotlinx.android.synthetic.main.activity_splash__screen.*

class SplashScreen : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 4000

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashScreenTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash__screen)

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        textViewSplashScreenAppName.startAnimation(animation)
        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            val intent = Intent(this, PatientMainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}