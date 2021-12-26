package com.telechaplaincy.chaplain_reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import com.telechaplaincy.chaplain_profile.ChaplainProfileActivity
import kotlinx.android.synthetic.main.activity_chaplain_reports.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChaplainReportsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var chaplainProfileFieldUserId: String = ""

    private var chaplainSpinnerSelection: String = ""
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
    private var timeRangeSelectionFirstTime: Long = 0
    private var timeRangeSelectionSecondTime: Long = 0

    private var appointmentsInfoModelClassArrayList = ArrayList<AppointmentModelClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_reports)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        if (user != null) {
            chaplainProfileFieldUserId = user.uid
        }
        chaplainReportsFrequencySelectionMethod()
    }

    private fun chaplainReportsFrequencySelectionMethod() {
        val optionsFrequencies = resources.getStringArray(R.array.reports_frequency)
        spinner_chaplain_reports_frequency.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionsFrequencies
        )
        spinner_chaplain_reports_frequency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    chaplainSpinnerSelection = optionsFrequencies[position]
                    //chaplain_reports_date_frequency_textView.text = chaplainSpinnerSelection
                    getFineAndCommissionPercentages()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
    }

    private fun getReports() {
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("appointments")
            .whereGreaterThanOrEqualTo("appointmentDate", timeRangeSelectionFirstTime.toString())
            .whereLessThan("appointmentDate", timeRangeSelectionSecondTime.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    appointmentsInfoModelClassArrayList.add(document.toObject<AppointmentModelClass>())
                }
                totalAppointmentsNumber = appointmentsInfoModelClassArrayList.size.toString()
                chaplain_reports_activity_total_numbers_appointments.text = totalAppointmentsNumber
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
                chaplain_reports_activity_total_income.text = "$totalIncome $"
                chaplain_reports_activity_canceled_appointments_by_patient.text =
                    "$canceledAppointmentsNumberByPatient"
                chaplain_reports_activity_canceled_appointments_by_you.text =
                    "$canceledAppointmentsNumberByYou"
                chaplain_reports_activity_appointment_cancel_fine_total.text =
                    "$fineTotalForCanceledAppointment $"
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun getFineAndCommissionPercentages() {
        commissionForPerAppointment = 0.00
        fineForPerCanceledAppointment = 0.00

        dbSave = db.collection("appointment").document("appointmentInfo")
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                commissionForPerAppointmentPercentage =
                    document["appointmentCommissionPercentage"].toString()
                appointmentCancelationFeePercentage =
                    document["appointmentCancelFinePercentage"].toString()
                priceForPerAppointment = document["appointmentPrice"].toString()

                chaplain_reports_activity_appointment_price.text = "$priceForPerAppointment $"

                commissionForPerAppointment =
                    (priceForPerAppointment.toDouble() * commissionForPerAppointmentPercentage.toInt()) / 100
                chaplain_reports_activity_appointment_commission.text =
                    "$commissionForPerAppointment $"

                fineForPerCanceledAppointment =
                    (priceForPerAppointment.toDouble() * appointmentCancelationFeePercentage.toInt()) / 100
                chaplain_reports_activity_appointment_cancel_fine.text =
                    "$fineForPerCanceledAppointment $"

                Log.d(
                    "Document",
                    "Document: $commissionForPerAppointmentPercentage - $appointmentCancelationFeePercentage"
                )
                reportRangeSelection()
            } else {
                Log.d("TAG", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun reportRangeSelection() {
        totalIncome = 0.00
        totalAppointmentsNumber = ""
        canceledAppointmentsNumberByYou = 0
        canceledAppointmentsNumberByPatient = 0
        fineTotalForCanceledAppointment = 0.00
        appointmentsInfoModelClassArrayList.clear()

        if (chaplainSpinnerSelection == "This Week") {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)

            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

            timeRangeSelectionFirstTime = calendar.timeInMillis
            timeRangeSelectionSecondTime = System.currentTimeMillis()

            val dateFormatLocalZone = SimpleDateFormat("MMM dd, EEE HH:mm")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(timeRangeSelectionFirstTime))
            val timeRangeSecond = dateFormatLocalZone.format(Date(timeRangeSelectionSecondTime))
            chaplain_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        } else if (chaplainSpinnerSelection == "Last Week") {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)

            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

            timeRangeSelectionFirstTime = calendar.timeInMillis - (7 * 24 * 60 * 60 * 1000)
            timeRangeSelectionSecondTime = calendar.timeInMillis - 1000

            val dateFormatLocalZone = SimpleDateFormat("MMM dd, EEE HH:mm")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(timeRangeSelectionFirstTime))
            val timeRangeSecond = dateFormatLocalZone.format(Date(timeRangeSelectionSecondTime))
            chaplain_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        } else if (chaplainSpinnerSelection == "This Month") {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)

            calendar.set(Calendar.DAY_OF_MONTH, 1)

            timeRangeSelectionFirstTime = calendar.timeInMillis
            timeRangeSelectionSecondTime = System.currentTimeMillis()

            val dateFormatLocalZone = SimpleDateFormat("MMM dd, EEE HH:mm")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(timeRangeSelectionFirstTime))
            val timeRangeSecond = dateFormatLocalZone.format(Date(timeRangeSelectionSecondTime))
            chaplain_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        } else if (chaplainSpinnerSelection == "Last Month") {

            //this part is to find first of the this month
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)

            calendar.set(Calendar.DAY_OF_MONTH, 1)

            timeRangeSelectionSecondTime = calendar.timeInMillis - 1000

            //this part is to find first of the last Month
            val calendarLastMonth = Calendar.getInstance()
            calendarLastMonth.add(Calendar.MONTH, -1)
            calendarLastMonth.set(Calendar.DATE, 1)
            calendarLastMonth.set(Calendar.HOUR_OF_DAY, 0)
            calendarLastMonth.clear(Calendar.MINUTE)
            calendarLastMonth.clear(Calendar.SECOND)
            calendarLastMonth.clear(Calendar.MILLISECOND)

            timeRangeSelectionFirstTime = calendarLastMonth.timeInMillis

            val dateFormatLocalZone = SimpleDateFormat("MMM dd, EEE HH:mm")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(timeRangeSelectionFirstTime))
            val timeRangeSecond = dateFormatLocalZone.format(Date(timeRangeSelectionSecondTime))
            chaplain_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        } else if (chaplainSpinnerSelection == "This Year") {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)

            calendar.set(Calendar.DAY_OF_YEAR, 1)

            timeRangeSelectionFirstTime = calendar.timeInMillis
            timeRangeSelectionSecondTime = System.currentTimeMillis()

            val dateFormatLocalZone = SimpleDateFormat("MMM dd yy, EEE HH:mm")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(timeRangeSelectionFirstTime))
            val timeRangeSecond = dateFormatLocalZone.format(Date(timeRangeSelectionSecondTime))
            chaplain_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        } else if (chaplainSpinnerSelection == "Last Year") {

            //this part is to find first of the this month
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)

            calendar.set(Calendar.DAY_OF_YEAR, 1)

            timeRangeSelectionSecondTime = calendar.timeInMillis - 1000

            //this part is to find first of the last Month
            val calendarLastMonth = Calendar.getInstance()
            calendarLastMonth.add(Calendar.YEAR, -1)
            calendarLastMonth.set(Calendar.DATE, 1)
            calendarLastMonth.set(Calendar.DAY_OF_YEAR, 1)
            calendarLastMonth.set(Calendar.HOUR_OF_DAY, 0)
            calendarLastMonth.clear(Calendar.MINUTE)
            calendarLastMonth.clear(Calendar.SECOND)
            calendarLastMonth.clear(Calendar.MILLISECOND)

            timeRangeSelectionFirstTime = calendarLastMonth.timeInMillis

            val dateFormatLocalZone = SimpleDateFormat("MMM dd yy, EEE HH:mm")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(timeRangeSelectionFirstTime))
            val timeRangeSecond = dateFormatLocalZone.format(Date(timeRangeSelectionSecondTime))
            chaplain_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        }

        getReports()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}