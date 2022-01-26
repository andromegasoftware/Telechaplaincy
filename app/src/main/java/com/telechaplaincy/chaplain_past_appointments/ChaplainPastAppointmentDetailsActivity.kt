package com.telechaplaincy.chaplain_past_appointments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.telechaplaincy.notification_page.NotificationModelClass
import kotlinx.android.synthetic.main.activity_chaplain_past_appointment_details.*
import java.text.SimpleDateFormat
import java.util.*

class ChaplainPastAppointmentDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainProfileFieldUserId: String = ""
    private var appointmentTime: String = ""
    private var patientAppointmentTimeZone: String = ""
    private var patientUserId: String = ""
    private var patientProfileImageLink: String = ""
    private var patientProfileFirstName: String = ""
    private var patientProfileLastName: String = ""
    private var doctorNoteForPatient: String = ""
    private var doctorNoteForChaplains: String = ""

    private var appointmentId: String = ""

    private var notificationTokenId: String = ""
    private var title: String = ""
    private var body: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_past_appointment_details)

        appointmentId = intent.getStringExtra("appointment_id").toString()
        chaplain_past_appointment_number_textView.text = appointmentId

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        chaplainProfileFieldUserId = user.uid

        dbSave = db.collection("chaplains").document(chaplainProfileFieldUserId)
            .collection("appointments").document(appointmentId)

        takeAppointmentInfo()

        //call edittext scrollable method here
        editTextBioScrollForPatient()
        editTextBioScrollForChaplain()

        chaplain_past_appointment_save_button.setOnClickListener {
            saveChaplainNotesToDatabase()
        }
    }

    private fun sendMessageToPatient() {

        db.collection("patients").document(patientUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    notificationTokenId = document["notificationTokenId"].toString()
                    title = getString(R.string.chaplain_note_for_patient_message_title)
                    body = getString(R.string.chaplain_note_for_patient_message_body)

                    val sender = FcmNotificationsSender(
                        notificationTokenId,
                        title,
                        body,
                        applicationContext,
                        this
                    )
                    sender.SendNotifications()

                    saveMessageToInboxForPatient()

                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun saveMessageToInboxForPatient() {
        val dbSave = db.collection("patients").document(patientUserId)
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

    private fun saveChaplainNotesToDatabase() {
        chaplain_past_appointment_progressBar.visibility = View.VISIBLE
        chaplain_past_appointment_save_button.isClickable = false
        doctorNoteForPatient = chaplain_past_appointment_note_for_patient_editText.text.toString()
        doctorNoteForChaplains =
            chaplain_past_appointment_note_for_chaplain_editText.text.toString()

        val chaplainNotes = hashMapOf(
            "chaplainNoteForPatient" to doctorNoteForPatient,
            "chaplainNoteForDoctors" to doctorNoteForChaplains
        )

        dbSave.set(chaplainNotes, SetOptions.merge())
            .addOnSuccessListener {
                chaplain_past_appointment_progressBar.visibility = View.GONE
                chaplain_past_appointment_save_button.isClickable = true
                chaplain_past_appointment_save_button.text =
                    getString(R.string.chaplain_past_appointment_save_button_title_after_clicked)

                sendMessageToPatient()

                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

        db.collection("patients").document(patientUserId)
            .collection("appointments").document(appointmentId)
            .set(chaplainNotes, SetOptions.merge())

        db.collection("appointment").document(appointmentId)
            .set(chaplainNotes, SetOptions.merge())
    }

    private fun takeAppointmentInfo() {
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<AppointmentModelClass>()
                    if (result != null) {
                        if (result.patientProfileImageLink != null) {
                            patientProfileImageLink = result.patientProfileImageLink.toString()
                            Picasso.get().load(patientProfileImageLink)
                                .into(past_appointment_imageView_patient_image)
                        }
                        if (result.patientName != null) {
                            patientProfileFirstName = result.patientName.toString()

                        }
                        if (result.patientSurname != null) {
                            patientProfileLastName = result.patientSurname.toString()

                            past_appointment_patient_name_textView.text =
                                "$patientProfileFirstName $patientProfileLastName"
                        }
                        if (result.patientAppointmentTimeZone != null) {
                            patientAppointmentTimeZone = result.patientAppointmentTimeZone!!
                        }

                        if (result.appointmentDate != null) {
                            appointmentTime = result.appointmentDate
                            val dateFormatLocalZone = SimpleDateFormat("EEE, dd.MMM.yyyy")
                            dateFormatLocalZone.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentDate =
                                dateFormatLocalZone.format(Date(appointmentTime.toLong()))
                            chaplain_past_appointment_date_textView.text = appointmentDate

                            val dateFormatTime = SimpleDateFormat("HH:mm aaa z")
                            dateFormatTime.timeZone =
                                TimeZone.getTimeZone(patientAppointmentTimeZone)
                            val appointmentTime =
                                dateFormatTime.format(Date(appointmentTime.toLong()))
                            chaplain_past_appointment_time_textView.text =
                                "$appointmentTime $patientAppointmentTimeZone"
                        }
                        if (result.patientId != null) {
                            patientUserId = result.patientId.toString()
                        }
                        if (result.chaplainNoteForPatient != null) {
                            doctorNoteForPatient = result.chaplainNoteForPatient.toString()
                            if (doctorNoteForPatient != "") {
                                chaplain_past_appointment_note_for_patient_editText.setText(
                                    doctorNoteForPatient
                                )
                            }
                        }
                        if (result.chaplainNoteForDoctors != null) {
                            doctorNoteForChaplains = result.chaplainNoteForDoctors.toString()
                            if (doctorNoteForChaplains != "") {
                                chaplain_past_appointment_note_for_chaplain_editText.setText(
                                    doctorNoteForChaplains
                                )
                            }
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

    //this method is for the edittext chaplain note for patient. it makes edittext scrollable.
    @SuppressLint("ClickableViewAccessibility")
    private fun editTextBioScrollForPatient() {
        chaplain_past_appointment_note_for_patient_editText.isVerticalScrollBarEnabled = true
        chaplain_past_appointment_note_for_patient_editText.overScrollMode = View.OVER_SCROLL_ALWAYS
        chaplain_past_appointment_note_for_patient_editText.scrollBarStyle =
            View.SCROLLBARS_INSIDE_INSET
        chaplain_past_appointment_note_for_patient_editText.movementMethod =
            ScrollingMovementMethod.getInstance()
        chaplain_past_appointment_note_for_patient_editText.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event?.action) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    //this method is for the edittext chaplain note for chaplain. it makes edittext scrollable.
    @SuppressLint("ClickableViewAccessibility")
    private fun editTextBioScrollForChaplain() {
        chaplain_past_appointment_note_for_chaplain_editText.isVerticalScrollBarEnabled = true
        chaplain_past_appointment_note_for_chaplain_editText.overScrollMode = View.OVER_SCROLL_ALWAYS
        chaplain_past_appointment_note_for_chaplain_editText.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        chaplain_past_appointment_note_for_chaplain_editText.movementMethod = ScrollingMovementMethod.getInstance()
        chaplain_past_appointment_note_for_chaplain_editText.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event?.action) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}