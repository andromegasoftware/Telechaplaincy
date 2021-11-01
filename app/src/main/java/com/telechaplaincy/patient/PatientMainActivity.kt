package com.telechaplaincy.patient

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.patient_sign_activities.LoginPage
import kotlinx.android.synthetic.main.activity_main.*

class PatientMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var userRole:String = ""
    private var userId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        logOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            checkUserRole()
            //Log.d("TAG", "patient main not null")
        }else{
            val intent = Intent(this, MainEntry::class.java)
            startActivity(intent)
            finish()
           // Log.d("TAG", "patient main null")
        }
    }

    private fun checkUserRole(){
        userId = auth.currentUser?.uid ?: userId
        val docRef = db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                progressBarPatientMain.visibility = View.GONE
                if (document != null) {
                    if (document.data?.containsKey("userRole") == true){
                        val data = document.data as Map<String, String>
                        userRole = data["userRole"].toString()
                        if (userRole == "2"){
                            val intent = Intent(this, ChaplainMainActivity::class.java)
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
                progressBarPatientMain.visibility = View.GONE
                Log.d("TAG", "get failed with ", exception)
            }

    }
}