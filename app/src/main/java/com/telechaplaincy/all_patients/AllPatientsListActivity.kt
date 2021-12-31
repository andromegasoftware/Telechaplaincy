package com.telechaplaincy.all_patients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.admin.AdminMainActivity
import com.telechaplaincy.patient_profile.PatientProfileDetailsActivity
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_all_patients_list.*

class AllPatientsListActivity : AppCompatActivity() {

    private var adminUid: String = ""
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: CollectionReference

    private var patientCollectionName: String = ""
    private var allPatientsListArray = ArrayList<UserProfile>()
    private lateinit var allPatientsListedAdapterClass: AllPatientsListedAdapterClass
    private var queryWord: String = ""
    private var queryPatientsArrayList = ArrayList<UserProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_patients_list)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        adminUid = user.uid

        patientCollectionName = getString(R.string.patient_collection)

        queryRef = db.collection(patientCollectionName)

        allPatientsListedAdapterClass =
            AllPatientsListedAdapterClass(allPatientsListArray) { patientListModelClassItem: UserProfile ->
                patientsListClickListener(patientListModelClassItem)
            }

        allPatientsListMethod()

        // patients list recyclerview
        val patientListsLayoutManager = LinearLayoutManager(this)
        patientListsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        allPatientsRecyclerView.layoutManager = patientListsLayoutManager
        allPatientsRecyclerView.adapter = allPatientsListedAdapterClass
    }

    private fun patientsListClickListener(patientUserProfile: UserProfile) {
        val intent = Intent(this, PatientProfileDetailsActivity::class.java)
        intent.putExtra("chaplain_id_send_by_admin", patientUserProfile.userId)
        startActivity(intent)
        finish()
    }

    private fun allPatientsListMethod() {
        val queryHumanistChaplain =
            queryRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        allPatientsListArray.add(document.toObject())
                    }
                    allPatientsListedAdapterClass.submitList(allPatientsListArray)
                    allPatientsListedAdapterClass.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("deneme", "Error getting documents: ", exception)
                }

        val searchView = findViewById<SearchView>(R.id.searchView_All_Patients)
        searchView.isActivated = true
        searchView.isSelected = true

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    queryWord = p0
                    queryPatientsArrayList.clear()
                    for (k in 0 until allPatientsListArray.size) {
                        if (allPatientsListArray[k].name?.toLowerCase()
                                ?.contains(queryWord.toLowerCase()) == true
                        ) {
                            queryPatientsArrayList.add(allPatientsListArray[k])
                        }
                    }
                    allPatientsListedAdapterClass.updateList(queryPatientsArrayList)
                }
                return false
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}