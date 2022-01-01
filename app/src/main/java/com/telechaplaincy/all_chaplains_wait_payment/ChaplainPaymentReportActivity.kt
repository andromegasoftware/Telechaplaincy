package com.telechaplaincy.all_chaplains_wait_payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import kotlinx.android.synthetic.main.activity_chaplain_payment_report.*

class ChaplainPaymentReportActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var chaplainProfileFieldUserId: String = ""
    private var adminUserId: String = ""

    private var totalIncome: Double = 0.00
    private var totalAppointmentsNumber: String = ""
    private var priceForPerAppointment: String = ""
    private var commissionForPerAppointment: Double = 0.00
    private var commissionForPerAppointmentPercentage: String = ""
    private var appointmentCancelationFeePercentage: String = ""
    private var canceledAppointmentsNumberByYou: Int = 0
    private var canceledAppointmentsNumberByPatient: Int = 0
    private var fineForPerCanceledAppointment: Double = 0.00
    private var fineTotalForCanceledAppointment: Double = 0.00
    private var timeNow: Long = 0

    private var appointmentsInfoModelClassArrayList = ArrayList<AppointmentModelClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_payment_report)

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id_send_by_admin").toString()
        timeNow = System.currentTimeMillis()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        if (user != null) {
            adminUserId = user.uid
        }

        getFineAndCommissionPercentages()
        getReports()
    }

    private fun getReports() {
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("appointments")
            .whereLessThan("appointmentDate", timeNow.toString())
            .whereEqualTo("isAppointmentPricePaidOutToChaplain", false)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    appointmentsInfoModelClassArrayList.add(document.toObject<AppointmentModelClass>())
                    //Log.d("admin_data", document.toObject<AppointmentModelClass>().toString())
                }
                totalAppointmentsNumber = appointmentsInfoModelClassArrayList.size.toString()
                chaplain_payment_activity_total_numbers_appointments.text = totalAppointmentsNumber

                for (k in 0 until appointmentsInfoModelClassArrayList.size) {
                    Log.d(
                        "model_class",
                        "${appointmentsInfoModelClassArrayList[k].appointmentPrice} - ${appointmentsInfoModelClassArrayList[k].appointmentStatus}"
                    )

                    if (appointmentsInfoModelClassArrayList[k].appointmentStatus != "canceled by patient"
                        && appointmentsInfoModelClassArrayList[k].appointmentStatus != "canceled by Chaplain"
                    ) {

                        totalIncome += appointmentsInfoModelClassArrayList[k].appointmentPrice?.toDouble()
                            ?: totalIncome
                        commissionForPerAppointment =
                            (appointmentsInfoModelClassArrayList[k].appointmentPrice?.toDouble()
                                ?.times(commissionForPerAppointmentPercentage.toInt()))?.div(100)
                                ?: commissionForPerAppointment
                        totalIncome -= commissionForPerAppointment
                    }

                    if (appointmentsInfoModelClassArrayList[k].appointmentStatus == "canceled by patient") {
                        canceledAppointmentsNumberByPatient += 1
                    }

                    if (appointmentsInfoModelClassArrayList[k].appointmentStatus == "canceled by Chaplain") {
                        canceledAppointmentsNumberByYou += 1
                        val appPrice =
                            (appointmentsInfoModelClassArrayList[k].appointmentPrice)?.toDouble()
                        val fineForAppCancel =
                            (appPrice?.times(appointmentCancelationFeePercentage.toInt()))?.div(
                                100
                            )
                        if (fineForAppCancel != null) {
                            fineTotalForCanceledAppointment += fineForAppCancel
                        }
                    }
                }
                totalIncome -= fineTotalForCanceledAppointment
                chaplain_payment_activity_total_income.text = "$totalIncome $"
                //chaplain_reports_activity_canceled_appointments_by_patient.text = "$canceledAppointmentsNumberByPatient"
                chaplain_payment_activity_canceled_appointments_by_chaplain.text =
                    "$canceledAppointmentsNumberByYou"
                chaplain_payment_activity_appointment_cancel_fine_total.text =
                    "$fineTotalForCanceledAppointment $"
            }
            .addOnFailureListener { exception ->
                Log.w("admin_data", "Error getting documents: ", exception)
            }
    }

    private fun getFineAndCommissionPercentages() {

        dbSave = db.collection("appointment").document("appointmentInfo")
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                commissionForPerAppointmentPercentage =
                    document["appointmentCommissionPercentage"].toString()
                appointmentCancelationFeePercentage =
                    document["appointmentCancelFinePercentage"].toString()
                priceForPerAppointment = document["appointmentPrice"].toString()

                chaplain_payment_activity_appointment_price.text = "$priceForPerAppointment $"

                commissionForPerAppointment =
                    (priceForPerAppointment.toDouble() * commissionForPerAppointmentPercentage.toInt()) / 100
                chaplain_payment_activity_appointment_commission.text =
                    "$commissionForPerAppointment $"

                fineForPerCanceledAppointment =
                    (priceForPerAppointment.toDouble() * appointmentCancelationFeePercentage.toInt()) / 100
                chaplain_payment_activity_appointment_cancel_fine.text =
                    "$fineForPerCanceledAppointment $"

                Log.d(
                    "Document",
                    "Document: $commissionForPerAppointmentPercentage - $appointmentCancelationFeePercentage"
                )
            } else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AllChaplainsWaitPaymentActivity::class.java)
        startActivity(intent)
        finish()
    }
}