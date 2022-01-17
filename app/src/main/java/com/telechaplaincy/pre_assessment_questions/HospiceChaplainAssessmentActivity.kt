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
import kotlinx.android.synthetic.main.activity_health_care_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_hospice_chaplain_assessment.*

class HospiceChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var psychologicalIssueYesNo: String = ""
    private var meaningOfLife: String = ""
    private var meaningOfLifeArrayList = ArrayList<String>()
    private var experiencingChallenge: String = ""
    private var partOfSocialCommunity: String = ""
    private var partOfSocialCommunityArrayList = ArrayList<String>()
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospice_chaplain_assessment)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

        topThreeEmotionsSelection()
        meaningOfLifeSelection()
        partOfSocialCommunitySelection()
        takeRadioButtonsInfo()

    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        hospice_top_three_emotions_chip_group.removeAllViews()
        hospice_source_of_strength_chip_group.removeAllViews()
        hospice_are_you_part_of_spiritual_chip_group.removeAllViews()
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
                                        Chip(this@HospiceChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    hospice_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        hospice_top_three_emotions_chip_group.removeView(
                                            chipTopThreeEmotions
                                        )
                                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        partOfSocialCommunityArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!partOfSocialCommunityArrayList.isNullOrEmpty()) {
                            for (k in partOfSocialCommunityArrayList.indices) {
                                if (partOfSocialCommunityArrayList[k] != "Nothing Selected") {
                                    val chipPartOfSocialCommunity =
                                        Chip(this@HospiceChaplainAssessmentActivity)
                                    chipPartOfSocialCommunity.isCloseIconVisible = true
                                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipPartOfSocialCommunity.text =
                                        partOfSocialCommunityArrayList[k]
                                    hospice_source_of_strength_chip_group.addView(
                                        chipPartOfSocialCommunity
                                    )

                                    chipPartOfSocialCommunity.setOnClickListener {
                                        hospice_source_of_strength_chip_group.removeView(
                                            chipPartOfSocialCommunity
                                        )
                                        partOfSocialCommunityArrayList.remove(
                                            chipPartOfSocialCommunity.text
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        meaningOfLifeArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!meaningOfLifeArrayList.isNullOrEmpty()) {
                            for (k in meaningOfLifeArrayList.indices) {
                                if (meaningOfLifeArrayList[k] != "Nothing Selected") {
                                    val chipMeaningOfLife =
                                        Chip(this@HospiceChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    hospice_are_you_part_of_spiritual_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        hospice_are_you_part_of_spiritual_chip_group.removeView(
                                            chipMeaningOfLife
                                        )
                                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["whatReasonBringsYou"].toString() != "") {
                        whatReasonBringsYou = document["whatReasonBringsYou"].toString()
                        if (whatReasonBringsYou != "") {
                            hospice_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["psychologicalIssueYesNo"].toString() != "") {
                        psychologicalIssueYesNo = document["psychologicalIssueYesNo"].toString()
                        if (psychologicalIssueYesNo != "") {
                            if (psychologicalIssueYesNo == "Yes") {
                                hospice_psychological_health_issue_yes_radioButton.isChecked = true
                                hospice_psychological_health_issue_no_radioButton.isChecked = false
                            } else if (psychologicalIssueYesNo == "No") {
                                hospice_psychological_health_issue_yes_radioButton.isChecked = false
                                hospice_psychological_health_issue_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                hospice_experiencing_challenge__affect_radioButton.isChecked =
                                    true
                                hospice_experiencing_challenge__result_radioButton.isChecked =
                                    false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                hospice_experiencing_challenge__affect_radioButton.isChecked =
                                    false
                                hospice_experiencing_challenge__result_radioButton.isChecked =
                                    true
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

    private fun saveDataToFireStore() {
        whatReasonBringsYou =
            hospice_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "psychologicalIssueYesNo" to psychologicalIssueYesNo,
            "experiencingChallenge" to experiencingChallenge,
            "meaningOfLife" to meaningOfLifeArrayList,
            "partOfSocialCommunity" to partOfSocialCommunityArrayList,
            "topThreeEmotions" to topThreeEmotionsArrayList
        )

        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    //part Of Social Community spinner item selection function
    private fun partOfSocialCommunitySelection() {
        val spinnerPartOfSocialCommunity =
            resources.getStringArray(R.array.are_you_part_of_spiritual_array)
        spinner_are_you_part_of_spiritual_hospice.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerPartOfSocialCommunity
        )
        spinner_are_you_part_of_spiritual_hospice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partOfSocialCommunity = spinnerPartOfSocialCommunity[position]
                    val chipPartOfSocialCommunity = Chip(this@HospiceChaplainAssessmentActivity)
                    chipPartOfSocialCommunity.isCloseIconVisible = true
                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (partOfSocialCommunity != "Nothing Selected" && partOfSocialCommunity != "Other") {
                        chipPartOfSocialCommunity.text = partOfSocialCommunity
                        hospice_are_you_part_of_spiritual_chip_group.addView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                    } else if (partOfSocialCommunity == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@HospiceChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@HospiceChaplainAssessmentActivity)
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
                                chipPartOfSocialCommunity.text = selection
                                partOfSocialCommunity = selection
                                hospice_are_you_part_of_spiritual_chip_group.addView(
                                    chipPartOfSocialCommunity
                                )
                                partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }

                    chipPartOfSocialCommunity.setOnClickListener {
                        hospice_are_you_part_of_spiritual_chip_group.removeView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.remove(chipPartOfSocialCommunity.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    //meaning Of Life Selection spinner item selection function
    private fun meaningOfLifeSelection() {
        val spinnerMeaningOfLife =
            resources.getStringArray(R.array.source_of_strength_array)
        spinner_source_of_strength_hospice.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_source_of_strength_hospice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@HospiceChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        hospice_source_of_strength_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@HospiceChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@HospiceChaplainAssessmentActivity)
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
                                meaningOfLife = selection
                                hospice_source_of_strength_chip_group.addView(
                                    chipMeaningOfLife
                                )
                                meaningOfLifeArrayList.add(meaningOfLife)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }
                    chipMeaningOfLife.setOnClickListener {
                        hospice_source_of_strength_chip_group.removeView(chipMeaningOfLife)
                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //top Three Emotions spinner item selection function
    private fun topThreeEmotionsSelection() {
        val spinnerTopThreeEmotions = resources.getStringArray(R.array.top_three_emotions_array)
        spinner_top_three_emotions_hospice.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_hospice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@HospiceChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        hospice_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@HospiceChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@HospiceChaplainAssessmentActivity)
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
                                hospice_top_three_emotions_chip_group.addView(
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
                        hospice_top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        hospice_psychological_health_issue_yes_radioButton.setOnClickListener {
            hospice_psychological_health_issue_no_radioButton.isChecked = false
            psychologicalIssueYesNo =
                hospice_psychological_health_issue_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", psychologicalIssueYesNo)
        }
        hospice_psychological_health_issue_no_radioButton.setOnClickListener {
            hospice_psychological_health_issue_yes_radioButton.isChecked = false
            psychologicalIssueYesNo =
                hospice_psychological_health_issue_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", psychologicalIssueYesNo)
        }
        hospice_experiencing_challenge__affect_radioButton.setOnClickListener {
            hospice_experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge =
                hospice_experiencing_challenge__affect_radioButton.text.toString()
        }
        hospice_experiencing_challenge__result_radioButton.setOnClickListener {
            hospice_experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge =
                hospice_experiencing_challenge__result_radioButton.text.toString()
        }
    }

    override fun onPause() {
        super.onPause()
        saveDataToFireStore()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientFutureAppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }
}