package com.telechaplaincy.fees_and_commissions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.admin.AdminMainActivity
import kotlinx.android.synthetic.main.activity_fees_and_commisions.*
import java.util.concurrent.TimeUnit

class FeesAndCommissionsActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var appointmentPrice: String = ""
    private var appointmentCancelFinePercentage: String = ""
    private var appointmentCancelLastDateForChaplain: String = ""
    private var appointmentCancelLastDateForPatient: String = ""
    private var appointmentCommissionPercentage: String = ""
    private var appointmentEditLastDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fees_and_commisions)

        fees_and_commissions_activity_save_button.setOnClickListener {
            SaveData()
        }
    }

    private fun SaveData() {

        fees_and_commissions_activity_save_button.isClickable = false
        fees_and_commissions_activity_progress_bar.visibility = View.VISIBLE

        appointmentPrice = fees_and_commissions_activity_price.text.toString()
        appointmentCancelFinePercentage = fees_and_commissions_activity_cancel_fine.text.toString()
        appointmentCommissionPercentage = fees_and_commissions_activity_commission.text.toString()
        appointmentCancelLastDateForChaplain =
            (((fees_and_commissions_activity_cancel_last_date_chaplain.text.toString()).toLong()) * 3600000).toString()
        appointmentCancelLastDateForPatient =
            (((fees_and_commissions_activity_cancel_last_date_patient.text.toString()).toLong()) * 3600000).toString()
        appointmentEditLastDate =
            (((fees_and_commissions_activity_edit_last_date.text.toString()).toLong()) * 3600000).toString()
        db.collection("appointment").document("appointmentInfo")
            .update(
                "appointmentPrice", appointmentPrice,
                "appointmentCancelFinePercentage", appointmentCancelFinePercentage,
                "appointmentCancelLastDateForChaplain", appointmentCancelLastDateForChaplain,
                "appointmentCancelLastDateForPatient", appointmentCancelLastDateForPatient,
                "appointmentCommissionPercentage", appointmentCommissionPercentage,
                "appointmentEditLastDate", appointmentEditLastDate
            )
            .addOnSuccessListener {

                fees_and_commissions_activity_save_button.isClickable = true
                fees_and_commissions_activity_progress_bar.visibility = View.GONE
                fees_and_commissions_activity_save_button.text =
                    getString(R.string.chaplain_bank_account_info_edit_save_button_after_click)
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
    }

    private fun readData() {
        db.collection("appointment").document("appointmentInfo")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    appointmentPrice = document["appointmentPrice"].toString()
                    appointmentCancelFinePercentage =
                        document["appointmentCancelFinePercentage"].toString()
                    appointmentCancelLastDateForChaplain =
                        document["appointmentCancelLastDateForChaplain"].toString()
                    appointmentCancelLastDateForPatient =
                        document["appointmentCancelLastDateForPatient"].toString()
                    appointmentCommissionPercentage =
                        document["appointmentCommissionPercentage"].toString()
                    appointmentEditLastDate = document["appointmentEditLastDate"].toString()

                    fees_and_commissions_activity_price.setText(appointmentPrice)
                    fees_and_commissions_activity_commission.setText(appointmentCommissionPercentage)
                    fees_and_commissions_activity_cancel_fine.setText(
                        appointmentCancelFinePercentage
                    )
                    val appCancelLastHourPatient =
                        TimeUnit.MILLISECONDS.toHours(appointmentCancelLastDateForPatient.toLong())
                            .toString()
                    fees_and_commissions_activity_cancel_last_date_patient.setText(
                        appCancelLastHourPatient
                    )
                    val appCancelLastHourChaplain =
                        TimeUnit.MILLISECONDS.toHours(appointmentCancelLastDateForChaplain.toLong())
                            .toString()
                    fees_and_commissions_activity_cancel_last_date_chaplain.setText(
                        appCancelLastHourChaplain
                    )
                    val appCancelLastHourEditPatient =
                        TimeUnit.MILLISECONDS.toHours(appointmentEditLastDate.toLong()).toString()
                    fees_and_commissions_activity_edit_last_date.setText(
                        appCancelLastHourEditPatient
                    )
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onStart() {
        super.onStart()
        readData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}