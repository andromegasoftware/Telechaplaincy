package com.telechaplaincy.chaplain_bank_account_info

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.all_chaplains_wait_payment.ChaplainPaymentReportActivity
import com.telechaplaincy.chaplain_profile.ChaplainProfileActivity
import kotlinx.android.synthetic.main.activity_chaplain_bank_account_info.*

class ChaplainBankAccountInfoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference

    private var chaplainProfileFieldUserId: String = ""
    private var chaplainUserIdSentByAdmin: String = ""
    private var chaplainAccountHolderName: String = ""
    private var chaplainBankAccountRoutingNumber: String = ""
    private var chaplainBankAccountNumber: String = ""
    private var chaplainBankAccountNumberAgain: String = ""
    private var chaplainBankAccountBirthDate: String = ""
    private var chaplainBankAccountAddress: String = ""
    private var chaplainBankAccountCity: String = ""
    private var chaplainBankAccountState: String = ""
    private var chaplainBankPostalCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_bank_account_info)

        chaplainUserIdSentByAdmin = intent.getStringExtra("chaplain_id_send_by_admin").toString()

        auth = FirebaseAuth.getInstance()

        if (chaplainUserIdSentByAdmin != "null") {
            chaplainProfileFieldUserId = chaplainUserIdSentByAdmin
            chaplain_bank_account_info_activity_edit_imageButton.visibility = View.GONE
        } else {
            if (auth.currentUser != null) {
                user = auth.currentUser!!
                chaplainProfileFieldUserId = user.uid
            }
        }

        dbSave = db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("bankAccountInfo").document("chaplainBankAccount")

        chaplain_bank_account_info_activity_edit_imageButton.setOnClickListener {
            val intent = Intent(this, ChaplainBankAccountInfoEditActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun readChaplainBankAccountInfoFromFireStore() {
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val chaplainAccountInfo = document.toObject<ChaplainBankAccountInfoModelClass>()
                    if (chaplainAccountInfo != null) {
                        if (chaplainAccountInfo.chaplainAccountHolderName != null) {
                            chaplainAccountHolderName =
                                chaplainAccountInfo.chaplainAccountHolderName.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankAccountRoutingNumber != null) {
                            chaplainBankAccountRoutingNumber =
                                chaplainAccountInfo.chaplainBankAccountRoutingNumber.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankAccountNumber != null) {
                            chaplainBankAccountNumber =
                                chaplainAccountInfo.chaplainBankAccountNumber.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankAccountBirthDate != null) {
                            chaplainBankAccountBirthDate =
                                chaplainAccountInfo.chaplainBankAccountBirthDate.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankAccountAddress != null) {
                            chaplainBankAccountAddress =
                                chaplainAccountInfo.chaplainBankAccountAddress.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankAccountCity != null) {
                            chaplainBankAccountCity =
                                chaplainAccountInfo.chaplainBankAccountCity.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankAccountState != null) {
                            chaplainBankAccountState =
                                chaplainAccountInfo.chaplainBankAccountState.toString()
                        }
                        if (chaplainAccountInfo.chaplainBankPostalCode != null) {
                            chaplainBankPostalCode =
                                chaplainAccountInfo.chaplainBankPostalCode.toString()
                        }
                        writeChaplainBankAccountInfoToEditText()
                    }
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun writeChaplainBankAccountInfoToEditText() {
        chaplain_bank_account_info_account_holder_name.text = chaplainAccountHolderName
        chaplain_bank_account_info_routing_number.text = chaplainBankAccountRoutingNumber
        chaplain_bank_account_info_account_number.text = chaplainBankAccountNumber
        chaplain_bank_account_info_birth_date.text = chaplainBankAccountBirthDate
        chaplain_bank_account_info_address.text = chaplainBankAccountAddress
        chaplain_bank_account_info_city.text = chaplainBankAccountCity
        chaplain_bank_account_info_state.text = chaplainBankAccountState
        chaplain_bank_account_info_postal_code.text = chaplainBankPostalCode
    }

    override fun onStart() {
        super.onStart()
        readChaplainBankAccountInfoFromFireStore()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (chaplainUserIdSentByAdmin != "null") {
            val intent = Intent(this, ChaplainPaymentReportActivity::class.java)
            intent.putExtra("chaplain_id_send_by_admin", chaplainProfileFieldUserId)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, ChaplainProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}