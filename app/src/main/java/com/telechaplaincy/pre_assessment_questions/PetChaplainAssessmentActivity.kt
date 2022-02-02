package com.telechaplaincy.pre_assessment_questions

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.patient_future_appointments.PatientFutureAppointmentDetailActivity
import kotlinx.android.synthetic.main.activity_palliative_care_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_pet_chaplain_assessment.*

class PetChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var psychologicalIssue: String = ""
    private var comeReason: String = ""
    private var comeReasonArrayList = ArrayList<String>()
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_chaplain_assessment)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

        takeRadioButtonsInfo()
        topThreeEmotionsSelection()
        comeReasonSelection()
    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        what_brings_pet_chip_group.removeAllViews()
        pet_top_three_emotions_chip_group.removeAllViews()
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["topThreeEmotions"] != null) {
                        topThreeEmotionsArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!topThreeEmotionsArrayList.isNullOrEmpty()) {
                            for (k in topThreeEmotionsArrayList.indices) {
                                if (topThreeEmotionsArrayList[k] != "Nothing Selected") {
                                    val chipTopThreeEmotions =
                                        Chip(this@PetChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    pet_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        pet_top_three_emotions_chip_group.removeView(
                                            chipTopThreeEmotions
                                        )
                                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["comeReason"] != null) {
                        comeReasonArrayList =
                            document["comeReason"] as ArrayList<String>
                        if (!comeReasonArrayList.isNullOrEmpty()) {
                            for (k in comeReasonArrayList.indices) {
                                if (comeReasonArrayList[k] != "Nothing Selected") {
                                    val chipPartOfSocialCommunity =
                                        Chip(this@PetChaplainAssessmentActivity)
                                    chipPartOfSocialCommunity.isCloseIconVisible = true
                                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipPartOfSocialCommunity.text =
                                        comeReasonArrayList[k]
                                    what_brings_pet_chip_group.addView(
                                        chipPartOfSocialCommunity
                                    )

                                    chipPartOfSocialCommunity.setOnClickListener {
                                        what_brings_pet_chip_group.removeView(
                                            chipPartOfSocialCommunity
                                        )
                                        comeReasonArrayList.remove(
                                            chipPartOfSocialCommunity.text
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (document["whatReasonBringsYou"].toString() != "") {
                        whatReasonBringsYou = document["whatReasonBringsYou"].toString()
                        if (whatReasonBringsYou != "") {
                            pet_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["psychologicalIssue"].toString() != "") {
                        psychologicalIssue = document["psychologicalIssue"].toString()
                        if (psychologicalIssue != "") {
                            if (psychologicalIssue == "Yes") {
                                pet_incident_issue_yes_radioButton.isChecked = true
                                pet_incident_issue_no_radioButton.isChecked = false
                            } else if (psychologicalIssue == "No") {
                                pet_incident_issue_yes_radioButton.isChecked = false
                                pet_incident_issue_no_radioButton.isChecked = true
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

    override fun onPause() {
        super.onPause()
        saveDataToFireStore()
    }

    private fun saveDataToFireStore() {
        whatReasonBringsYou =
            pet_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "psychologicalIssue" to psychologicalIssue,
            "comeReason" to comeReasonArrayList,
            "topThreeEmotions" to topThreeEmotionsArrayList
        )

        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    //come reason item selection function
    private fun comeReasonSelection() {
        val spinnerMeaningOfLife = resources.getStringArray(R.array.bring_reason_array)
        spinner_what_brings_pet.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_what_brings_pet.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    comeReason = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@PetChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (comeReason != "Nothing Selected" && comeReason != "Other") {
                        chipMeaningOfLife.text = comeReason
                        what_brings_pet_chip_group.addView(chipMeaningOfLife)
                        comeReasonArrayList.add(comeReason)
                    } else if (comeReason == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@PetChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PetChaplainAssessmentActivity)
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.hint = getString(R.string.social_problem_alert_hint)
                        input.inputType = InputType.TYPE_CLASS_TEXT
                        builder.setView(input)

                        // Set up the buttons
                        builder.setPositiveButton(
                            getString(R.string.chaplain_sign_up_alert_ok_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                // Here you get get input text from the Edittext
                                val selection = input.text.toString()

                                //assign input to chip and add chip
                                chipMeaningOfLife.text = selection
                                comeReason = selection
                                what_brings_pet_chip_group.addView(
                                    chipMeaningOfLife
                                )
                                comeReasonArrayList.add(comeReason)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }
                    chipMeaningOfLife.setOnClickListener {
                        what_brings_pet_chip_group.removeView(chipMeaningOfLife)
                        comeReasonArrayList.remove(chipMeaningOfLife.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }


    //top Three Emotions spinner item selection function
    private fun topThreeEmotionsSelection() {
        val spinnerTopThreeEmotions = resources.getStringArray(R.array.top_three_emotions_array)
        spinner_top_three_emotions_pet.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_pet.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@PetChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        pet_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@PetChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PetChaplainAssessmentActivity)
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.hint = getString(R.string.social_problem_alert_hint)
                        input.inputType = InputType.TYPE_CLASS_TEXT
                        builder.setView(input)

                        // Set up the buttons
                        builder.setPositiveButton(
                            getString(R.string.chaplain_sign_up_alert_ok_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                // Here you get get input text from the Edittext
                                val selection = input.text.toString()

                                //assign input to chip and add chip
                                chipTopThreeEmotions.text = selection
                                topThreeEmotions = selection
                                pet_top_three_emotions_chip_group.addView(
                                    chipTopThreeEmotions
                                )
                                topThreeEmotionsArrayList.add(topThreeEmotions)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }
                    chipTopThreeEmotions.setOnClickListener {
                        pet_top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        pet_incident_issue_yes_radioButton.setOnClickListener {
            pet_incident_issue_no_radioButton.isChecked = false
            psychologicalIssue = pet_incident_issue_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        pet_incident_issue_no_radioButton.setOnClickListener {
            pet_incident_issue_yes_radioButton.isChecked = false
            psychologicalIssue = pet_incident_issue_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }
}