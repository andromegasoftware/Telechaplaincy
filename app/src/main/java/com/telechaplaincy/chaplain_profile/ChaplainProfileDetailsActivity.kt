package com.telechaplaincy.chaplain_profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.telechaplaincy.all_chaplains.AllChaplainsListActivity
import com.telechaplaincy.all_chaplains_wait_to_confirm.AllChaplainsWaitConfirmActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainSignUpSecondPart
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.cloud_message.FcmNotificationsSender
import com.telechaplaincy.mail_and_message_send_package.MailSendClass
import com.telechaplaincy.notification_page.NotificationModelClass
import kotlinx.android.synthetic.main.activity_chaplain_profile_details.*


class ChaplainProfileDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainCollectionName: String = ""

    private var chaplainProfileFieldUserId: String = ""
    private var chaplainUserIdSentByAdmin: String = ""
    private var chaplainProfileImageLink: String = ""
    private var chaplainProfileFirstName: String = ""
    private var chaplainProfileLastName: String = ""
    private var chaplainProfileAddressTitle: String = ""
    private var chaplainProfileCredentialTitle: String = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainProfileFieldSsn: String = ""
    private var chaplainProfileFieldChaplainField: String = ""
    private var chaplainFieldArrayList = ArrayList<String>()
    private var chaplainProfileFieldEmail: String = ""
    private var chaplainProfileFieldPhone: String = ""
    private var chaplainProfileFieldBirthDate: String = ""
    private var chaplainProfileFieldExperience: String = ""
    private var chaplainProfileFieldChaplainEthnic: String = ""
    private var ethnicArrayList = ArrayList<String>()
    private var chaplainProfileFieldChaplainFaith: String = ""
    private var faithArrayList = ArrayList<String>()
    private var chaplainProfileFieldEducation: String = ""
    private var chaplainProfileFieldPreferredLanguage: String = ""
    private var chaplainProfileFieldOtherLanguage: String = ""
    private var otherLanguagesArrayList = ArrayList<String>()
    private var chaplainProfileFieldOrdainedName: String = ""
    private var chaplainProfileFieldOrdainedPeriod: String = ""
    private var chaplainProfileAdditionalCredentials: String = ""
    private var chaplainProfileExplanation: String = ""
    private var chaplainProfileResume: String = ""
    private var chaplainProfileCertificate: String = ""
    private var activityOpenCode: String = ""
    private var notificationTokenId: String = ""
    private var title: String = ""
    private var body: String = ""
    private var chaplainPhoneNumber = ""

    private var mailSendClass = MailSendClass()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_profile_details)


        chaplain_confirm_layout.visibility = View.GONE

        chaplainUserIdSentByAdmin = intent.getStringExtra("chaplain_id_send_by_admin").toString()
        activityOpenCode = intent.getStringExtra("activity_open_code").toString()
        Log.d("uid_chaplain", activityOpenCode)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        if (chaplainUserIdSentByAdmin != "null") {
            chaplainProfileFieldUserId = chaplainUserIdSentByAdmin
            chaplain_profile_details_activity_edit_imageButton.visibility = View.GONE
            if (activityOpenCode != "null") {
                chaplain_confirm_layout.visibility = View.VISIBLE
            }
        } else {
            chaplainProfileFieldUserId = user.uid
        }
        chaplainCollectionName = getString(R.string.chaplain_collection)

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        readChaplainInfo()

        chaplain_profile_details_activity_edit_imageButton.setOnClickListener {
            val intent = Intent(this, ChaplainSignUpSecondPart::class.java)
            intent.putExtra("activity_name", "ChaplainProfileDetailsActivity")
            startActivity(intent)
        }
        chaplain_profile_details_activity_chaplain_certificate_textView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(chaplainProfileCertificate), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val newIntent = Intent.createChooser(intent, "Open File")
            try {
                startActivity(newIntent)
            } catch (e: ActivityNotFoundException) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
        chaplain_profile_details_activity_chaplain_resume_textView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(chaplainProfileResume), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val newIntent = Intent.createChooser(intent, "Open File")
            try {
                startActivity(newIntent)
            } catch (e: ActivityNotFoundException) {
                // Instruct the user to install a PDF reader here, or something
            }
        }

        chaplain_reject_button.setOnClickListener {
            chaplainRejectMethod()
        }

        chaplain_confirm_button.setOnClickListener {
            chaplainConfirmMethod()
            sendMessageToChaplain()
            sendMailToChaplain()
        }

    }

    private fun chaplainConfirmMethod() {
        chaplain_confirm_button.isClickable = false
        chaplain_account_status_progress_bar.visibility = View.VISIBLE
        val data = hashMapOf(
            "accountStatus" to "2"
        )
        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {

                chaplain_confirm_button.isClickable = true
                chaplain_account_status_progress_bar.visibility = View.GONE
                Toast.makeText(
                    this,
                    getString(R.string.chaplain_profile_details_page_confirm_toast_message),
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAG", "DocumentSnapshot successfully written!")
                val intent = Intent(this, AllChaplainsWaitConfirmActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun sendMailToChaplain() {
        val body = getString(R.string.chaplain_confirm_message_body)
        mailSendClass.sendMailToChaplain(
            getString(R.string.chaplain_confirm_message_title),
            body, chaplainProfileFieldEmail
        )
    }

    private fun sendMessageToChaplain() {

        db.collection("chaplains").document(chaplainProfileFieldUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    notificationTokenId = document["notificationTokenId"].toString()
                    chaplainPhoneNumber = document["phone"].toString()
                    //Log.d("notificationTokenId", notificationTokenId)

                    title = getString(R.string.chaplain_confirm_message_title)
                    body = getString(R.string.chaplain_confirm_message_body)

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

        val message =
            NotificationModelClass(title, body, null, null, null, dateSent, messageId, false)

        dbSave.set(message, SetOptions.merge())
    }

    private fun chaplainRejectMethod() {
        chaplain_reject_button.isClickable = false
        chaplain_account_status_progress_bar.visibility = View.VISIBLE
        val data = hashMapOf(
            "accountStatus" to "3"
        )
        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {

                chaplain_reject_button.isClickable = true
                chaplain_account_status_progress_bar.visibility = View.GONE
                Toast.makeText(
                    this,
                    getString(R.string.chaplain_profile_details_page_reject_toast_message),
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAG", "DocumentSnapshot successfully written!")
                val intent = Intent(this, AllChaplainsWaitConfirmActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun readChaplainInfo() {
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val result = document.toObject<ChaplainUserProfile>()
                    if (result != null) {
                        if (result.profileImageLink != null) {
                            chaplainProfileImageLink = result.profileImageLink.toString()
                            Picasso.get().load(chaplainProfileImageLink)
                                .placeholder(R.drawable.ic_baseline_account_circle_24)
                                .error(R.drawable.ic_baseline_account_circle_24)
                                .into(chaplain_profile_details_activity_profile_image)
                        }
                        if (result.name != null) {
                            chaplainProfileFirstName = result.name.toString()

                        }
                        if (result.surname != null) {
                            chaplainProfileLastName = result.surname.toString()
                        }
                        if (result.addressingTitle != null) {
                            chaplainProfileAddressTitle = result.addressingTitle.toString()
                        }
                        if (result.credentialsTitle != null) {
                            credentialTitleArrayList.clear() //this code for emptying the array before reading data
                            chaplainProfileCredentialTitle =
                                "" //this is also same to empty string. because when app enter the resume method, it reads the data again and write same data again. This prevents this problem
                            credentialTitleArrayList = result.credentialsTitle
                        }
                        if (!credentialTitleArrayList.isNullOrEmpty()) {
                            for (k in credentialTitleArrayList) {
                                chaplainProfileCredentialTitle += ", $k"
                            }
                        }
                        //we write the info that is addressingTitle, first name and last name, cridentialTitles on text_view
                        chaplain_profile_details_activity_chaplain_name_textView.text =
                            "$chaplainProfileAddressTitle $chaplainProfileFirstName $chaplainProfileLastName$chaplainProfileCredentialTitle"

                        if (result.ssn != null) {
                            chaplainProfileFieldSsn = result.ssn.toString()
                            chaplain_profile_details_activity_chaplain_ssn_textView.text =
                                chaplainProfileFieldSsn
                        }
                        if (result.field != null) {
                            chaplainFieldArrayList.clear()
                            chaplainProfileFieldChaplainField = ""
                            chaplainFieldArrayList = result.field
                            if (!chaplainFieldArrayList.isNullOrEmpty()) {
                                for (k in chaplainFieldArrayList) {
                                    chaplainProfileFieldChaplainField += "$k "
                                }
                                chaplain_profile_details_activity_chaplain_field_textView.text =
                                    chaplainProfileFieldChaplainField
                            }
                        }
                        if (result.email != null) {
                            chaplainProfileFieldEmail = result.email.toString()
                            chaplain_profile_details_activity_chaplain_email_textView.text =
                                chaplainProfileFieldEmail
                        }
                        if (result.phone != null) {
                            chaplainProfileFieldPhone = result.phone.toString()
                            chaplain_profile_details_activity_chaplain_phone_textView.text =
                                chaplainProfileFieldPhone
                        }
                        if (result.birthDate != null) {
                            chaplainProfileFieldBirthDate = result.birthDate.toString()
                            chaplain_profile_details_activity_chaplain_birth_date_textView.text =
                                chaplainProfileFieldBirthDate
                        }
                        if (result.experience != null) {
                            chaplainProfileFieldExperience = result.experience.toString()
                            chaplain_profile_details_activity_chaplain_experience_textView.text =
                                chaplainProfileFieldExperience
                        }
                        if (result.ethnic != null) {
                            ethnicArrayList.clear()
                            chaplainProfileFieldChaplainEthnic = ""
                            ethnicArrayList = result.ethnic
                            if (!ethnicArrayList.isNullOrEmpty()) {
                                for (k in ethnicArrayList) {
                                    chaplainProfileFieldChaplainEthnic += "$k "
                                }
                                chaplain_profile_details_activity_chaplain_ethnicity_textView.text =
                                    chaplainProfileFieldChaplainEthnic
                            }
                        }
                        if (result.faith != null) {
                            faithArrayList.clear()
                            chaplainProfileFieldChaplainFaith = ""
                            faithArrayList = result.faith
                            if (!faithArrayList.isNullOrEmpty()) {
                                for (k in faithArrayList) {
                                    chaplainProfileFieldChaplainFaith += "$k "
                                }
                                chaplain_profile_details_activity_chaplain_faith_textView.text =
                                    chaplainProfileFieldChaplainFaith
                            }
                        }
                        if (result.education != null) {
                            chaplainProfileFieldEducation = result.education.toString()
                            if (chaplainProfileFieldEducation != "null") {
                                chaplain_profile_details_activity_chaplain_education_textView.text =
                                    chaplainProfileFieldEducation
                            }
                        }
                        if (result.language != null) {
                            chaplainProfileFieldPreferredLanguage = result.language.toString()
                            if (chaplainProfileFieldPreferredLanguage != "null") {
                                chaplain_profile_details_activity_chaplain_language_textView.text =
                                    chaplainProfileFieldPreferredLanguage
                            }
                        }
                        if (result.otherLanguages != null) {
                            otherLanguagesArrayList.clear()
                            chaplainProfileFieldOtherLanguage = ""
                            otherLanguagesArrayList = result.otherLanguages
                            if (!otherLanguagesArrayList.isNullOrEmpty()) {
                                for (k in otherLanguagesArrayList) {
                                    chaplainProfileFieldOtherLanguage += "$k "
                                }
                                chaplain_profile_details_activity_chaplain_additional_languages_textView.text =
                                    chaplainProfileFieldOtherLanguage
                            }
                        }
                        if (result.ordainedName != null) {
                            chaplainProfileFieldOrdainedName = result.ordainedName.toString()
                            if (chaplainProfileFieldOrdainedName != "null") {
                                chaplain_profile_details_activity_chaplain_ordained_textView.text =
                                    chaplainProfileFieldOrdainedName
                            }
                        }
                        if (result.ordainedPeriod != null) {
                            chaplainProfileFieldOrdainedPeriod = result.ordainedPeriod.toString()
                            if (chaplainProfileFieldOrdainedPeriod != "null") {
                                chaplain_profile_details_activity_chaplain_ordained_period_textView.text =
                                    chaplainProfileFieldOrdainedPeriod
                            }
                        }
                        if (result.additionalCredential != null) {
                            chaplainProfileAdditionalCredentials =
                                result.additionalCredential.toString()
                            if (chaplainProfileAdditionalCredentials != "null") {
                                chaplain_profile_details_activity_chaplain_additional_credentials_textView.text =
                                    chaplainProfileAdditionalCredentials
                            }
                        }
                        if (result.bio != null) {
                            chaplainProfileExplanation = result.bio.toString()
                            if (chaplainProfileExplanation != "null") {
                                chaplain_profile_details_activity_chaplain_bio_textView.text =
                                    chaplainProfileExplanation
                            }
                        }
                        if (result.certificateUrl != null) {
                            chaplainProfileCertificate = result.certificateUrl.toString()
                            //Log.d("certificate", chaplainProfileCertificate)
                        }
                        if (result.cvUrl != null) {
                            chaplainProfileResume = result.cvUrl.toString()
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

    override fun onResume() {
        super.onResume()
        readChaplainInfo()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (chaplainUserIdSentByAdmin != "null") {
            if (activityOpenCode != "null") {
                val intent = Intent(this, AllChaplainsWaitConfirmActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, AllChaplainsListActivity::class.java)
                startActivity(intent)
                finish()
            }

        } else {
            val intent = Intent(this, ChaplainProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}