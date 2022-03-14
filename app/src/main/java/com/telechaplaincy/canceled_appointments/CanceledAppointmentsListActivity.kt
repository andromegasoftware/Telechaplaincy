package com.telechaplaincy.canceled_appointments

import android.content.Intent
import android.os.Bundle
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
import com.telechaplaincy.appointment.AppointmentModelClass
import kotlinx.android.synthetic.main.activity_canceled_appointments_list.*

class CanceledAppointmentsListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var queryRef: CollectionReference

    private var canceledAppointmentsListArray = ArrayList<AppointmentModelClass>()
    private var canceledAppointmentsListArrayForLoop =
        ArrayList<AppointmentModelClass>() //I will use this array in for loop
    private lateinit var canceledAppointmentsListedAdapterClass: CanceledAppointmentsListedAdapterClass
    private var queryWord: String = ""
    private var queryCanceledAppointmentsArrayList = ArrayList<AppointmentModelClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canceled_appointments_list)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        queryRef = db.collection("appointment")

        canceledAppointmentsListedAdapterClass =
            CanceledAppointmentsListedAdapterClass(canceledAppointmentsListArray)
            { canceledAppointmentsListModelClassItem: AppointmentModelClass ->
                canceledAppointmentsListClickListener(canceledAppointmentsListModelClassItem)
            }

        canceledAppointmentsListMethod()

        // canceled appointments lists recyclerview
        val canceledAppointmentsListsLayoutManager = LinearLayoutManager(this)
        canceledAppointmentsListsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        canceled_appointments_RecyclerView.layoutManager = canceledAppointmentsListsLayoutManager
        canceled_appointments_RecyclerView.adapter = canceledAppointmentsListedAdapterClass
    }

    private fun canceledAppointmentsListMethod() {

        //this part will give us all of the appointments that have not repaid back to the customer
        queryRef.whereEqualTo("appointmentFeeRepaid", false)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    canceledAppointmentsListArray.add(document.toObject())
                }
                //this part will give us that the canceled appointments by patients and chaplains
                for (item in canceledAppointmentsListArray) {
                    if (item.appointmentStatus.equals("canceled by Chaplain")
                        || item.appointmentStatus.equals("canceled by patient")
                    ) {
                        canceledAppointmentsListArrayForLoop.add(item)
                    }
                }
                //in this part,  we added all the canceled appointments into the recyclerview
                canceledAppointmentsListedAdapterClass.submitList(
                    canceledAppointmentsListArrayForLoop
                )
                canceledAppointmentsListedAdapterClass.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                //Log.w("TAG", "Error getting documents: ", exception)
            }

        val searchView = findViewById<SearchView>(R.id.searchView_canceled_appointments)
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
                    queryCanceledAppointmentsArrayList.clear()
                    for (k in 0 until canceledAppointmentsListArray.size) {
                        if (canceledAppointmentsListArray[k].patientName?.toLowerCase()
                                ?.contains(queryWord.toLowerCase()) == true
                        ) {
                            queryCanceledAppointmentsArrayList.add(canceledAppointmentsListArray[k])
                        }
                    }
                    canceledAppointmentsListedAdapterClass.updateList(
                        queryCanceledAppointmentsArrayList
                    )
                }
                return false
            }
        })
    }

    private fun canceledAppointmentsListClickListener(appointmentModelClass: AppointmentModelClass) {
        val intent = Intent(this, CanceledAppointmentDetailsActivity::class.java)
        intent.putExtra("appointmentId", appointmentModelClass.appointmentId)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}