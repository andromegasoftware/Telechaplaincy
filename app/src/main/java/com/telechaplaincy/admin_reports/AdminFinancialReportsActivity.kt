package com.telechaplaincy.admin_reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.admin.AdminMainActivity
import com.telechaplaincy.appointment.AppointmentModelClass
import kotlinx.android.synthetic.main.activity_admin_financial_reports.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminFinancialReportsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainSpinnerSelection: String = ""
    private var totalIncome: Double = 0.00
    private var totalCommission: Double = 0.00
    private var totalPaymentToChaplain: Double = 0.00
    private var timeRangeSelectionFirstTime: Long = 0
    private var timeRangeSelectionSecondTime: Long = 0
    private var commissionForPerAppointmentPercentage: String = ""
    private var appointmentCancelFeePercentage: String = ""
    private var totalFineForCanceledAppointments: Double = 0.00

    private var appointmentsInfoModelClassArrayList = ArrayList<AppointmentModelClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_financial_reports)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        chaplainReportsFrequencySelectionMethod()
    }

    private fun setBarChart() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(totalIncome.toFloat(), 0))
        entries.add(BarEntry(totalPaymentToChaplain.toFloat(), 1))
        entries.add(BarEntry(totalCommission.toFloat(), 2))

        val barDataSet = BarDataSet(entries, "")

        val labels = ArrayList<String>()
        labels.add(getString(R.string.bar_total_income))
        labels.add(getString(R.string.bar_total_amount_to_chaplain))
        labels.add(getString(R.string.bar_total_commission))

        val data = BarData(labels, barDataSet)
        barChart.data = data // set the data and list of lables into chart

        barChart.setDescription("")  // set the description

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)

        barChart.animateY(1000)

        //hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.setDrawAxisLine(false)

        //remove right y-axis
        barChart.axisRight.isEnabled = false

        //remove legend
        barChart.legend.isEnabled = false
    }

    private fun getReports() {
        db.collection("appointment")
            .whereGreaterThanOrEqualTo("appointmentDate", timeRangeSelectionFirstTime.toString())
            .whereLessThan("appointmentDate", timeRangeSelectionSecondTime.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    appointmentsInfoModelClassArrayList.add(document.toObject<AppointmentModelClass>())
                }
                for (k in 0 until appointmentsInfoModelClassArrayList.size) {

                    if (appointmentsInfoModelClassArrayList[k].appointmentStatus != "canceled by patient"
                        && appointmentsInfoModelClassArrayList[k].appointmentStatus != "canceled by Chaplain"
                    ) {

                        totalIncome += appointmentsInfoModelClassArrayList[k].appointmentPrice?.toDouble()
                            ?: totalIncome

                        val appointmentCommission =
                            (appointmentsInfoModelClassArrayList[k].appointmentPrice?.toDouble()
                                ?.times(commissionForPerAppointmentPercentage.toInt()))?.div(100)
                                ?: 0.0

                        totalPaymentToChaplain += appointmentCommission

                    }

                    if (appointmentsInfoModelClassArrayList[k].appointmentStatus == "canceled by Chaplain") {

                        val appPrice =
                            (appointmentsInfoModelClassArrayList[k].appointmentPrice)?.toDouble()

                        val fineForAppCancel =
                            (appPrice?.times(appointmentCancelFeePercentage.toInt()))?.div(
                                100
                            )

                        if (fineForAppCancel != null) {
                            totalFineForCanceledAppointments += fineForAppCancel
                        }
                    }
                }
                totalPaymentToChaplain -= totalFineForCanceledAppointments

                totalCommission = totalIncome - totalPaymentToChaplain

                admin_reports_activity_total_income.text = "$totalIncome $"

                admin_reports_activity_total_payment_to_chaplain.text = "$totalPaymentToChaplain $"

                admin_reports_activity_total_commission.text = "$totalCommission $"

                setBarChart()

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun chaplainReportsFrequencySelectionMethod() {
        val optionsFrequencies = resources.getStringArray(R.array.reports_frequency)
        spinner_admin_reports_frequency.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionsFrequencies
        )
        spinner_admin_reports_frequency.onItemSelectedListener =
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

    private fun getFineAndCommissionPercentages() {

        dbSave = db.collection("appointment").document("appointmentInfo")
        dbSave.get().addOnSuccessListener { document ->
            if (document != null) {
                commissionForPerAppointmentPercentage =
                    document["appointmentCommissionPercentage"].toString()
                appointmentCancelFeePercentage =
                    document["appointmentCancelFinePercentage"].toString()

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
        totalCommission = 0.00
        totalPaymentToChaplain = 0.00
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
            admin_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

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
            admin_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

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
            admin_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

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
            admin_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

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
            admin_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

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
            admin_reports_date_frequency_textView.text = "$timeRangeFirst - $timeRangeSecond"

            Log.d("time:", "$timeRangeSelectionFirstTime - $timeRangeSelectionSecondTime")
                .toString()
        }

        getReports()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}