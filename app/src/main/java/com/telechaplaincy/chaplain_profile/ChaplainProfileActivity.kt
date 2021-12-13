package com.telechaplaincy.chaplain_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainLogIn
import com.telechaplaincy.chaplain_sign_activities.ChaplainSignUpSecondPart
import com.telechaplaincy.video_call.ChaplainVideoActivity
import com.telechaplaincy.video_call.VideoCallActivity
import kotlinx.android.synthetic.main.activity_chaplain_main.*
import kotlinx.android.synthetic.main.activity_chaplain_main.bottomNavigation
import kotlinx.android.synthetic.main.activity_chaplain_profile.*

class ChaplainProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_profile)

        auth = FirebaseAuth.getInstance()

        addingActivitiesToBottomMenu()

        buttonJoinCall.setOnClickListener {
            val intent = Intent(this, ChaplainVideoActivity::class.java)
            //intent.putExtra("chaplainUniqueUserId", "437897592")
            //intent.putExtra("token", "006afb5ee413d864417bbcf32ba55b40dacIAAlFbcY+uDAu62JeS1VOelMuwkiAysz7GgsnahbgknOIET4OP5cLkU+IgA6TwEAl5G4YQQAAQAnTrdhAwAnTrdhAgAnTrdhBAAnTrdh")
            startActivity(intent)
        }

        chaplain_logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, ChaplainLogIn::class.java)
            startActivity(intent)
            finish()
        }

        profile_page_button.setOnClickListener {
            val intent = Intent(this, ChaplainSignUpSecondPart::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun addingActivitiesToBottomMenu(){

        bottomNavigation.selectedItemId = R.id.bottom_menu_profile

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_menu_home -> {
                    val intent = Intent(this, ChaplainMainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "main", Toast.LENGTH_LONG).show()
                    finish()
                }

                R.id.bottom_menu_notification -> {
                    //val intent = Intent(this, MyListActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show()
                    //finish()
                }
                R.id.bottom_menu_profile -> {
                    //val intent = Intent(this, ProfileActivity::class.java)
                    //startActivity(intent)
                    Toast.makeText(this, "profile", Toast.LENGTH_LONG).show()
                    //finish()
                }
            }
            true
        }
    }
}