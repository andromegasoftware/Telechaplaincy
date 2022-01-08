package com.telechaplaincy.appointment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.databinding.ActivityPatientAppointmentTimeSelectionBinding
import kotlinx.android.synthetic.main.activity_chaplain_calendar.*
import kotlinx.android.synthetic.main.activity_patient_appointment_continue.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import kotlinx.android.synthetic.main.activity_patient_appointment_time_selection.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PatientAppointmentTimeSelectionActivity : AppCompatActivity() {

    private lateinit var dbSaveAppointmentForMainCollection: DocumentReference //this is to create appointment data in firestore to check 15 minutes
    private var appointmentId = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAvailableTimes: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId: String = ""

    private lateinit var binding: ActivityPatientAppointmentTimeSelectionBinding

    private var patientTimeZone: String = ""

    private var formattedDate: String = ""
    private var patientSelectedMonth = ""
    private var patientSelectedDay = ""
    private var todayDateLong = ""
    private var todayDateFormatted = ""
    private var timeSelectedString = ""

    private var chaplainCategory = ""
    private var chaplainProfileFieldUserId:String = ""
    private var chaplainCollectionName:String = ""
    private var continueButton:Boolean = false
    private var patientSelectedTime = ""
    private var chaplainEarliestDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_time_selection)

        //this is to create appointment data in firestore to check 15 minutes
        dbSaveAppointmentForMainCollection = db.collection("appointment_temporary").document()
        appointmentId = intent.getStringExtra("appointmentId").toString()

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()
        chaplainEarliestDate = intent.getStringExtra("readTime").toString()
        patientSelectedTime = intent.getStringExtra("patientSelectedTime").toString()

        binding = ActivityPatientAppointmentTimeSelectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSaveAvailableTimes = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)
            .collection("chaplainTimes").document("availableTimes")

        patientTimeZoneSelection()
        todayDateConvertSelectedTimeZone()
        calenderSelectedDate()

        patient_appointment_time_selection_button.setOnClickListener {
            if (continueButton){
                val map = HashMap<String, Any>()
                map["time"] = patientSelectedTime
                map["isBooked"] = true
                map["patientUid"] = patientUserId
                dbSaveAvailableTimes.update((patientSelectedTime), map)
                    .addOnSuccessListener {
                        Log.d("data upload", "DocumentSnapshot successfully written!")
                        createAppointmentData()
                    }
                    .addOnFailureListener { e ->
                        Log.w("data upload", "Error writing document", e)
                    }

            } else {
                Toast.makeText(this, R.string.select_time_button_toast_message, Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    //this is to delete appointment data in firestore created to check 15 minutes
    private fun deleteAppointmentData() {
        if (appointmentId != "null" && appointmentId != "") {
            db.collection("appointment_temporary").document(appointmentId).delete()
                .addOnSuccessListener {
                    //Log.d("appointment_info", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    //Log.w("appointment_info", "Error writing document", e)
                }
        }
    }

    //this is to create appointment data in firestore to check 15 minutes
    private fun createAppointmentData() {
        appointmentId = dbSaveAppointmentForMainCollection.id
        val appointmentInfo = hashMapOf(
            "appointmentId" to appointmentId,
            "appointmentDate" to patientSelectedTime,
            "chaplainId" to chaplainProfileFieldUserId,
            "isAppointmentPaymentDone" to false,
            "appointmentCreationDate" to FieldValue.serverTimestamp()
        )
        dbSaveAppointmentForMainCollection.set(appointmentInfo, SetOptions.merge())
            .addOnSuccessListener {
                val intent = Intent(this, PatientAppointmentSummaryActivity::class.java)
                intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
                intent.putExtra("chaplain_category", chaplainCategory)
                intent.putExtra("readTime", chaplainEarliestDate)
                intent.putExtra("patientSelectedTime", patientSelectedTime)
                intent.putExtra("appointmentTimeZone", patientTimeZone)
                intent.putExtra("appointmentId", appointmentId)
                startActivity(intent)
                finish()
            }
    }

    private fun calenderSelectedDate(){
        calendarViewPatientSelection.minDate = todayDateLong.toLong()
        calendarViewPatientSelection.maxDate = todayDateLong.toLong()+(2592000000)
        if (patientSelectedTime != "" && patientSelectedTime != "null"){
            calendarViewPatientSelection.date = patientSelectedTime.toLong()
        }else{
            calendarViewPatientSelection.date = chaplainEarliestDate.toLong()
        }
        val format = SimpleDateFormat("yyyy-MM-dd")
        formattedDate = format.format(todayDateLong.toLong())

        val cTimeSelected = calendarViewPatientSelection.date
        timeSelectedString = format.format(cTimeSelected).toString()

        readChaplainSelectedDates()

        binding.calendarViewPatientSelection.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            continueButton = false
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
        dbSaveAvailableTimes.addSnapshotListener { snapshot, e ->
            patient_select_calender_chip_group.removeAllViews()
            if (snapshot != null && snapshot.exists()) {
                var chaplainAvailableTimes = snapshot.data
                chaplainAvailableTimes?.let {
                    chaplainAvailableTimes = chaplainAvailableTimes!!.toList().sortedBy { (key, _) -> key }.toMap()
                    for ((key, value) in chaplainAvailableTimes!!) {
                        val v = value as Map<*, *>
                        val time = v["time"]
                        var isBooked = v["isBooked"]
                        var patientUid = v["patientUid"]
                        if (isBooked == false) {
                            val readTime = time.toString()
                            if (readTime > todayDateLong) {
                                val dateFormatLocalZone = SimpleDateFormat("yyyy-MM-dd HH:mm")
                                dateFormatLocalZone.timeZone = TimeZone.getTimeZone(ZoneId.of(patientTimeZone))
                                val chipTimeLocale = dateFormatLocalZone.format(Date(readTime.toLong()))
                                if (chipTimeLocale.take(10) == timeSelectedString) {
                                    val dateFormatLocalZoneForChip = SimpleDateFormat("H:mm a")
                                    dateFormatLocalZoneForChip.timeZone = TimeZone.getTimeZone(ZoneId.of(patientTimeZone))
                                    val chipText = dateFormatLocalZoneForChip.format(Date(readTime.toLong()))
                                    val chipChaplainAvailableTime = layoutInflater.inflate(
                                        R.layout.single_chip_layout,
                                        patient_select_calender_chip_group,
                                        false
                                    ) as Chip
                                    chipChaplainAvailableTime.isCheckedIconVisible = false
                                    chipChaplainAvailableTime.text = chipText
                                    patient_select_calender_chip_group.addView(
                                        chipChaplainAvailableTime
                                    )
                                    chipChaplainAvailableTime.setOnClickListener {
                                        if (chipChaplainAvailableTime.isChecked) {
                                            continueButton = true
                                            patientSelectedTime = time.toString()
                                        }else{
                                        continueButton = false
                                            }
                                        }
                                    if (patientSelectedTime == time){
                                        chipChaplainAvailableTime.isChecked = true
                                        continueButton = true
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
        //Log.d("patientTimeZone", patientTimeZone)
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
        deleteAppointmentData()
        val intent = Intent(this, PatientAppointmentContinueActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        startActivity(intent)
        finish()
    }
}