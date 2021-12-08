package com.telechaplaincy.patient_future_appointments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_profile.PatientAppointmentPersonalInfo
import com.telechaplaincy.video_call.VideoCallActivity
import kotlinx.android.synthetic.main.activity_patient_appointment_continue.*
import kotlinx.android.synthetic.main.activity_patient_future_appointment_detail.*
import kotlinx.android.synthetic.main.activity_video_call.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PatientFutureAppointmentDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainProfileFieldUserId:String = ""
    private var chaplainProfileImageLink:String = ""
    private var chaplainProfileFirstName:String = ""
    private var chaplainProfileLastName:String = ""
    private var chaplainProfileAddressTitle:String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileCredentialTitle:String = ""
    private var appointmentTime:String = ""
    private var patientUserId:String = ""
    private var patientFirstName:String = ""
    private var patientLastName:String = ""
    private var appointmentPrice:String = ""
    private var appointmentId:String = ""
    private var patientAppointmentTimeZone:String = ""

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1


    private var chaplainUniqueUserId:String = ""
    private var patientUniqueUserId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_future_appointment_detail)
        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        patientUserId = user.uid

        dbSave = db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)

        takeAppointmentInfo()

        future_appointment_cancel_button.setOnClickListener {

        }

        future_appointment_call_button.setOnClickListener {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)){
                // If all the permissions are granted, call the call method and initialize the RtcEngine object and join a channel.
                callChaplainMethod()
            }

        }

        future_appointment_edit_button.setOnClickListener {

        }
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }

    private fun timerMethod() {
        future_appointment_call_button.isClickable = true
        val timeNow = System.currentTimeMillis()
        val appointmentTimeLong = appointmentTime.toLong()
        val timeRemain:Long = appointmentTimeLong - timeNow
        val timer = object: CountDownTimer(timeRemain, 1000) {
            override fun onTick(timeRemain: Long) {
                val timeRemainDay = (TimeUnit.MILLISECONDS.toDays(timeRemain)).toString()
                val timeRemainHours = (TimeUnit.MILLISECONDS.toHours(timeRemain)-TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeRemain))).toString()
                val timeRemainMinutes = (TimeUnit.MILLISECONDS.toMinutes(timeRemain)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeRemain))).toString()
                val timeRemainSeconds = (TimeUnit.MILLISECONDS.toSeconds(timeRemain)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemain))).toString()
                future_appointment_remaining_time_textView.text = getString(R.string.day, timeRemainDay, timeRemainHours, timeRemainMinutes, timeRemainSeconds)
            }

            override fun onFinish() {
                future_appointment_call_button.isClickable = true
            }
        }.start()
    }

    private fun callChaplainMethod() {
        //val toast = Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show()
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra("chaplain_name", future_appointment_chaplain_name_textView.text.toString())
        intent.putExtra("chaplainUniqueUserId", chaplainUniqueUserId)
        intent.putExtra("patientUniqueUserId", patientUniqueUserId)
        intent.putExtra("chaplainProfileImageLink", chaplainProfileImageLink)
        startActivity(intent)
    }

    private fun takeAppointmentInfo(){
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<AppointmentModelClass>()
                    if (result != null) {
                        if (result.chaplainProfileImageLink != null){
                            chaplainProfileImageLink = result.chaplainProfileImageLink.toString()
                            Picasso.get().load(chaplainProfileImageLink).into(future_appointment_imageView_chaplain_image)
                        }
                        if (result.chaplainUniqueUserId != null) {
                            chaplainUniqueUserId = result.chaplainUniqueUserId.toString()

                        }
                        if (result.patientUniqueUserId != null) {
                            patientUniqueUserId = result.patientUniqueUserId.toString()

                        }
                        if (result.chaplainName != null){
                            chaplainProfileFirstName = result.chaplainName.toString()

                        }
                        if (result.chaplainSurname != null){
                            chaplainProfileLastName = result.chaplainSurname.toString()
                        }
                        if (result.chaplainAddressingTitle != null){
                            chaplainProfileAddressTitle = result.chaplainAddressingTitle.toString()
                        }
                        if (result.chaplainCredentialsTitle != null){
                            credentialTitleArrayList = result.chaplainCredentialsTitle
                        }
                        if (!credentialTitleArrayList.isNullOrEmpty()){
                            for (k in credentialTitleArrayList){
                                chaplainProfileCredentialTitle += ", $k"
                            }
                            future_appointment_chaplain_name_textView.text = chaplainProfileAddressTitle+ " " +
                                    chaplainProfileFirstName+ " "+ chaplainProfileLastName + chaplainProfileCredentialTitle
                        }
                        if (result.patientName != null){
                            patientFirstName = result.patientName
                        }
                        if (result.patientSurname != null){
                            patientLastName = result.patientSurname
                            future_appointment_patient_textView.text = "$patientFirstName $patientLastName"
                        }
                        if (appointmentId != ""){
                            future_appointment_number_textView.text = appointmentId
                        }
                        if (result.patientAppointmentTimeZone != null){
                            patientAppointmentTimeZone = result.patientAppointmentTimeZone!!
                        }
                        if (result.appointmentDate != null){
                            appointmentTime = result.appointmentDate
                            val dateFormatLocalZone = SimpleDateFormat("EEE, dd.MMM.yyyy")
                            dateFormatLocalZone.timeZone = TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentDate = dateFormatLocalZone.format(Date(appointmentTime.toLong()))
                            future_appointment_date_textView.text = appointmentDate

                            val dateFormatTime = SimpleDateFormat("HH:mm aaa z")
                            dateFormatTime.timeZone = TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentTime = dateFormatTime.format(Date(appointmentTime.toLong()))
                            future_appointment_time_textView.text = "$appointmentTime $patientAppointmentTimeZone"
                        }
                        if (result.appointmentPrice != null){
                            appointmentPrice = result.appointmentPrice
                            future_appointment_price_textView.text = getString(R.string.appointment_price) + appointmentPrice
                        }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
                timerMethod()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}