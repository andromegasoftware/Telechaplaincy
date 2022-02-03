package com.telechaplaincy.chaplain_future_appointments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import com.telechaplaincy.chaplain.ChaplainMainActivity
import com.telechaplaincy.cloud_message.FcmNotificationsSender
import com.telechaplaincy.mail_and_message_send_package.MailSendClass
import com.telechaplaincy.notification_page.NotificationModelClass
import com.telechaplaincy.patient_profile.PatientProfileInfoActivityForChaplain
import com.telechaplaincy.pre_assessment_questions_show_to_chaplain.PreAssessmentQuestionsAnswersActivity
import com.telechaplaincy.video_call.VideoCallActivity
import kotlinx.android.synthetic.main.activity_chaplain_future_appointment_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class ChaplainFutureAppointmentDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainProfileFieldUserId:String = ""
    private var patientProfileImageLink:String = ""
    private var chaplainProfileFirstName:String = ""
    private var chaplainProfileLastName:String = ""
    private var appointmentTime:String = ""
    private var patientUserId:String = ""
    private var patientFirstName:String = ""
    private var patientLastName:String = ""
    private var appointmentPrice:String = ""
    private var appointmentId:String = ""
    private var patientAppointmentTimeZone:String = ""
    private var lastAppointmentCancelTime: String = ""
    private var appointmentStatus: String = ""

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1


    private var chaplainUniqueUserId: String = ""
    private var patientUniqueUserId: String = ""


    private var notificationTokenId: String = ""
    private var title: String = ""
    private var body: String = ""

    private var chaplainFullNameForMail = ""
    private var patientFullNameForMail = ""
    private var patientAppointmentDateForMail = ""
    private var patientAppointmentTimeForMail = ""
    private var patientMail = ""
    private var chaplainMail = ""
    private var chaplainCategory: String = ""
    private var patientPhoneNumber: String = ""

    private var mailSendClass = MailSendClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_future_appointment_details)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        chaplainProfileFieldUserId = user.uid
        chaplainMail = user.email.toString()

        dbSave = db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("appointments").document(appointmentId)

        takeAppointmentInfo()

        //this is for that chaplain will see the patient's appointment history
        chaplain_future_appointment_patient_past_appointments_textView.setOnClickListener {
            val intent = Intent(this, PatientAppointmentHistory::class.java)
            intent.putExtra("patientUserId", patientUserId)
            intent.putExtra("appointment_id", appointmentId)
            startActivity(intent)
            finish()
        }

        chaplain_future_appointment_cancel_button.setOnClickListener {
            alertDialogMessageMethodForAppointmentCancel()
        }

        chaplain_future_appointment_call_button.setOnClickListener {

            if (checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO,
                    PERMISSION_REQ_ID_RECORD_AUDIO
                ) &&
                checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
            ) {
                // If all the permissions are granted, call the call method and initialize the RtcEngine object and join a channel.
                callChaplainMethod()
                sendMailToPatientWhenUserStartCall()
                sendMessageToPatientWhenChaplainJoinedCall()
            }

        }

        chaplain_future_appointment_patient_info_button.setOnClickListener {
            val intent = Intent(this, PatientProfileInfoActivityForChaplain::class.java)
            intent.putExtra("patientUserId", patientUserId)
            intent.putExtra("appointment_id", appointmentId)
            startActivity(intent)
            finish()
        }

        chaplain_future_appointment_patient_assessment_textView.setOnClickListener {
            val intent = Intent(this, PreAssessmentQuestionsAnswersActivity::class.java)
            intent.putExtra("patientUserId", patientUserId)
            intent.putExtra("chaplainCategory", chaplainCategory)
            intent.putExtra("appointment_id", appointmentId)
            startActivity(intent)
            finish()
        }
    }

    private fun sendMessageToPatientWhenChaplainJoinedCall() {

        db.collection("patients").document(patientUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    notificationTokenId = document["notificationTokenId"].toString()
                    patientPhoneNumber = document["phone"].toString()
                    title = getString(R.string.patient_mail_when_chaplain_join_meeting_title)
                    body = getString(R.string.patient_mail_when_chaplain_join_meeting_body)

                    val sender = FcmNotificationsSender(
                        notificationTokenId,
                        title,
                        body,
                        applicationContext,
                        this
                    )
                    sender.SendNotifications()
                    mailSendClass.textMessageSendMethod(patientPhoneNumber, body)

                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun sendMailToPatientWhenUserStartCall() {
        val subject = getString(R.string.patient_mail_when_chaplain_join_meeting_title)
        val body = getString(R.string.patient_mail_when_chaplain_join_meeting_body)
        mailSendClass.sendMailToChaplain(subject, body, patientMail)
    }

    private fun sendMailToChaplain() {
        val body = getString(
            R.string.chaplain_appointment_cancel_mail_body_for_chaplain,
            patientAppointmentDateForMail,
            patientAppointmentTimeForMail,
            chaplainFullNameForMail,
            patientFullNameForMail,
            appointmentPrice
        )
        val message = hashMapOf(
            "body" to body,
            "mailAddress" to chaplainMail,
            "subject" to getString(R.string.chaplain_appointment_cancel_mail_title_for_chaplain)
        )
        db.collection("mailSend").document()
            .set(message, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }
    }

    private fun sendMailToPatient() {
        val body = getString(
            R.string.chaplain_appointment_cancel_mail_body,
            patientAppointmentDateForMail,
            patientAppointmentTimeForMail,
            chaplainFullNameForMail,
            patientFullNameForMail,
            appointmentPrice
        )
        val message = hashMapOf(
            "body" to body,
            "mailAddress" to patientMail,
            "subject" to getString(R.string.chaplain_appointment_cancel_mail_title)
        )
        db.collection("mailSend").document()
            .set(message, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
            }
    }

    private fun sendMessageToPatient() {

        db.collection("patients").document(patientUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    notificationTokenId = document["notificationTokenId"].toString()
                    patientMail = document["email"].toString()
                    //Log.d("notificationTokenId", notificationTokenId)

                    title = getString(R.string.chaplain_appointment_cancel_message_title)
                    body = getString(R.string.chaplain_appointment_cancel_message_body)

                    val sender = FcmNotificationsSender(
                        notificationTokenId,
                        title,
                        body,
                        applicationContext,
                        this
                    )
                    sender.SendNotifications()

                    saveMessageToInbox()

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
            chaplainProfileFieldUserId,
            dateSent,
            messageId,
            false
        )

        dbSave.set(message, SetOptions.merge())
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    private fun timerMethod() {
        chaplain_future_appointment_call_button.isClickable =
            true //when I publish, I will make this false
        val timeNow = System.currentTimeMillis()
        val appointmentTimeLong = appointmentTime.toLong()
        val timeRemain: Long = appointmentTimeLong - timeNow
        val timer = object : CountDownTimer(timeRemain, 1000) {
            override fun onTick(timeRemain: Long) {
                val timeRemainDay = (TimeUnit.MILLISECONDS.toDays(timeRemain)).toString()
                val timeRemainHours =
                    (TimeUnit.MILLISECONDS.toHours(timeRemain) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(timeRemain)
                    )).toString()
                val timeRemainMinutes =
                    (TimeUnit.MILLISECONDS.toMinutes(timeRemain) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(timeRemain)
                    )).toString()
                val timeRemainSeconds =
                    (TimeUnit.MILLISECONDS.toSeconds(timeRemain) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeRemain)
                    )).toString()
                chaplain_future_appointment_remaining_time_textView.text = getString(
                    R.string.day,
                    timeRemainDay,
                    timeRemainHours,
                    timeRemainMinutes,
                    timeRemainSeconds
                )
            }

            override fun onFinish() {
                chaplain_future_appointment_call_button.isClickable = true
            }
        }.start()
    }

    private fun callChaplainMethod() {
        //val toast = Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show()
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra("chaplain_name", future_appointment_patient_name_textView.text.toString())
        intent.putExtra("userId", chaplainUniqueUserId)
        intent.putExtra("patientUniqueUserId", patientUniqueUserId)
        intent.putExtra("chaplainProfileImageLink", patientProfileImageLink)
        intent.putExtra("appointmentId", appointmentId)
        intent.putExtra("userType", "chaplain")
        startActivity(intent)
    }

    private fun takeAppointmentInfo(){
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<AppointmentModelClass>()
                    if (result != null) {
                        if (result.patientProfileImageLink != null){
                            patientProfileImageLink = result.patientProfileImageLink.toString()
                            Picasso.get().load(patientProfileImageLink).into(future_appointment_imageView_patient_image)
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
                        if (result.chaplainSurname != null) {
                            chaplainProfileLastName = result.chaplainSurname.toString()
                            chaplainFullNameForMail =
                                "$chaplainProfileFirstName $chaplainProfileLastName"
                            future_appointment_chaplain_textView.text = chaplainFullNameForMail
                        }

                        if (result.patientName != null){
                            patientFirstName = result.patientName
                        }
                        if (result.patientSurname != null) {
                            patientLastName = result.patientSurname
                            patientFullNameForMail = "$patientFirstName $patientLastName"
                            future_appointment_patient_name_textView.text = patientFullNameForMail
                        }
                        if (appointmentId != ""){
                            chaplain_future_appointment_number_textView.text = appointmentId
                        }
                        if (result.patientAppointmentTimeZone != null){
                            patientAppointmentTimeZone = result.patientAppointmentTimeZone!!
                        }
                        if (result.appointmentDate != null) {
                            appointmentTime = result.appointmentDate
                            val dateFormatLocalZone = SimpleDateFormat("EEE, dd.MMM.yyyy")
                            dateFormatLocalZone.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentDate =
                                dateFormatLocalZone.format(Date(appointmentTime.toLong()))
                            patientAppointmentDateForMail = appointmentDate.toString()
                            chaplain_future_appointment_date_textView.text =
                                patientAppointmentDateForMail

                            val dateFormatTime = SimpleDateFormat("HH:mm aaa z")
                            dateFormatTime.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentTime =
                                dateFormatTime.format(Date(appointmentTime.toLong()))
                            patientAppointmentTimeForMail =
                                "$appointmentTime $patientAppointmentTimeZone"
                            chaplain_future_appointment_time_textView.text =
                                patientAppointmentTimeForMail
                        }
                        if (result.appointmentPrice != null) {
                            appointmentPrice = result.appointmentPrice
                            chaplain_future_appointment_price_textView.text =
                                getString(R.string.appointment_price) + appointmentPrice
                        }
                        if (result.chaplainId != null) {
                            patientUserId = result.patientId.toString()
                        }
                        if (result.appointmentStatus != null) {
                            appointmentStatus = result.appointmentStatus.toString()
                        }
                        if (result.chaplainCategory != null) {
                            chaplainCategory = result.chaplainCategory.toString()
                        }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
                if (appointmentStatus != "canceled by Chaplain" && appointmentStatus != "canceled by patient"){
                    timerMethod()
                }else{
                    chaplain_future_appointment_remaining_time_textView.text = appointmentStatus
                    //future_appointment_edit_button.isClickable = false
                    chaplain_future_appointment_call_button.isClickable = false
                    chaplain_future_appointment_cancel_button.isClickable = false
                }

            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun checkCancelTimeAndCancelAppointment(){
        db.collection("appointment").document("appointmentInfo").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    lastAppointmentCancelTime =
                        document["appointmentCancelLastDateForChaplain"].toString()
                    //Log.d("lastAppointmentEditTime", lastAppointmentEditTime)
                    val timeNow = System.currentTimeMillis()
                    val appointmentTimeLong = appointmentTime.toLong()
                    val timeDifference:Long = appointmentTimeLong - timeNow
                    val leftAppointmentTimeHours = TimeUnit.MILLISECONDS.toHours(timeDifference)
                    if(timeDifference > lastAppointmentCancelTime.toLong()){
                        futureAppointmentCancelMethod()
                    }else{
                        val toast = Toast.makeText(this, getString(R.string.patient_future_appointment_cancel_activity_toast_message, leftAppointmentTimeHours.toString()), Toast.LENGTH_LONG).show()
                    }
                } else {
                    //Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun alertDialogMessageMethodForAppointmentCancel(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.patient_future_appointment_cancel_activity_cancel_alert_dialog_title))
        builder.setMessage(getString(R.string.patient_future_appointment_cancel_activity_cancel_alert_dialog_question))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            checkCancelTimeAndCancelAppointment()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun futureAppointmentCancelMethod(){
        appointmentStatus = "canceled by Chaplain"
        db.collection("appointment").document(appointmentId)
            .update("appointmentStatus", appointmentStatus)
            .addOnSuccessListener {

                sendMessageToPatient()
                sendMailToPatient()
                sendMailToChaplain()
            }
            .addOnFailureListener { }

        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("appointments").document(appointmentId)
            .update("appointmentStatus", appointmentStatus)

        db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)
            .update("appointmentStatus", appointmentStatus)

        makeAvailableChaplainTimeAfterCancelingAppointment()
        val intent = Intent(this, ChaplainFutureAppointmentDetailsActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }

    private fun makeAvailableChaplainTimeAfterCancelingAppointment() {
        val map = HashMap<String, Any>()
        map["time"] = appointmentTime
        map["isBooked"] = false
        map["patientUid"] = "null"
        db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("chaplainTimes")
            .document("availableTimes").update((appointmentTime), map)
            .addOnSuccessListener {
                Log.d("data upload", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("data upload", "Error writing document", e)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}