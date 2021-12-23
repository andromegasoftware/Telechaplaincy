package com.telechaplaincy.chaplain_bank_account_info

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.birth_date_class.DateMask
import kotlinx.android.synthetic.main.activity_chaplain_bank_account_info_edit.*

class ChaplainBankAccountInfoEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference

    private var chaplainProfileFieldUserId: String = ""
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
        setContentView(R.layout.activity_chaplain_bank_account_info_edit)

        chaplain_bank_account_info_progress_bar.visibility = View.GONE

        //this part makes editText birth date format like dd/mm/yyyy
        DateMask(chaplain_bank_account_info_edit_activity_birth_date).listen()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            chaplainProfileFieldUserId = user.uid
        }

        dbSave = db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("bankAccountInfo").document("chaplainBankAccount")

        chaplain_bank_account_info_edit_activity_save_button.setOnClickListener {
            takeChaplainBankAccountInfoFromEditTexts()
        }
    }

    private fun takeChaplainBankAccountInfoFromEditTexts() {
        chaplainAccountHolderName = chaplain_bank_account_info_edit_activity_name.text.toString()
        chaplainBankAccountRoutingNumber =
            chaplain_bank_account_info_edit_activity_routing_number.text.toString()
        chaplainBankAccountNumber =
            chaplain_bank_account_info_edit_activity_account_number.text.toString()
        chaplainBankAccountNumberAgain =
            chaplain_bank_account_info_edit_activity_account_number_again.text.toString()
        chaplainBankAccountBirthDate =
            chaplain_bank_account_info_edit_activity_birth_date.text.toString()
        chaplainBankAccountAddress =
            chaplain_bank_account_info_edit_activity_address.text.toString()
        chaplainBankAccountCity = chaplain_bank_account_info_edit_activity_city.text.toString()
        chaplainBankAccountState = chaplain_bank_account_info_edit_activity_state.text.toString()
        chaplainBankPostalCode =
            chaplain_bank_account_info_edit_activity_postal_code.text.toString()

        if (chaplainAccountHolderName == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_name_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountRoutingNumber == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_routing_number_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountNumber == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_account_number_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountNumberAgain == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_account_number_again_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountNumberAgain != chaplainBankAccountNumber) {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_account_number_check_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountBirthDate == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_birth_date_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountAddress == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_address_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountCity == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_city_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankAccountState == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_state_toast),
                Toast.LENGTH_LONG
            ).show()
        } else if (chaplainBankPostalCode == "") {
            Toast.makeText(
                this,
                getString(R.string.chaplain_bank_account_info_postal_code_toast),
                Toast.LENGTH_LONG
            ).show()
        } else {
            saveAccountInfoToFireStore()
        }
    }

    private fun saveAccountInfoToFireStore() {
        chaplain_bank_account_info_progress_bar.visibility = View.VISIBLE
        chaplain_bank_account_info_edit_activity_save_button.isClickable = false

        val chaplainAccountInfo = ChaplainBankAccountInfoModelClass(
            chaplainAccountHolderName, chaplainBankAccountRoutingNumber, chaplainBankAccountNumber,
            chaplainBankAccountBirthDate, chaplainBankAccountAddress, chaplainBankAccountCity,
            chaplainBankAccountState, chaplainBankPostalCode
        )

        dbSave.set(chaplainAccountInfo, SetOptions.merge())
            .addOnSuccessListener {
                chaplain_bank_account_info_progress_bar.visibility = View.GONE
                chaplain_bank_account_info_edit_activity_save_button.isClickable = true
                chaplain_bank_account_info_edit_activity_save_button.text =
                    getString(R.string.chaplain_bank_account_info_edit_save_button_after_click)
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

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
        chaplain_bank_account_info_edit_activity_name.setText(chaplainAccountHolderName)
        chaplain_bank_account_info_edit_activity_routing_number.setText(
            chaplainBankAccountRoutingNumber
        )
        chaplain_bank_account_info_edit_activity_account_number.setText(chaplainBankAccountNumber)
        chaplain_bank_account_info_edit_activity_account_number_again.setText(
            chaplainBankAccountNumber
        )
        chaplain_bank_account_info_edit_activity_birth_date.setText(chaplainBankAccountBirthDate)
        chaplain_bank_account_info_edit_activity_address.setText(chaplainBankAccountAddress)
        chaplain_bank_account_info_edit_activity_city.setText(chaplainBankAccountCity)
        chaplain_bank_account_info_edit_activity_state.setText(chaplainBankAccountState)
        chaplain_bank_account_info_edit_activity_postal_code.setText(chaplainBankPostalCode)
    }

    override fun onStart() {
        super.onStart()
        readChaplainBankAccountInfoFromFireStore()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainBankAccountInfoActivity::class.java)
        startActivity(intent)
        finish()
    }
}