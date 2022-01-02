package com.telechaplaincy.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.MainEntry
import com.telechaplaincy.R
import com.telechaplaincy.all_chaplains.AllChaplainsListActivity
import com.telechaplaincy.all_chaplains_wait_payment.AllChaplainsWaitPaymentActivity
import com.telechaplaincy.all_chaplains_wait_to_confirm.AllChaplainsWaitConfirmActivity
import com.telechaplaincy.all_patients.AllPatientsListActivity
import com.telechaplaincy.fees_and_commissions.FeesAndCommissionsActivity
import kotlinx.android.synthetic.main.activity_admin_main.*

class AdminMainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var queryRef: CollectionReference
    private var adminUserId: String = ""
    private var totalChaplainsCount: String = ""
    private var totalPatientsCount: String = ""
    private var totalChaplainsWaitConfirmCount: String = ""
    private var totalChaplainsWaitPaymentCount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        auth = FirebaseAuth.getInstance()
        adminUserId = auth.currentUser?.uid ?: adminUserId

        getAllChaplainsCount()
        getAllPatientsCount()
        getChaplainsWaitConfirmCount()
        getChaplainsWaitPaymentCount()

        all_chaplains_cardView.setOnClickListener {
            val intent = Intent(this, AllChaplainsListActivity::class.java)
            startActivity(intent)
            finish()
        }
        all_patients_cardView.setOnClickListener {
            val intent = Intent(this, AllPatientsListActivity::class.java)
            startActivity(intent)
            finish()
        }
        chaplains_wait_confirm_cardView.setOnClickListener {
            val intent = Intent(this, AllChaplainsWaitConfirmActivity::class.java)
            startActivity(intent)
            finish()
        }
        chaplain_wait_pay_cardView.setOnClickListener {
            val intent = Intent(this, AllChaplainsWaitPaymentActivity::class.java)
            startActivity(intent)
            finish()
        }
        send_message_cardView.setOnClickListener {

        }
        financial_reports_cardView.setOnClickListener {

        }
        fees_commissions_cardView.setOnClickListener {
            val intent = Intent(this, FeesAndCommissionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        sign_out_cardView.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainEntry::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getChaplainsWaitPaymentCount() {
        queryRef = db.collection("chaplains")
        queryRef
            .whereEqualTo("isPaymentWait", true)
            .get()
            .addOnSuccessListener { documents ->
                totalChaplainsWaitPaymentCount = documents.size().toString()
                chaplains_wait_pay_textView.text = getString(
                    R.string.admin_main_activity_total_text,
                    totalChaplainsWaitPaymentCount
                )
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun getChaplainsWaitConfirmCount() {
        queryRef = db.collection("chaplains")
        queryRef
            .whereEqualTo("accountStatus", "1")
            .get()
            .addOnSuccessListener { documents ->
                totalChaplainsWaitConfirmCount = documents.size().toString()
                chaplains_wait_confirm_textView.text = getString(
                    R.string.admin_main_activity_total_text,
                    totalChaplainsWaitConfirmCount
                )
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun getAllPatientsCount() {
        queryRef = db.collection("patients")
        queryRef.get()
            .addOnSuccessListener { documents ->
                totalPatientsCount = documents.size().toString()
                all_patients_count_textView.text =
                    getString(R.string.admin_main_activity_total_text, totalPatientsCount)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun getAllChaplainsCount() {
        queryRef = db.collection("chaplains")
        queryRef.get()
            .addOnSuccessListener { documents ->
                totalChaplainsCount = documents.size().toString()
                all_chaplains_count_textView.text =
                    getString(R.string.admin_main_activity_total_text, totalChaplainsCount)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }
}