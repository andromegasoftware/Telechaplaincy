package com.telechaplaincy.appointment

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_selection.chaplains_selection.ChaplainsListedSelectionActivity
import com.telechaplaincy.databinding.ActivityChaplainCalendarBinding
import com.telechaplaincy.databinding.ActivityPatientAppointmentTimeSelectionBinding
import kotlinx.android.synthetic.main.activity_chaplain_calendar.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import kotlinx.android.synthetic.main.activity_patient_appointment_time_selection.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PatientAppointmentTimeSelectionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAvailableTimes: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId:String = ""

    private lateinit var binding: ActivityPatientAppointmentTimeSelectionBinding

    private var patientTimeZone:String = ""

    private var formattedDate: String = ""
    private var patientSelectedMonth = ""
    private var patientSelectedDay = ""
    private var todayDateLong = ""
    private var todayDateFormatted = ""
    var timeSelectedString = ""

    private var chaplainCategory = ""
    private var chaplainProfileFieldUserId:String = ""
    private var chaplainCollectionName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_time_selection)

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()

        binding = ActivityPatientAppointmentTimeSelectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        patientTimeZoneSelection()
        todayDateConvertSelectedTimeZone()
        calenderSelectedDate()

    }

    private fun calenderSelectedDate(){
        calendarViewPatientSelection.minDate = todayDateLong.toLong()
        calendarViewPatientSelection.maxDate = todayDateLong.toLong()+(2592000000)
        calendarViewPatientSelection.date = todayDateLong.toLong()
        val format = SimpleDateFormat("yyyy-MM-dd")
        formattedDate = format.format(todayDateLong.toLong())

        val cTimeSelected = calendarViewPatientSelection.date
        timeSelectedString = format.format(cTimeSelected).toString()

        readChaplainSelectedDates()

        binding.calendarViewPatientSelection.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            patientSelectedDay = dayOfMonth.toString()
            if (patientSelectedDay.length == 1){
                patientSelectedDay = "0$dayOfMonth"
            }
            //Log.d("chaplainSelectedDay", chaplainSelectedDay)
            patientSelectedMonth = (month+1).toString()
            if (patientSelectedMonth.length == 1){
                patientSelectedMonth = "0$patientSelectedMonth"
            }
            val date = "$year-$patientSelectedMonth-$patientSelectedDay"
            timeSelectedString = "$year-$patientSelectedMonth-$patientSelectedDay"
            //Log.d("chaplainSelectedDay1", cTimeSelectedString)
            formattedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
            //Log.d("timezone c", formattedDate)

            readChaplainSelectedDates()

        }

    }

    private fun readChaplainSelectedDates() {
        dbSaveAvailableTimes = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
            .collection("chaplainTimes").document("availableTimes")

        dbSaveAvailableTimes.addSnapshotListener { snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                patient_select_calender_chip_group.removeAllViews()
                var chaplainAvailableTimes = snapshot.data
                chaplainAvailableTimes?.let {
                    chaplainAvailableTimes = chaplainAvailableTimes!!.toList().sortedBy { (key, _) -> key }.toMap()
                    for ((key, value) in chaplainAvailableTimes!!) {
                        val v = value as Map<*, *>
                        val time = v["time"]
                        val isBooked = v["isBooked"]
                        if (isBooked == false){
                            val readTime = time.toString()
                            if (readTime > todayDateLong){
                            val dateFormatLocalZone = SimpleDateFormat("yyyy-MM-dd HH:mm")
                            dateFormatLocalZone.timeZone = TimeZone.getTimeZone(ZoneId.of(patientTimeZone))
                            val chipTimeLocale = dateFormatLocalZone.format(Date(readTime.toLong()))
                            if (chipTimeLocale.take(10) == timeSelectedString) {
                                val chipText = chipTimeLocale.takeLast(5)
                                val chipChaplainAvailableTime = layoutInflater.inflate(
                                    R.layout.single_chip_layout,
                                    patient_select_calender_chip_group,
                                    false
                                ) as Chip
                                chipChaplainAvailableTime.isCheckedIconVisible = true
                                chipChaplainAvailableTime.isCheckable = true
                                chipChaplainAvailableTime.text = chipText
                                patient_select_calender_chip_group.addView(chipChaplainAvailableTime)

                                chipChaplainAvailableTime.setOnCloseIconClickListener {

                                }
                            }
                            }
                        }
                    }
                    //Log.d("chaplainArray", chaplainAvailableTimesArray.toString())
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
        }
    }

    private fun patientTimeZoneSelection(){
        patientTimeZone = TimeZone.getDefault().id
        //Log.d("timezone", chaplainTimeZone)
        val optionPatientTimeZoneSelection = TimeZone.getAvailableIDs()
        spinnerPatientTimeZone.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionPatientTimeZoneSelection
        )
        spinnerPatientTimeZone.setSelection(optionPatientTimeZoneSelection.indexOf(patientTimeZone))
        spinnerPatientTimeZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                patientTimeZone = optionPatientTimeZoneSelection[position]
                todayDateConvertSelectedTimeZone()
                readChaplainSelectedDates()
                //Log.d("timezone", patientTimeZone)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun todayDateConvertSelectedTimeZone(){
        todayDateLong = System.currentTimeMillis().toString()
        //Log.d("todayDateLong", todayDateLong)
        val dateFormatLocalZone = SimpleDateFormat("yyyy-MM-dd HH:mm")
        dateFormatLocalZone.timeZone = TimeZone.getTimeZone(ZoneId.of(patientTimeZone))
        todayDateFormatted = dateFormatLocalZone.format(Date(todayDateLong.toLong()))
        //Log.d("todayDateLong tz select", todayDateFormatted)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentContinueActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        startActivity(intent)
        finish()
    }
}