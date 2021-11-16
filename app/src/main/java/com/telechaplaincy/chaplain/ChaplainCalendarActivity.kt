package com.telechaplaincy.chaplain

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.telechaplaincy.R
import com.telechaplaincy.databinding.ActivityChaplainCalendarBinding
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.activity_chaplain_calendar.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.spinner_patient_marital_status
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ChaplainCalendarActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAvailableTimes: DocumentReference
    private val db = Firebase.firestore
    private var chaplainCollectionName:String = ""
    private var chaplainAvailableTimesCollectionName:String = "chaplainAvailableTimes"
    private var chaplainDaysCollectionName:String = "days"
    private var chaplainUserId:String = ""

    private lateinit var binding: ActivityChaplainCalendarBinding

    private var chaplainTimeZone:String = ""
    private var chaplainAvailableTimes = ArrayList<String>()
    private var chaplainAvailableTimesRead = ArrayList<String>() //this array for reading the dates
    private var chaplainAvailableTime = ""
    private var formattedDate: String = ""
    private var chaplainAvailableTimeLongFormat = ""
    private var chaplainSelectedMonth = ""
    private var chaplainSelectedDay = ""

    private var chipArrayList = ArrayList<Chip>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_calendar)
        binding = ActivityChaplainCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            user = auth.currentUser!!
            chaplainUserId = user.uid
        }
        chaplainTimeZoneSelection()
        calenderSelectedDate()

        chipSelection()

        chaplain_enter_calendar_progressBar.visibility = View.GONE

        chaplain_enter_calendar_confirm_button.setOnClickListener {
            //chaplainAvailableTime = calendarViewChaplainEntry.date.toString()

        }

    }

    private fun chipSelection(){
        val chip1 = findViewById<Chip>(R.id.chip1)
        val chip2 = findViewById<Chip>(R.id.chip2)
        val chip3 = findViewById<Chip>(R.id.chip3)
        val chip4 = findViewById<Chip>(R.id.chip4)
        val chip5 = findViewById<Chip>(R.id.chip5)
        val chip6 = findViewById<Chip>(R.id.chip6)
        val chip7 = findViewById<Chip>(R.id.chip7)
        val chip8 = findViewById<Chip>(R.id.chip8)
        val chip9 = findViewById<Chip>(R.id.chip9)
        val chip10 = findViewById<Chip>(R.id.chip10)
        val chip11 = findViewById<Chip>(R.id.chip11)
        val chip12 = findViewById<Chip>(R.id.chip12)
        val chip13 = findViewById<Chip>(R.id.chip13)
        val chip14 = findViewById<Chip>(R.id.chip14)
        val chip15 = findViewById<Chip>(R.id.chip15)
        val chip16 = findViewById<Chip>(R.id.chip16)
        val chip17 = findViewById<Chip>(R.id.chip17)
        val chip18 = findViewById<Chip>(R.id.chip18)
        val chip19 = findViewById<Chip>(R.id.chip19)
        val chip20 = findViewById<Chip>(R.id.chip20)
        val chip21 = findViewById<Chip>(R.id.chip21)
        val chip22 = findViewById<Chip>(R.id.chip22)
        val chip23 = findViewById<Chip>(R.id.chip23)
        val chip24 = findViewById<Chip>(R.id.chip24)
        val chip25 = findViewById<Chip>(R.id.chip25)
        val chip26 = findViewById<Chip>(R.id.chip26)
        val chip27 = findViewById<Chip>(R.id.chip27)
        val chip28 = findViewById<Chip>(R.id.chip28)
        val chip29 = findViewById<Chip>(R.id.chip29)
        val chip30 = findViewById<Chip>(R.id.chip30)
        val chip31 = findViewById<Chip>(R.id.chip31)
        val chip32 = findViewById<Chip>(R.id.chip32)
        val chip33 = findViewById<Chip>(R.id.chip33)
        val chip34 = findViewById<Chip>(R.id.chip34)
        val chip35 = findViewById<Chip>(R.id.chip35)
        val chip36 = findViewById<Chip>(R.id.chip36)
        val chip37 = findViewById<Chip>(R.id.chip37)
        val chip38 = findViewById<Chip>(R.id.chip38)
        val chip39 = findViewById<Chip>(R.id.chip39)
        val chip40 = findViewById<Chip>(R.id.chip40)
        val chip41 = findViewById<Chip>(R.id.chip41)
        val chip42 = findViewById<Chip>(R.id.chip42)
        val chip43 = findViewById<Chip>(R.id.chip43)
        val chip44 = findViewById<Chip>(R.id.chip44)
        val chip45 = findViewById<Chip>(R.id.chip45)
        val chip46 = findViewById<Chip>(R.id.chip46)
        val chip47 = findViewById<Chip>(R.id.chip47)
        val chip48 = findViewById<Chip>(R.id.chip48)

        chipArrayList.add(chip1)
        chipArrayList.add(chip2)
        chipArrayList.add(chip3)
        chipArrayList.add(chip4)
        chipArrayList.add(chip5)
        chipArrayList.add(chip6)
        chipArrayList.add(chip7)
        chipArrayList.add(chip8)
        chipArrayList.add(chip9)
        chipArrayList.add(chip10)
        chipArrayList.add(chip11)
        chipArrayList.add(chip12)
        chipArrayList.add(chip13)
        chipArrayList.add(chip14)
        chipArrayList.add(chip15)
        chipArrayList.add(chip16)
        chipArrayList.add(chip17)
        chipArrayList.add(chip18)
        chipArrayList.add(chip19)
        chipArrayList.add(chip20)
        chipArrayList.add(chip21)
        chipArrayList.add(chip22)
        chipArrayList.add(chip23)
        chipArrayList.add(chip24)
        chipArrayList.add(chip25)
        chipArrayList.add(chip26)
        chipArrayList.add(chip27)
        chipArrayList.add(chip28)
        chipArrayList.add(chip29)
        chipArrayList.add(chip30)
        chipArrayList.add(chip31)
        chipArrayList.add(chip32)
        chipArrayList.add(chip33)
        chipArrayList.add(chip34)
        chipArrayList.add(chip35)
        chipArrayList.add(chip36)
        chipArrayList.add(chip37)
        chipArrayList.add(chip38)
        chipArrayList.add(chip39)
        chipArrayList.add(chip40)
        chipArrayList.add(chip41)
        chipArrayList.add(chip42)
        chipArrayList.add(chip43)
        chipArrayList.add(chip44)
        chipArrayList.add(chip45)
        chipArrayList.add(chip46)
        chipArrayList.add(chip47)
        chipArrayList.add(chip48)

        for (i in 0..47){
            chipArrayList[i].isCheckedIconVisible = false
            chipArrayList[i].setOnCheckedChangeListener { group, checkedId ->
                if (group.isChecked){
                    val chipText = chipArrayList[i].text as String
                    val chipTextTime = chipText.take(5)
                    chaplainAvailableTime = "$formattedDate $chipTextTime:00"
                    val parser =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formattedDate = LocalDateTime.parse(chaplainAvailableTime, parser)
                    Log.d("timezone cha", formattedDate.toString())

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT") //this defines the time zone that time will be converter
                    val dateStr = dateFormat.format(Date.from(formattedDate.atZone(ZoneId.of(chaplainTimeZone)).toInstant())) //this converts from selected time zone to gmt time zone
                    val dateStrLong = dateFormat.parse(dateStr).time //this line converts time to long
                    chaplainAvailableTimeLongFormat = dateStrLong.toString() // this line converts long time to string for sending to firebase
                    chaplainAvailableTimes.add(chaplainAvailableTimeLongFormat)
                    Log.d("timezone", dateStrLong.toString())
                    Log.d("timezone", dateStr)
                    Log.d("timezone", ZoneId.of(chaplainTimeZone).toString())
                }
            }
        }
    }

    private fun calenderSelectedDate(){
        calendarViewChaplainEntry.minDate = calendarViewChaplainEntry.date + 86400000
        calendarViewChaplainEntry.date = calendarViewChaplainEntry.date
        val format = SimpleDateFormat("yyyy-MM-dd")
        formattedDate = format.format(calendarViewChaplainEntry.date)

        chaplainSelectedMonth = formattedDate.substring(5, 7)
        chaplainSelectedDay = formattedDate.substring(8, 10)
        Log.d("timezone long", chaplainSelectedDay)
        readChaplainSelectedDates()
        binding.calendarViewChaplainEntry.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            allChipsUnSelection()
            saveChaplainAvailableDates()
            chaplainSelectedDay = dayOfMonth.toString()
            if (chaplainSelectedDay.length == 1){
                chaplainSelectedDay = "0$dayOfMonth"
            }
            Log.d("timezone long", chaplainSelectedDay)
            chaplainSelectedMonth = (month+1).toString()
            if (chaplainSelectedMonth.length == 1){
                chaplainSelectedMonth = "0$chaplainSelectedMonth"
            }
            val date = "$year-$chaplainSelectedMonth-$chaplainSelectedDay"
            formattedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
            //Log.d("timezone c", formattedDate)
            readChaplainSelectedDates()
            chaplainAvailableTimes.clear()
        }

    }

    private fun saveChaplainAvailableDates(){
        dbSaveAvailableTimes = db.collection(chaplainCollectionName).document(chaplainUserId)
            .collection(chaplainAvailableTimesCollectionName).document(chaplainSelectedMonth)
            .collection(chaplainDaysCollectionName).document(chaplainSelectedDay)

        if (chaplainAvailableTimes.isNotEmpty()){
            val calendarModelClass = ChaplainCalendarModelClass(chaplainAvailableTimes, chaplainTimeZone)
            dbSaveAvailableTimes.set(calendarModelClass, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("data upload", "DocumentSnapshot successfully written!")

                    //chaplainAvailableTimes.clear()
                }
                .addOnFailureListener {
                        e -> Log.w("data upload", "Error writing document", e)
                }
        }
    }

    private fun readChaplainSelectedDates(){
        dbSaveAvailableTimes = db.collection(chaplainCollectionName).document(chaplainUserId)
            .collection(chaplainAvailableTimesCollectionName).document(chaplainSelectedMonth)
            .collection(chaplainDaysCollectionName).document(chaplainSelectedDay)

        dbSaveAvailableTimes.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val chaplainCalendar = document.toObject<ChaplainCalendarModelClass>()
                    if (chaplainCalendar != null) {
                        chaplainCalendar.chaplainAvailableTimes?.let { chaplainAvailableTimesRead.addAll(it) }
                    }
                    if (chaplainAvailableTimesRead.isNotEmpty()){
                        for (k in 0 until chaplainAvailableTimesRead.size){
                            val readTime = chaplainAvailableTimesRead[k]

                            val dateFormatLocalZone = SimpleDateFormat("yyyy-MM-dd HH:mm")
                            dateFormatLocalZone.timeZone = TimeZone.getTimeZone(ZoneId.of(chaplainTimeZone))
                            val dateLocale = dateFormatLocalZone.format(Date(readTime.toLong()))
                            val chipText = dateLocale.takeLast(5)
                            for (i in 0..47){
                                if ((chipArrayList[i].text).contains(chipText)){
                                    chipArrayList[i].isChecked = true
                                }
                            }
                            Log.d("TAG 2", chipText)
                            Log.d("TAG 2", ZoneId.of(chaplainTimeZone).toString())
                        }
                    }
                    //Log.d("TAG", "DocumentSnapshot data: $chaplainAvailableTimes")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        chaplainAvailableTimesRead.clear()
    }

    private fun allChipsUnSelection(){
        for (i in 0..47){
            chipArrayList[i].isChecked = false
            }
    }

    private fun chaplainTimeZoneSelection(){
        chaplainTimeZone = TimeZone.getDefault().id
        //Log.d("timezone", chaplainTimeZone)
        val optionChaplainTimeZoneSelection = TimeZone.getAvailableIDs()
        spinnerChaplainTimeZone.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionChaplainTimeZoneSelection
        )
        spinnerChaplainTimeZone.setSelection(optionChaplainTimeZoneSelection.indexOf(chaplainTimeZone))
        spinnerChaplainTimeZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainTimeZone = optionChaplainTimeZoneSelection[position]
                chaplainAvailableTimes.clear()
                allChipsUnSelection()
                readChaplainSelectedDates()

                //Log.d("timezone", chaplainTimeZone)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}