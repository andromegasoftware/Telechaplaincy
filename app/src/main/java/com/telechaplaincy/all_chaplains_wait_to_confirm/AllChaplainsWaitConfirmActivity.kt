package com.telechaplaincy.all_chaplains_wait_to_confirm

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
import com.telechaplaincy.chaplain_profile.ChaplainProfileDetailsActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import kotlinx.android.synthetic.main.activity_all_chaplains_wait_confirm.*

class AllChaplainsWaitConfirmActivity : AppCompatActivity() {

    private var adminUid: String = ""
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: CollectionReference

    private var chaplainCollectionName: String = ""
    private var allChaplainsListArray = ArrayList<ChaplainUserProfile>()
    private lateinit var allChaplainsListedAdapterClass: AllChaplainsWaitConfirmAdapterClass
    private var queryWord: String = ""
    private var queryChaplainsArrayList = ArrayList<ChaplainUserProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chaplains_wait_confirm)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        adminUid = user.uid

        chaplainCollectionName = getString(R.string.chaplain_collection)

        queryRef = db.collection(chaplainCollectionName)

        allChaplainsListedAdapterClass =
            AllChaplainsWaitConfirmAdapterClass(allChaplainsListArray) { chaplainListModelClassItem: ChaplainUserProfile ->
                chaplainsListClickListener(chaplainListModelClassItem)
            }

        allChaplainsListMethod()

        // chaplain lists recyclerview
        val chaplainListsLayoutManager = LinearLayoutManager(this)
        chaplainListsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        all_chaplains_wait_confirm_recyclerView.layoutManager = chaplainListsLayoutManager
        all_chaplains_wait_confirm_recyclerView.adapter = allChaplainsListedAdapterClass
    }

    private fun chaplainsListClickListener(chaplainUserProfile: ChaplainUserProfile) {
        val intent = Intent(this, ChaplainProfileDetailsActivity::class.java)
        intent.putExtra("chaplain_id_send_by_admin", chaplainUserProfile.userId)
        intent.putExtra("activity_open_code", "confirm")
        startActivity(intent)
        finish()
    }

    private fun allChaplainsListMethod() {
        val queryHumanistChaplain =
            queryRef.whereEqualTo("accountStatus", "1")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        allChaplainsListArray.add(document.toObject())
                    }
                    allChaplainsListedAdapterClass.submitList(allChaplainsListArray)
                    allChaplainsListedAdapterClass.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("deneme", "Error getting documents: ", exception)
                }

        val searchView = findViewById<SearchView>(R.id.searchView_all_chaplains_wait_confirm)
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
                    queryChaplainsArrayList.clear()
                    for (k in 0 until allChaplainsListArray.size) {
                        if (allChaplainsListArray[k].name?.toLowerCase()
                                ?.contains(queryWord.toLowerCase()) == true
                        ) {
                            queryChaplainsArrayList.add(allChaplainsListArray[k])
                        }
                    }
                    allChaplainsListedAdapterClass.updateList(queryChaplainsArrayList)
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