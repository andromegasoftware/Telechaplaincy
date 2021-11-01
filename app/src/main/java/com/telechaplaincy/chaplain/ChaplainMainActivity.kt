package com.telechaplaincy.chaplain

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainLogIn
import com.telechaplaincy.chaplain_sign_activities.ChaplainSignUpSecondPart
import kotlinx.android.synthetic.main.activity_chaplain_main.*
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChaplainMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference
    private var chaplainCollectionName:String = ""
    private var chaplainProfileCollectionName:String = ""
    private var chaplainProfileDocumentName:String = ""
    private var chaplainProfileFieldUserId:String = ""

    private var chaplainProfileAddresTitle:String = ""
    private var chaplainProfileCredentialTitle:String = ""
    private var chaplainProfileFieldPhone:String = ""
    private var chaplainProfileFieldBirthDate:String = "0"
    private var chaplainProfileFieldEducation:String = ""
    private var chaplainProfileFieldExperience:String = ""
    private var chaplainProfileFieldChaplainField:String = ""
    private var chaplainProfileFieldPreferredLanguage:String = ""
    private var chaplainProfileFieldSsn:String = ""
    var chaplainCvUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage
    var chaplainCertificateUrl: String = ""   // this is chaplain uploaded cv link to reach cv later in the storage
    private var chaplainAccountStatus = "1"

    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var dbChaplainFieldSave: DocumentReference
    private lateinit var dbChaplainAccountStatus: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_main)

        addingActivitiesToBottomMenu()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            chaplainProfileFieldUserId = user.uid
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        chaplainCollectionName = getString(R.string.chaplain_collection)
        chaplainProfileCollectionName = getString(R.string.chaplain_profile_collection)
        chaplainProfileDocumentName = getString(R.string.chaplain_profile_document)
        val chaplainField = "chaplain field"

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
            .collection(chaplainProfileCollectionName).document(chaplainProfileDocumentName)
        dbChaplainFieldSave = db.collection(chaplainCollectionName)
            .document(chaplainProfileFieldUserId).collection(chaplainField).document("field")

        dbChaplainAccountStatus = db.collection(chaplainCollectionName)
            .document(chaplainProfileFieldUserId).collection("account").document("status")

        readData() //to check if the chaplain profile filled in fully or not
        readAccountStatus()//to check chaplain account status

        chaplain_logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, ChaplainLogIn::class.java)
            startActivity(intent)
            finish()
        }

        chaplain_signUp_continue_button.setOnClickListener {
            checkSignUpData()

        }
    }

    private fun readAccountStatus(){
        dbChaplainAccountStatus.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("account", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                chaplainAccountStatus = snapshot.data?.get("accountStatus").toString()
                Log.d("account", chaplainAccountStatus)
            }
        }
    }

    //this method will read the chaplain profile data from the firebase
    private fun readData(){

        //this part is for the realtime listening, if we need this
        /*dbSave.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                chaplainProfileAddresTitle = snapshot.data?.get("addressing title").toString()
                Log.d("CProfile a", chaplainProfileAddresTitle)

                chaplainProfileFieldPhone = snapshot.data?.get("phone").toString()
                Log.d("CProfile b", chaplainProfileFieldPhone)

                var birthDate = Timestamp(Date(chaplainProfileFieldBirthDate.toLong()))
                birthDate = (snapshot.data?.get("birth date") as? com.google.firebase.Timestamp)!!
                val millisecondsLong = birthDate.seconds * 1000 + birthDate.nanoseconds / 1000000
                chaplainProfileFieldBirthDate = millisecondsLong.toString()
                Log.d("CProfile d", chaplainProfileFieldBirthDate)

                chaplainProfileFieldEducation = snapshot.data?.get("education").toString()
                Log.d("CProfile e", chaplainProfileFieldEducation)

                chaplainProfileFieldExperience = snapshot.data?.get("experiance").toString()
                Log.d("CProfile f", chaplainProfileFieldExperience)

                chaplainProfileFieldPreferredLanguage =
                    snapshot.data?.get("language").toString()
                Log.d("CProfile g", chaplainProfileFieldPreferredLanguage)

                chaplainProfileFieldSsn = snapshot.data?.get("ssn").toString()
                Log.d("CProfile h", chaplainProfileFieldSsn)

                chaplainCvUrl = snapshot.data?.get("cv url").toString()
                Log.d("CProfile i", chaplainCvUrl)

                chaplainCertificateUrl = snapshot.data?.get("certificate url").toString()
                Log.d("CProfile j", chaplainCertificateUrl)

                chaplainProfileCredentialTitle = snapshot.data?.get("cridentials title").toString()
                Log.d("CProfile k", chaplainProfileCredentialTitle)


            }

        }

        dbChaplainFieldSave.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("CProfile c", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                chaplainProfileFieldChaplainField = snapshot.data.toString()
                Log.d("CProfile c", chaplainProfileFieldChaplainField)
            }
        }*/

        //this part is to get the profile data from the firestore
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                if (document.data?.containsKey("addressing title") == true) {
                    chaplainProfileAddresTitle = document.data?.get("addressing title")
                        .toString() //if it is empty, it will return "Nothing Selected"
                    //Log.d("CProfile a", chaplainProfileAddresTitle)
                }
                if (document.data?.containsKey("phone") == true) {
                    chaplainProfileFieldPhone = document.data?.get("phone").toString()
                    //Log.d("CProfile b", chaplainProfileFieldPhone)
                }


                if (document.data?.containsKey("birth date") == true) {
                    var birthDate = Timestamp(Date(chaplainProfileFieldBirthDate.toLong()))
                    birthDate = (document.data?.get("birth date") as? com.google.firebase.Timestamp)!!
                    val millisecondsLong = birthDate.seconds * 1000 + birthDate.nanoseconds / 1000000
                    chaplainProfileFieldBirthDate = millisecondsLong.toString()
                    //Log.d("CProfile d", chaplainProfileFieldBirthDate)
                }


                if (document.data?.containsKey("education") == true) {
                    chaplainProfileFieldEducation = document.data?.get("education").toString()
                    //Log.d("CProfile e", chaplainProfileFieldEducation)
                }

                if (document.data?.containsKey("experiance") == true) {
                    chaplainProfileFieldExperience = document.data?.get("experiance").toString()
                    //Log.d("CProfile f", chaplainProfileFieldExperience)
                }

                if (document.data?.containsKey("language") == true) {
                    chaplainProfileFieldPreferredLanguage =
                        document.data?.get("language").toString()
                    //Log.d("CProfile g", chaplainProfileFieldPreferredLanguage)
                }

                if (document.data?.containsKey("ssn") == true) {
                    chaplainProfileFieldSsn = document.data?.get("ssn").toString()
                    //Log.d("CProfile h", chaplainProfileFieldSsn)
                }

                if (document.data?.containsKey("cv url") == true) {
                    chaplainCvUrl = document.data?.get("cv url").toString()
                   // Log.d("CProfile i", chaplainCvUrl)
                }

                if (document.data?.containsKey("certificate url") == true) {
                    chaplainCertificateUrl = document.data?.get("certificate url").toString()
                   // Log.d("CProfile j", chaplainCertificateUrl)
                }

                if (document.data?.containsKey("cridentials title") == true) {
                    chaplainProfileCredentialTitle = document.data?.get("cridentials title").toString()
                    //Log.d("CProfile k", chaplainProfileCredentialTitle)
                }
            }

            else{
               // Log.d("CProfile", "no such a document")
            }
            Log.d("CProfile", document.data.toString())

        }.addOnFailureListener { exception ->
                //Log.d("CProfile", "get failed with ", exception)
        }


        dbChaplainFieldSave.get().addOnSuccessListener { document ->
            if (document != null) {
                //Log.d("CField", "DocumentSnapshot data: ${document.data}")
                if (document.data?.containsKey("1") == true) {
                    chaplainProfileFieldChaplainField = document.data.toString()
                }
                //Log.d("CProfile L", chaplainProfileFieldChaplainField)
            }
            else {
               // Log.d("CField", "No such document")
            }
            Log.d("CProfile1", document.data.toString())
        }
            .addOnFailureListener { exception ->
                //Log.d("CField", "get failed with ", exception)

            }

    }

    //This method will check if the chaplain filled in the all necessary fields in the sign up page
    private fun checkSignUpData(){
        if (chaplainProfileAddresTitle != "" && chaplainProfileAddresTitle != "Nothing Selected"
            && chaplainProfileCredentialTitle != ""
            && chaplainProfileFieldPhone != "" && chaplainProfileFieldBirthDate != "0"
            && chaplainProfileFieldEducation != "" && chaplainProfileFieldExperience != ""
            && chaplainProfileFieldPreferredLanguage != ""
            && chaplainProfileFieldSsn != "" && chaplainCvUrl != "" && chaplainCertificateUrl != ""
            && chaplainProfileFieldChaplainField != ""){

            chaplain_signUp_continue_button.visibility = View.GONE
            val toast = Toast.makeText(this, "You entered every necessary info. You can change your info from the profile page", Toast.LENGTH_LONG).show()
            /*Log.d("Button", "OK")
            Log.d("Button", chaplainProfileAddresTitle)*/
        }else{
            val intent = Intent(this, ChaplainSignUpSecondPart::class.java)
            startActivity(intent)
            finish()
            /*Log.d("Button", "NOT OK")
            Log.d("Button a", chaplainProfileAddresTitle)
            Log.d("Button b", chaplainProfileFieldPhone)
            Log.d("Button d", chaplainProfileFieldBirthDate)
            Log.d("Button e", chaplainProfileFieldEducation)
            Log.d("Button f", chaplainProfileFieldExperience)
            Log.d("Button g", chaplainProfileFieldPreferredLanguage)
            Log.d("Button h", chaplainProfileFieldSsn)
            Log.d("Button i", chaplainCvUrl)
            Log.d("Button j", chaplainCertificateUrl)
            Log.d("Button k", chaplainProfileCredentialTitle)*/
        }
    }


    fun addingActivitiesToBottomMenu(){

        bottomNavigation.selectedItemId = R.id.bottom_menu_home

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_menu_home -> {

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