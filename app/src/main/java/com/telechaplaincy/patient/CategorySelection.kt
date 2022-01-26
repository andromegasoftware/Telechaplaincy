package com.telechaplaincy.patient

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_selection.chaplains_selection.ChaplainsListedSelectionActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient_profile.PatientAppointmentPersonalInfo
import kotlinx.android.synthetic.main.activity_category_selection.*
import kotlinx.android.synthetic.main.activity_category_selection.college_chaplain_cardView
import kotlinx.android.synthetic.main.activity_college_chaplain_assessment.*

class CategorySelection : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var result: ChaplainUserProfile
    private lateinit var queryRef:CollectionReference

    private var chaplainCollectionName:String = ""

    private var collegeChaplainCount = 0
    private var humanistChaplainCount = 0
    private var crisesChaplainCount = 0
    private var healthChaplainCount = 0
    private var pallativeChaplainCount = 0
    private var pediatricChaplainCount = 0
    private var mentalChaplainCount = 0
    private var hospiceChaplainCount = 0
    private var lawChaplainCount = 0
    private var militaryChaplainCount = 0
    private var prisonChaplainCount = 0
    private var corporateChaplainCount = 0
    private var petChaplainCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        auth = FirebaseAuth.getInstance()

        chaplainCollectionName = getString(R.string.chaplain_collection)

        queryRef = db.collection(chaplainCollectionName)

        college_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_college_chaplain_title)
            builder.setMessage(R.string.alert_college_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "CollegeChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()
        }

        community_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_community_chaplain_title)
            builder.setMessage(R.string.alert_community_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "HumanistChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        crisis_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_crisis_chaplain_title)
            builder.setMessage(R.string.alert_crisis_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "CrisisChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        health_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_health_care_chaplain_title)
            builder.setMessage(R.string.alert_health_care_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "HealthChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        pallative_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_palliative_chaplain_title)
            builder.setMessage(R.string.alert_palliative_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "PalliativeChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        pediatric_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_pediatric_chaplain_title)
            builder.setMessage(R.string.alert_pediatric_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "PediatricChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        mental_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_mental_chaplain_title)
            builder.setMessage(R.string.alert_mental_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "MentalChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        hospice_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_hospice_chaplain_title)
            builder.setMessage(R.string.alert_hospice_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "HospiceChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        law_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_law_chaplain_title)
            builder.setMessage(R.string.alert_law_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "LawChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        military_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_military_chaplain_title)
            builder.setMessage(R.string.alert_military_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "MilitaryChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        correctional_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_prison_chaplain_title)
            builder.setMessage(R.string.alert_prison_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "PrisonChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        corporate_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_corporate_chaplain_title)
            builder.setMessage(R.string.alert_corporate_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "CorporateChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()

        }
        pet_chaplain_cardView.setOnClickListener {

            //alert dialog create
            val builder = AlertDialog.Builder(this@CategorySelection)
            builder.setTitle(R.string.alert_pet_chaplain_title)
            builder.setMessage(R.string.alert_pet_chaplain)

            // Set up the buttons
            builder.setPositiveButton(
                getString(R.string.chaplain_definitions_alert_continue_button),
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this, ChaplainsListedSelectionActivity::class.java)
                    intent.putExtra("chaplain_category", "PetChaplains")
                    startActivity(intent)
                    finish()
                })
            builder.setNegativeButton(
                getString(R.string.chaplain_definitions_alert_cancel_button),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.show()
        }

        chaplain_field_back_button.setOnClickListener {
            val intent = Intent(this, PatientAppointmentPersonalInfo::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveChaplains(){
        queryCollegeChaplains()
        queryHumanistChaplains()
        queryCrisisChaplains()
        queryHealthChaplains()
        queryPalliativeChaplains()
        queryPediatricChaplains()
        queryMentalChaplains()
        queryHospiceChaplains()
        queryLawChaplains()
        queryMilitaryChaplains()
        queryPrisonChaplains()
        queryCorporateChaplains()
        queryPetChaplains()
    }

    private fun queryPetChaplains(){
        val queryPetChaplain = queryRef.whereArrayContains("field", "Pet Ch.")
            .whereEqualTo("accountStatus", "2")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    petChaplainCount += 1
                }
                if (petChaplainCount == 0){
                    pet_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_pet_chaplain_count.text = "$petChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryCorporateChaplains(){
        val queryCorporateChaplain = queryRef.whereArrayContains("field", "Corporate Ch.")
            .whereEqualTo("accountStatus", "2")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    corporateChaplainCount += 1
                }
                if (corporateChaplainCount == 0){
                    corporate_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_corporate_chaplain_count.text = "$corporateChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryPrisonChaplains(){
        val queryPrisonChaplain = queryRef.whereArrayContains("field", "Prison-Correctional Facilities Ch")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    prisonChaplainCount += 1
                }
                if (prisonChaplainCount == 0){
                    correctional_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_prison_chaplain_count.text = "$prisonChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryMilitaryChaplains(){
        val queryMilitaryChaplain = queryRef.whereArrayContains("field", "Military Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    militaryChaplainCount += 1
                }
                if (militaryChaplainCount == 0){
                    military_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_military_chaplain_count.text = "$militaryChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryLawChaplains(){
        val queryLawChaplain = queryRef.whereArrayContains("field", "Law Enforcement-Police Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    lawChaplainCount += 1
                }
                if (lawChaplainCount == 0){
                    law_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_law_chaplain_count.text = "$lawChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryHospiceChaplains(){
        val queryHospiceChaplain = queryRef.whereArrayContains("field", "Hospice Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    hospiceChaplainCount += 1
                }
                if (hospiceChaplainCount == 0){
                    hospice_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_hospice_chaplain_count.text = "$hospiceChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryMentalChaplains(){
        val queryMentalChaplain = queryRef.whereArrayContains("field", "Mental Health Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    mentalChaplainCount += 1
                }
                if (mentalChaplainCount == 0){
                    mental_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_mental_chaplain_count.text = "$mentalChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryPediatricChaplains(){
        val queryPediatricChaplain = queryRef.whereArrayContains("field", "Pediatric Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    pediatricChaplainCount += 1
                }
                if (pediatricChaplainCount == 0){
                    pediatric_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_pediatric_chaplain_count.text = "$pediatricChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryPalliativeChaplains(){
        val queryPalliativeChaplain = queryRef.whereArrayContains("field", "Palliative Care Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    pallativeChaplainCount += 1
                }
                if (pallativeChaplainCount == 0){
                    pallative_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_pallative_chaplain_count.text = "$pallativeChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryHealthChaplains(){
        val queryHealthChaplain = queryRef.whereArrayContains("field", "Health Care Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    healthChaplainCount += 1
                }
                if (healthChaplainCount == 0){
                    health_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_health_chaplain_count.text = "$healthChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryCrisisChaplains(){
        val queryCrisisChaplain = queryRef.whereArrayContains("field", "Crisis-Disaster Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    crisesChaplainCount += 1
                }
                if (crisesChaplainCount == 0){
                    crisis_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_crisis_chaplain_count.text = "$crisesChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryHumanistChaplains(){
        val queryHumanistChaplain = queryRef.whereArrayContains("field", "Community-Humanist Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    humanistChaplainCount += 1
                }
                if (humanistChaplainCount == 0){
                    community_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_community_chaplain_count.text = "$humanistChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun queryCollegeChaplains(){
        val queryCollegeChaplain = queryRef.whereArrayContains("field", "College-Education Ch.")
            .whereEqualTo("accountStatus", "2").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result = document.toObject()
                    collegeChaplainCount += 1
                }
                if (collegeChaplainCount == 0){
                    college_chaplain_cardView.visibility = View.GONE
                }
                else{
                    textView_college_chaplain_count.text = "$collegeChaplainCount " + getString(R.string.chaplain_count_text)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentPersonalInfo::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        retrieveChaplains()
    }
}