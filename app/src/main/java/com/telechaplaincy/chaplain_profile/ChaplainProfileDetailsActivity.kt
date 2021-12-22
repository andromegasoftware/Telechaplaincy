package com.telechaplaincy.chaplain_profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainSignUpSecondPart
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import kotlinx.android.synthetic.main.activity_chaplain_profile_details.*

class ChaplainProfileDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser

    private var chaplainCollectionName: String = ""

    private var chaplainProfileFieldUserId: String = ""
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_profile_details)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        chaplainProfileFieldUserId = user.uid
        chaplainCollectionName = getString(R.string.chaplain_collection)

        dbSave = db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        readChaplainInfo()

        chaplain_profile_details_activity_edit_imageButton.setOnClickListener {
            val intent = Intent(this, ChaplainSignUpSecondPart::class.java)
            intent.putExtra("activity_name", "ChaplainProfileDetailsActivity")
            startActivity(intent)
        }

        chaplain_profile_details_activity_chaplain_resume_textView.setOnClickListener {
            chaplainProfileResume.asUri()?.openInBrowser(this)
        }

    }

    private fun Uri?.openInBrowser(context: Context) {
        this ?: return // Do nothing if uri is null

        val browserIntent = Intent(Intent.ACTION_VIEW, this)
        ContextCompat.startActivity(context, browserIntent, null)
    }

    private fun String?.asUri(): Uri? {
        return try {
            Uri.parse(this)
        } catch (e: Exception) {
            null
        }
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
                            credentialTitleArrayList = result.credentialsTitle
                        }
                        if (!credentialTitleArrayList.isNullOrEmpty()) {
                            for (k in credentialTitleArrayList) {
                                chaplainProfileCredentialTitle += ", $k"
                            }
                            chaplain_profile_details_activity_chaplain_name_textView.text =
                                "$chaplainProfileAddressTitle $chaplainProfileFirstName $chaplainProfileLastName$chaplainProfileCredentialTitle"
                        }
                        if (result.ssn != null) {
                            chaplainProfileFieldSsn = result.ssn.toString()
                            chaplain_profile_details_activity_chaplain_ssn_textView.text =
                                chaplainProfileFieldSsn
                        }
                        if (result.field != null) {
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
                            Log.d("certificate", chaplainProfileCertificate)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}