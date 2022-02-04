package com.telechaplaincy.all_chaplains_wait_payment

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
import com.telechaplaincy.appointment.AppointmentModelClass
import com.telechaplaincy.chaplain_bank_account_info.ChaplainBankAccountInfoActivity
import com.telechaplaincy.cloud_message.FcmNotificationsSender
import com.telechaplaincy.mail_and_message_send_package.MailSendClass
import com.telechaplaincy.notification_page.NotificationModelClass
import kotlinx.android.synthetic.main.activity_chaplain_payment_report.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private var lastPayment: String = ""
    private var lastPaymentDate: Long = 0
    private var canceledAppointmentsNumberByYou: Int = 0
    private var canceledAppointmentsNumberByPatient: Int = 0
    private var fineForPerCanceledAppointment: Double = 0.00
    private var fineTotalForCanceledAppointment: Double = 0.00
    private var timeNow: Long = 0

    private var appointmentsInfoModelClassArrayList = ArrayList<AppointmentModelClass>()
    private var paymentsInfoModelClassArrayList = ArrayList<ChaplainPaymentInfoModelClass>()
    private var paymentsDateArrayList = ArrayList<Long>()

    private var chaplainMail = ""
    private var chaplainPhoneNumber = ""
    private var notificationTokenId: String = ""
    private var title: String = ""
    private var body: String = ""

    private var mailSendClass = MailSendClass()

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
        getLastPaymentInfoForChaplain()

        chaplain_see_bank_info_button.setOnClickListener {
            val intent = Intent(this, ChaplainBankAccountInfoActivity::class.java)
            intent.putExtra("chaplain_id_send_by_admin", chaplainProfileFieldUserId)
            startActivity(intent)
            finish()
        }

        chaplain_mark_as_paid_button.setOnClickListener {
            markAppointmentsPaidOut()
            sendMessageToChaplainWhenPaymentIsDone()
        }
    }

    private fun sendMessageToChaplainWhenPaymentIsDone() {
        db.collection("chaplains").document(chaplainProfileFieldUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    notificationTokenId = document["notificationTokenId"].toString()
                    chaplainMail = document["email"].toString()
                    chaplainPhoneNumber = document["phone"].toString()
                    title = getString(R.string.chaplain_payment_is_done_notification_title)
                    body = getString(R.string.chaplain_payment_is_done_notification_body)

                    val sender = FcmNotificationsSender(
                        notificationTokenId,
                        title,
                        body,
                        applicationContext,
                        this
                    )
                    sender.SendNotifications()
                    saveMessageToInbox()
                    if (chaplainPhoneNumber != "") {
                        mailSendClass.textMessageSendMethod(chaplainPhoneNumber, body)
                    }

                    mailSendClass.sendMailToChaplain(title, body, chaplainMail)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun saveMessageToInbox() {
        val dbSave = db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("inbox").document()

        val dateSent = System.currentTimeMillis().toString()
        val messageId = dbSave.id

        val message = NotificationModelClass(
            title,
            body,
            null,
            null,
            adminUserId,
            dateSent,
            messageId,
            false
        )

        dbSave.set(message, SetOptions.merge())
    }

    private fun getLastPaymentInfoForChaplain() {
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("payments")
            .whereLessThan("paymentDate", timeNow.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    paymentsInfoModelClassArrayList.add(document.toObject<ChaplainPaymentInfoModelClass>())
                }
                for (k in paymentsInfoModelClassArrayList) {
                    k.paymentDate?.let { paymentsDateArrayList.add(it.toLong()) }
                }
                val biggestDate = paymentsDateArrayList.maxOrNull()
                for (k in paymentsInfoModelClassArrayList) {
                    if (k.paymentDate?.toLong() ?: biggestDate == biggestDate) {
                        lastPayment = k.paymentAmount.toString()
                        lastPaymentDate = k.paymentDate?.toLong() ?: lastPaymentDate
                        chaplain_payment_activity_last_payment.text = "$lastPayment $"

                        val date = Date(lastPaymentDate)
                        val format = SimpleDateFormat("dd.MMM.yyyy HH:mm")
                        val dateFormatted = format.format(date)
                        chaplain_payment_activity_last_payment_date.text = dateFormatted
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun savePaymentInfo() {
        val data = hashMapOf(
            "payerUid" to adminUserId,
            "paymentAmount" to totalIncome.toString(),
            "paymentDate" to timeNow.toString(),
            "chaplainUid" to chaplainProfileFieldUserId
        )
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("payments").document()
            .set(data, SetOptions.merge())
            .addOnSuccessListener {

                db.collection("admins").document(adminUserId)
                    .collection("payments").document()
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        markChaplainPaymentWaitFalse()
                    }

            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

    }

    private fun markAppointmentsPaidOut() {
        chaplain_payment_page_progress_bar.visibility = View.VISIBLE
        chaplain_mark_as_paid_button.isClickable = false
        for (k in appointmentsInfoModelClassArrayList) {
            val documentId = k.appointmentId
            if (documentId != null) {
                db.collection("chaplains").document(chaplainProfileFieldUserId)
                    .collection("appointments").document(documentId)
                    .update("isAppointmentPricePaidOutToChaplain", true)
                    .addOnSuccessListener {
                        if (k == appointmentsInfoModelClassArrayList.last()) {
                            savePaymentInfo()
                        }
                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
            }
        }
    }

    private fun markChaplainPaymentWaitFalse() {
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .update("isPaymentWait", false)
            .addOnSuccessListener {
                chaplain_payment_page_progress_bar.visibility = View.GONE
                chaplain_mark_as_paid_button.isClickable = true
                Toast.makeText(
                    this,
                    getString(R.string.chaplain_mark_as_paid_toast_message),
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this, AllChaplainsWaitPaymentActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
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


                getReports()

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