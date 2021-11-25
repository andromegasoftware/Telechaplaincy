package com.telechaplaincy.chaplain_selection.college_chaplains_selection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient.CategorySelection
import kotlinx.android.synthetic.main.activity_college_chaplains_selection.*

class ChaplainsListedSelectionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: CollectionReference

    private var chaplainCollectionName: String = ""
    private var collegeChaplainsListArray = ArrayList<ChaplainUserProfile>()
    private lateinit var chaplainsListedAdapterClass: ChaplainsListedAdapterClass

    private var chaplainCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_college_chaplains_selection)

        chaplainCategory = intent.getStringExtra("chaplain_category").toString()
        Log.d("deneme_", chaplainCategory)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        val userUid = user.uid

        chaplainCollectionName = getString(R.string.chaplain_collection)

        queryRef = db.collection(chaplainCollectionName)

        chaplainsListedAdapterClass =
            ChaplainsListedAdapterClass(collegeChaplainsListArray) { chaplainListModelClassItem: ChaplainUserProfile ->
                chaplainsListClickListener(chaplainListModelClassItem)
            }

        queryCollegeChaplains()

        // chaplain lists recyclerview
        val chaplainListsLayoutManager = LinearLayoutManager(this)
        chaplainListsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        collegeChaplainsRecyclerView.layoutManager = chaplainListsLayoutManager
        collegeChaplainsRecyclerView.adapter = chaplainsListedAdapterClass

    }

    private fun chaplainsListClickListener(chaplainUserProfile: ChaplainUserProfile) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CategorySelection::class.java)
        startActivity(intent)
        finish()
    }

    private fun queryCollegeChaplains() {
        if (chaplainCategory == "CollegeChaplains") {
            collegeChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_college)
        } else if (chaplainCategory == "HumanistChaplains") {
            humanistChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_community)

        }else if (chaplainCategory == "CrisisChaplains") {
            crisisChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_crisis)

        }else if (chaplainCategory == "HealthChaplains") {
            healthChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_health)

        }else if (chaplainCategory == "PalliativeChaplains") {
            palliativeChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_pallative)

        }else if (chaplainCategory == "PediatricChaplains") {
            pediatricChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_pediatric)

        }else if (chaplainCategory == "MentalChaplains") {
            mentalChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_mental)

        }else if (chaplainCategory == "HospiceChaplains") {
            hospiceChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_hospice)

        }else if (chaplainCategory == "LawChaplains") {
            lawChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_law)

        }else if (chaplainCategory == "MilitaryChaplains") {
            militaryChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_military)

        }else if (chaplainCategory == "PrisonChaplains") {
            prisonChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_prison)

        }else if (chaplainCategory == "CorporateChaplains") {
            corporateChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_corporate)

        }else if (chaplainCategory == "PetChaplains") {
            petChaplains()
            textViewChaplainsListTitle.text = getString(R.string.chaplains_title_pet)

        }
    }

    private fun humanistChaplains() {
        val queryHumanistChaplain =
            queryRef.whereArrayContains("field", "Community-Humanist Ch.").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        collegeChaplainsListArray.add(document.toObject())
                    }
                    chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                    chaplainsListedAdapterClass.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("deneme", "Error getting documents: ", exception)
                }
    }

    private fun collegeChaplains() {
        val queryCollegeChaplain =
            queryRef.whereArrayContains("field", "College-Education Ch.").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        collegeChaplainsListArray.add(document.toObject())
                    }
                    chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                    chaplainsListedAdapterClass.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("deneme", "Error getting documents: ", exception)
                }
    }

    private fun petChaplains(){
        val queryPetChaplain = queryRef.whereArrayContains("field", "Pet Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun corporateChaplains(){
        val queryCorporateChaplain = queryRef.whereArrayContains("field", "Corporate Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun prisonChaplains(){
        val queryPrisonChaplain = queryRef.whereArrayContains("field", "Prison-Correctional Facilities Ch").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun militaryChaplains(){
        val queryMilitaryChaplain = queryRef.whereArrayContains("field", "Military Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun lawChaplains(){
        val queryLawChaplain = queryRef.whereArrayContains("field", "Law Enforcement-Police Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun hospiceChaplains(){
        val queryHospiceChaplain = queryRef.whereArrayContains("field", "Hospice Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun mentalChaplains(){
        val queryMentalChaplain = queryRef.whereArrayContains("field", "Mental Health Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun pediatricChaplains(){
        val queryPediatricChaplain = queryRef.whereArrayContains("field", "Pediatric Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun palliativeChaplains(){
        val queryPalliativeChaplain = queryRef.whereArrayContains("field", "Palliative Care Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun healthChaplains(){
        val queryHealthChaplain = queryRef.whereArrayContains("field", "Health Care Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    private fun crisisChaplains(){
        val queryCrisisChaplain = queryRef.whereArrayContains("field", "Crisis-Disaster Ch.").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    collegeChaplainsListArray.add(document.toObject())
                }
                chaplainsListedAdapterClass.submitList(collegeChaplainsListArray)
                chaplainsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }
}