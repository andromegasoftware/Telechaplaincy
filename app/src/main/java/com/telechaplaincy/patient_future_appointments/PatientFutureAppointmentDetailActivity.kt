package com.telechaplaincy.patient_future_appointments

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
import com.telechaplaincy.cloud_message.FcmNotificationsSender
import com.telechaplaincy.notification_page.NotificationModelClass
import com.telechaplaincy.patient.PatientMainActivity
import com.telechaplaincy.patient_edit_appointment.PatientAppointmentEditActivity
import com.telechaplaincy.pre_assessment_questions.*
import com.telechaplaincy.video_call.VideoCallActivity
import kotlinx.android.synthetic.main.activity_patient_future_appointment_detail.*
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
    private var patientAppointmentTimeZone: String = ""
    private var lastAppointmentEditTime: String = ""
    private var lastAppointmentCancelTime: String = ""
    private var appointmentStatus: String = ""
    private var chaplainCategory: String = ""

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    private var chaplainUniqueUserId: String = ""
    private var patientUniqueUserId: String = ""

    private var notificationTokenId: String = ""
    private var title: String = ""
    private var body: String = ""

    private var chaplainFullNameForMail = ""
    private var patientFullNameForMail = ""
    private var patientAppointmentTimeForMail = ""
    private var patientAppointmentDateForMail = ""
    private var patientMail = ""
    private var chaplainMail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_future_appointment_detail)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        patientUserId = user.uid
        patientMail = user.email.toString()

        dbSave = db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)

        takeAppointmentInfo()

        future_appointment_preAssessment_textView.setOnClickListener {
            if (chaplainCategory == "HumanistChaplains") {
                val intent = Intent(this, CommunityChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            } else if (chaplainCategory == "HealthChaplains") {
                val intent = Intent(this, HealthCareChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "HospiceChaplains"){
                val intent = Intent(this, HospiceChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "MentalChaplains"){
                val intent = Intent(this, MentalHealthChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            //crisis chaplain and mental health Chaplain assessment questions are same
            else if (chaplainCategory == "CrisisChaplains"){
                val intent = Intent(this, MentalHealthChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "PalliativeChaplains"){
                val intent = Intent(this, PalliativeCareChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "PetChaplains"){
                val intent = Intent(this, PetChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "PrisonChaplains"){
                val intent = Intent(this, PrisonChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "PediatricChaplains"){
                val intent = Intent(this, PediatricChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "CollegeChaplains"){
                val intent = Intent(this, CollegeChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
            else if (chaplainCategory == "CorporateChaplains"){
                val intent = Intent(this, CorporateChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
             else if (chaplainCategory == "MilitaryChaplains"){
                val intent = Intent(this, MilitaryLawChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }
             else if (chaplainCategory == "LawChaplains"){
                val intent = Intent(this, MilitaryLawChaplainAssessmentActivity::class.java)
                intent.putExtra("appointment_id", appointmentId)
                startActivity(intent)
                finish()
            }


            /*val intent = Intent(this, MilitaryLawChaplainAssessmentActivity::class.java)
            intent.putExtra("appointment_id", appointmentId)
            startActivity(intent)
            finish()*/
        }

        future_appointment_cancel_button.setOnClickListener {
            alertDialogMessageMethodForAppointmentCancel()
        }

        future_appointment_call_button.setOnClickListener {

            if (checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO,
                    PERMISSION_REQ_ID_RECORD_AUDIO
                ) &&
                checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
            ) {
                // If all the permissions are granted, call the call method and initialize the RtcEngine object and join a channel.
                callChaplainMethod()
            }

        }

        future_appointment_edit_button.setOnClickListener {
            alertDialogMessageMethodForAppointmentEdit()
        }
    }

    private fun sendMailToChaplain() {
        val body = getString(
            R.string.patient_appointment_cancel_mail_body_for_chaplain,
            patientAppointmentDateForMail,
            patientAppointmentTimeForMail,
            chaplainFullNameForMail,
            patientFullNameForMail,
            appointmentPrice
        )
        val message = hashMapOf(
            "body" to body,
            "mailAddress" to chaplainMail,
            "subject" to getString(R.string.patient_appointment_cancel_mail_title_for_chaplain)
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
            R.string.patient_appointment_cancel_mail_body,
            patientAppointmentDateForMail,
            patientAppointmentTimeForMail,
            chaplainFullNameForMail,
            patientFullNameForMail,
            appointmentPrice
        )

        val message = hashMapOf(
            "body" to body,
            "mailAddress" to patientMail,
            "subject" to getString(R.string.patient_appointment_cancel_mail_title)
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

    private fun sendMessageToChaplain() {

        db.collection("chaplains").document(chaplainProfileFieldUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    notificationTokenId = document["notificationTokenId"].toString()
                    chaplainMail =
                        document["email"].toString() //this is for sending email to chaplain
                    //Log.d("notificationTokenId", notificationTokenId)
                    title = getString(R.string.patient_appointment_cancel_message_title)
                    body = getString(R.string.patient_appointment_cancel_message_body)

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
            patientUserId,
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
        intent.putExtra("chaplainProfileFieldUserId", chaplainProfileFieldUserId)
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
                        if (!credentialTitleArrayList.isNullOrEmpty()) {
                            for (k in credentialTitleArrayList) {
                                chaplainProfileCredentialTitle += ", $k"
                            }

                            chaplainFullNameForMail = chaplainProfileAddressTitle + " " +
                                    chaplainProfileFirstName + " " + chaplainProfileLastName + chaplainProfileCredentialTitle

                            future_appointment_chaplain_name_textView.text = chaplainFullNameForMail
                        }
                        if (result.patientName != null){
                            patientFirstName = result.patientName
                        }
                        if (result.patientSurname != null) {
                            patientLastName = result.patientSurname

                            patientFullNameForMail = "$patientFirstName $patientLastName"
                            future_appointment_patient_textView.text = patientFullNameForMail
                        }
                        if (appointmentId != ""){
                            future_appointment_number_textView.text = appointmentId
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
                            future_appointment_date_textView.text = patientAppointmentDateForMail

                            val dateFormatTime = SimpleDateFormat("HH:mm aaa z")
                            dateFormatTime.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentTime =
                                dateFormatTime.format(Date(appointmentTime.toLong()))
                            patientAppointmentTimeForMail =
                                "$appointmentTime $patientAppointmentTimeZone"
                            future_appointment_time_textView.text = patientAppointmentTimeForMail
                        }
                        if (result.appointmentPrice != null) {
                            appointmentPrice = result.appointmentPrice
                            future_appointment_price_textView.text =
                                getString(R.string.appointment_price) + appointmentPrice
                        }
                        if (result.chaplainId != null) {
                            chaplainProfileFieldUserId = result.chaplainId
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
                    future_appointment_remaining_time_textView.text = appointmentStatus
                    future_appointment_edit_button.isClickable = false
                    future_appointment_call_button.isClickable = false
                    future_appointment_cancel_button.isClickable = false
                }

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

    private fun checkEditTimeAndEditAppointment(){
        db.collection("appointment").document("appointmentInfo").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    lastAppointmentEditTime = document["appointmentEditLastDate"].toString()
                    //Log.d("lastAppointmentEditTime", lastAppointmentEditTime)
                    val timeNow = System.currentTimeMillis()
                    val appointmentTimeLong = appointmentTime.toLong()
                    val timeDifference:Long = appointmentTimeLong - timeNow
                    val leftAppointmentTimeHours = TimeUnit.MILLISECONDS.toHours(timeDifference)
                    if(timeDifference > lastAppointmentEditTime.toLong()) {
                        val intent = Intent(this, PatientAppointmentEditActivity::class.java)
                        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
                        intent.putExtra("patientSelectedTime", appointmentTime)
                        intent.putExtra("patientAppointmentTimeZone", patientAppointmentTimeZone)
                        intent.putExtra("appointment_id", appointmentId)
                        intent.putExtra(
                            "patientAppointmentDateForMail",
                            patientAppointmentDateForMail
                        )
                        intent.putExtra(
                            "patientAppointmentTimeForMail",
                            patientAppointmentTimeForMail
                        )
                        intent.putExtra("chaplainFullNameForMail", chaplainFullNameForMail)
                        intent.putExtra("patientFullNameForMail", patientFullNameForMail)
                        intent.putExtra("patientMail", patientMail)
                        intent.putExtra("chaplainMail", chaplainMail)
                        startActivity(intent)
                        finish()
                    }else{
                        val toast = Toast.makeText(this, getString(R.string.patient_future_appointment_edit_activity_toast_message, leftAppointmentTimeHours.toString()), Toast.LENGTH_LONG).show()
                    }
                } else {
                    //Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun alertDialogMessageMethodForAppointmentEdit(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.patient_future_appointment_edit_activity_edit_alert_dialog_title))
        builder.setMessage(getString(R.string.patient_future_appointment_edit_activity_edit_alert_dialog_question))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            checkEditTimeAndEditAppointment()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun checkCancelTimeAndCancelAppointment(){
        db.collection("appointment").document("appointmentInfo").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    lastAppointmentCancelTime =
                        document["appointmentCancelLastDateForPatient"].toString()
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
        appointmentStatus = "canceled by patient"
        db.collection("appointment").document(appointmentId)
            .update("appointmentStatus", appointmentStatus)
            .addOnSuccessListener {

                sendMessageToChaplain()
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
        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
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
}