package com.telechaplaincy.patient_edit_appointment

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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.cloud_message.FcmNotificationsSender
import com.telechaplaincy.databinding.ActivityPatientAppointmentEditBinding
import com.telechaplaincy.mail_and_message_send_package.MailSendClass
import com.telechaplaincy.notification_page.NotificationModelClass
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentDetailActivity
import kotlinx.android.synthetic.main.activity_patient_appointment_continue.*
import kotlinx.android.synthetic.main.activity_patient_appointment_edit.*
import kotlinx.android.synthetic.main.activity_patient_appointment_time_selection.*
import kotlinx.android.synthetic.main.activity_patient_appointment_time_selection.patient_select_calender_chip_group
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PatientAppointmentEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAvailableTimes: DocumentReference
    private lateinit var dbAppointmentTimeEdit: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId:String = ""

    private lateinit var binding: ActivityPatientAppointmentEditBinding

    private var patientTimeZone:String = ""

    private var formattedDate: String = ""
    private var patientSelectedMonth = ""
    private var patientSelectedDay = ""
    private var todayDateLong = ""
    private var todayDateFormatted = ""
    private var timeSelectedString = ""

    private var chaplainProfileFieldUserId: String = ""
    private var chaplainCollectionName: String = ""
    private var continueButton: Boolean = false
    private var patientSelectedTime = ""
    private var chaplainEarliestDate: String = ""
    private var appointmentId: String = ""
    private var newAppointmentTime: String = ""
    private var chaplainAvailableTimesArray = ArrayList<String>()

    private var notificationTokenId: String = ""
    private var title: String = ""
    private var body: String = ""

    private var chaplainFullNameForMail = ""
    private var patientFullNameForMail = ""
    private var patientAppointmentTimeForMail = ""
    private var patientAppointmentDateForMail = ""
    private var patientMail = ""
    private var chaplainMail = ""
    private var chaplainPhoneNumber = ""

    private var mailSendClass = MailSendClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_edit)

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        patientSelectedTime = intent.getStringExtra("patientSelectedTime").toString()
        patientTimeZone = intent.getStringExtra("patientAppointmentTimeZone").toString()
        appointmentId = intent.getStringExtra("appointment_id").toString()
        patientAppointmentDateForMail =
            intent.getStringExtra("patientAppointmentDateForMail").toString()
        patientAppointmentTimeForMail =
            intent.getStringExtra("patientAppointmentTimeForMail").toString()
        chaplainFullNameForMail = intent.getStringExtra("chaplainFullNameForMail").toString()
        patientFullNameForMail = intent.getStringExtra("patientFullNameForMail").toString()
        patientMail = intent.getStringExtra("patientMail").toString()
        patientMail = intent.getStringExtra("chaplainMail").toString()

        binding = ActivityPatientAppointmentEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSaveAvailableTimes = db.collection(chaplainCollectionName).document(
            chaplainProfileFieldUserId
        )
            .collection("chaplainTimes").document("availableTimes")

        readChaplainEarliestDate()
        patientTimeZoneSelection()
        todayDateConvertSelectedTimeZone()

        patient_future_appointment_edit_activity_save_button_text.setOnClickListener {
            editedAppointmentTimeFromDatabase()
            appointmentTimeTakeBack()
            editedAppointmentTimeSave()
            sendMessageToChaplain()
            sendMailToPatient()
            sendMailToChaplain()
        }
    }

    private fun sendMailToChaplain() {
        val body = getString(
            R.string.patient_appointment_edit_mail_body_for_chaplain,
            patientAppointmentDateForMail,
            patientAppointmentTimeForMail,
            chaplainFullNameForMail,
            patientFullNameForMail
        )
        mailSendClass.sendMailToChaplain(
            getString(R.string.patient_appointment_edit_mail_title_for_chaplain),
            body, chaplainMail
        )
    }

    private fun sendMailToPatient() {

        val body = getString(
            R.string.patient_appointment_edit_mail_body,
            patientAppointmentDateForMail,
            patientAppointmentTimeForMail,
            chaplainFullNameForMail,
            patientFullNameForMail
        )
        mailSendClass.sendMailToChaplain(
            getString(R.string.patient_appointment_edit_mail_title),
            body, patientMail
        )
    }

    private fun sendMessageToChaplain() {

        db.collection("chaplains").document(chaplainProfileFieldUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    notificationTokenId = document["notificationTokenId"].toString()
                    chaplainPhoneNumber = document["phone"].toString()
                    title = getString(R.string.patient_appointment_edit_message_title)
                    body = getString(R.string.patient_appointment_edit_message_body)

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
            patientUserId,
            dateSent,
            messageId,
            false
        )

        dbSave.set(message, SetOptions.merge())
    }

    private fun editedAppointmentTimeFromDatabase() {
        db.collection("appointment").document(appointmentId)
            .update("appointmentDate", newAppointmentTime)
            .addOnSuccessListener { }
            .addOnFailureListener { }

        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("appointments").document(appointmentId)
            .update("appointmentDate", newAppointmentTime)

        db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)
            .update("appointmentDate", newAppointmentTime)
    }

    private fun editedAppointmentTimeSave(){
        progressBarAppointmentEditActivity.visibility = View.VISIBLE
        patient_future_appointment_edit_activity_save_button_text.isClickable = false
        if (continueButton){
            val map = HashMap<String, Any>()
            map["time"] = newAppointmentTime
            map["isBooked"] = true
            map["patientUid"] = patientUserId
            //Log.d("data upload", newAppointmentTime)
            dbSaveAvailableTimes.update((newAppointmentTime), map)
                .addOnSuccessListener {
                    patient_future_appointment_edit_activity_save_button_text.text = getString(R.string.patient_future_appointment_edit_activity_saved_button_text)
                    //Log.d("data upload", "DocumentSnapshot successfully written!")
                        progressBarAppointmentEditActivity.visibility = View.GONE
                        patient_future_appointment_edit_activity_save_button_text.isClickable = true
                        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
                        intent.putExtra("appointment_id", appointmentId)
                        startActivity(intent)
                        finish()

                }
                .addOnFailureListener { e ->
                    progressBarAppointmentEditActivity.visibility = View.GONE
                    patient_future_appointment_edit_activity_save_button_text.isClickable = true
                    //Log.w("data upload", "Error writing document", e)
                }
        }
        else{
            Toast.makeText(this, R.string.select_time_button_toast_message, Toast.LENGTH_LONG).show()
        }
    }

    private fun appointmentTimeTakeBack() {
        val map = HashMap<String, Any>()
        map["time"] = patientSelectedTime
        map["isBooked"] = false
        map["patientUid"] = "null"
        dbSaveAvailableTimes.update((patientSelectedTime), map)
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("data upload", "Error writing document", e)
            }
    }

    private fun calenderSelectedDate(){
        calendarViewPatientSelectionAppointmentEditActivity.minDate = todayDateLong.toLong()
        calendarViewPatientSelectionAppointmentEditActivity.maxDate = todayDateLong.toLong()+(2592000000)
        calendarViewPatientSelectionAppointmentEditActivity.date = chaplainEarliestDate.toLong()
        val format = SimpleDateFormat("yyyy-MM-dd")
        formattedDate = format.format(todayDateLong.toLong())

        val cTimeSelected = calendarViewPatientSelectionAppointmentEditActivity.date
        timeSelectedString = format.format(cTimeSelected).toString()

        readChaplainSelectedDates()

        binding.calendarViewPatientSelectionAppointmentEditActivity.setOnDateChangeListener{ view, year, month, dayOfMonth ->
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
                                    dateFormatLocalZoneForChip.timeZone = TimeZone.getTimeZone(
                                        ZoneId.of(patientTimeZone))
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
                                            newAppointmentTime = time.toString()
                                        }else{
                                            continueButton = false
                                        }
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
        //Log.d("timezone", chaplainTimeZone)
        val optionPatientTimeZoneSelection = TimeZone.getAvailableIDs()
        spinnerPatientTimeZoneAppointmentEditActivity.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionPatientTimeZoneSelection
        )
        spinnerPatientTimeZoneAppointmentEditActivity.setSelection(optionPatientTimeZoneSelection.indexOf(patientTimeZone))
        spinnerPatientTimeZoneAppointmentEditActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }

    private fun readChaplainEarliestDate(){
        var timeNow = System.currentTimeMillis().toString()
        dbSaveAvailableTimes.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val chaplainAvailableTimes = document.data
                    if (chaplainAvailableTimes != null) {
                        for ((key, value) in chaplainAvailableTimes) {
                            val v = value as Map<*, *>
                            val time = v["time"]
                            val isBooked = v["isBooked"]
                            //Log.d("time", "time: ${time.toString()}, time now: $timeNow")
                            if (isBooked == false && time.toString().toLong() > timeNow.toLong()) {
                                chaplainAvailableTimesArray.add(key)
                            }
                        }
                        if (!chaplainAvailableTimesArray.isNullOrEmpty()){
                            chaplainEarliestDate = Collections.min(chaplainAvailableTimesArray)
                            calenderSelectedDate()
                        }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }
}