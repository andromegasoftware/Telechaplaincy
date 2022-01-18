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
import kotlinx.android.synthetic.main.activity_mental_health_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_palliative_care_chaplain_assessment.*

class PalliativeCareChaplainAssessmentActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var psychologicalIssue: String = ""
    private var incidentIssue: String = ""
    private var meaningfulResource: String = ""
    private var meaningOfLife: String = ""
    private var meaningOfLifeArrayList = ArrayList<String>()
    private var experiencingChallenge: String = ""
    private var partOfSocialCommunity: String = ""
    private var partOfSocialCommunityArrayList = ArrayList<String>()
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_palliative_care_chaplain_assessment)

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
        partOfSocialCommunitySelection()
        meaningOfLifeSelection()

    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        palliative_care_source_of_strength_chip_group.removeAllViews()
        palliative_care_are_you_part_of_spiritual_chip_group.removeAllViews()
        palliative_care_top_three_emotions_chip_group.removeAllViews()
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
                                        Chip(this@PalliativeCareChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    palliative_care_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        palliative_care_top_three_emotions_chip_group.removeView(
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
                                        Chip(this@PalliativeCareChaplainAssessmentActivity)
                                    chipPartOfSocialCommunity.isCloseIconVisible = true
                                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipPartOfSocialCommunity.text =
                                        partOfSocialCommunityArrayList[k]
                                    palliative_care_are_you_part_of_spiritual_chip_group.addView(
                                        chipPartOfSocialCommunity
                                    )

                                    chipPartOfSocialCommunity.setOnClickListener {
                                        palliative_care_are_you_part_of_spiritual_chip_group.removeView(
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
                                        Chip(this@PalliativeCareChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    palliative_care_source_of_strength_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        palliative_care_source_of_strength_chip_group.removeView(
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
                            palliative_care_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["psychologicalIssue"].toString() != "") {
                        psychologicalIssue = document["psychologicalIssue"].toString()
                        if (psychologicalIssue != "") {
                            if (psychologicalIssue == "Yes") {
                                palliative_care_psychological_yes_radioButton.isChecked = true
                                palliative_care_psychological_no_radioButton.isChecked = false
                            } else if (psychologicalIssue == "No") {
                                palliative_care_psychological_yes_radioButton.isChecked = false
                                palliative_care_psychological_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["incidentIssue"].toString() != "") {
                        incidentIssue = document["incidentIssue"].toString()
                        if (incidentIssue != "") {
                            if (incidentIssue == "Yes") {
                                palliative_care_incident_issue_yes_radioButton.isChecked = true
                                palliative_care_incident_issue_no_radioButton.isChecked = false
                            } else if (incidentIssue == "No") {
                                palliative_care_incident_issue_yes_radioButton.isChecked = false
                                palliative_care_incident_issue_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["meaningfulResource"].toString() != "") {
                        meaningfulResource = document["meaningfulResource"].toString()
                        if (meaningfulResource != "") {
                            if (meaningfulResource == "Yes") {
                                palliative_care_meaningful_issue_yes_radioButton.isChecked = true
                                palliative_care_meaningful_issue_no_radioButton.isChecked = false
                            } else if (meaningfulResource == "No") {
                                palliative_care_meaningful_issue_yes_radioButton.isChecked = false
                                palliative_care_meaningful_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                palliative_care_experiencing_challenge__affect_radioButton.isChecked =
                                    true
                                palliative_care_experiencing_challenge__result_radioButton.isChecked =
                                    false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                palliative_care_experiencing_challenge__affect_radioButton.isChecked =
                                    false
                                palliative_care_experiencing_challenge__result_radioButton.isChecked =
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

    override fun onPause() {
        super.onPause()
        saveDataToFireStore()
    }

    private fun saveDataToFireStore() {
        whatReasonBringsYou =
            palliative_care_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "psychologicalIssue" to psychologicalIssue,
            "incidentIssue" to incidentIssue,
            "meaningfulResource" to meaningfulResource,
            "meaningOfLife" to meaningOfLifeArrayList,
            "experiencingChallenge" to experiencingChallenge,
            "partOfSocialCommunity" to partOfSocialCommunityArrayList,
            "topThreeEmotions" to topThreeEmotionsArrayList
        )

        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    //meaning Of Life spinner item selection function
    private fun meaningOfLifeSelection() {
        val spinnerMeaningOfLife = resources.getStringArray(R.array.meaning_of_life_array)
        spinner_source_of_strength_palliative_care.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_source_of_strength_palliative_care.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@PalliativeCareChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        palliative_care_source_of_strength_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@PalliativeCareChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PalliativeCareChaplainAssessmentActivity)
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
                                palliative_care_source_of_strength_chip_group.addView(
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
                        palliative_care_source_of_strength_chip_group.removeView(chipMeaningOfLife)
                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //part Of Social Community spinner item selection function
    private fun partOfSocialCommunitySelection() {
        val spinnerPartOfSocialCommunity =
            resources.getStringArray(R.array.are_you_part_of_spiritual_array)
        spinner_are_you_part_of_spiritual_palliative_care.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerPartOfSocialCommunity
        )
        spinner_are_you_part_of_spiritual_palliative_care.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partOfSocialCommunity = spinnerPartOfSocialCommunity[position]
                    val chipPartOfSocialCommunity =
                        Chip(this@PalliativeCareChaplainAssessmentActivity)
                    chipPartOfSocialCommunity.isCloseIconVisible = true
                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (partOfSocialCommunity != "Nothing Selected" && partOfSocialCommunity != "Other") {
                        chipPartOfSocialCommunity.text = partOfSocialCommunity
                        palliative_care_are_you_part_of_spiritual_chip_group.addView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                    } else if (partOfSocialCommunity == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@PalliativeCareChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PalliativeCareChaplainAssessmentActivity)
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
                                palliative_care_are_you_part_of_spiritual_chip_group.addView(
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
                        palliative_care_are_you_part_of_spiritual_chip_group.removeView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.remove(chipPartOfSocialCommunity.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //top Three Emotions spinner item selection function
    private fun topThreeEmotionsSelection() {
        val spinnerTopThreeEmotions = resources.getStringArray(R.array.top_three_emotions_array)
        spinner_top_three_emotions_palliative_care.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_palliative_care.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@PalliativeCareChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        palliative_care_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@PalliativeCareChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PalliativeCareChaplainAssessmentActivity)
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
                                palliative_care_top_three_emotions_chip_group.addView(
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
                        palliative_care_top_three_emotions_chip_group.removeView(
                            chipTopThreeEmotions
                        )
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        palliative_care_psychological_yes_radioButton.setOnClickListener {
            palliative_care_psychological_no_radioButton.isChecked = false
            psychologicalIssue = palliative_care_psychological_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        palliative_care_psychological_no_radioButton.setOnClickListener {
            palliative_care_psychological_yes_radioButton.isChecked = false
            psychologicalIssue = palliative_care_psychological_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        palliative_care_incident_issue_yes_radioButton.setOnClickListener {
            palliative_care_incident_issue_no_radioButton.isChecked = false
            incidentIssue = palliative_care_incident_issue_yes_radioButton.text.toString()
        }
        palliative_care_incident_issue_no_radioButton.setOnClickListener {
            palliative_care_incident_issue_yes_radioButton.isChecked = false
            incidentIssue = palliative_care_incident_issue_no_radioButton.text.toString()
        }

        palliative_care_meaningful_issue_yes_radioButton.setOnClickListener {
            palliative_care_meaningful_issue_no_radioButton.isChecked = false
            meaningfulResource = palliative_care_meaningful_issue_yes_radioButton.text.toString()
        }
        palliative_care_meaningful_issue_no_radioButton.setOnClickListener {
            palliative_care_meaningful_issue_yes_radioButton.isChecked = false
            meaningfulResource = palliative_care_meaningful_issue_no_radioButton.text.toString()
        }
        palliative_care_experiencing_challenge__affect_radioButton.setOnClickListener {
            palliative_care_experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge =
                palliative_care_experiencing_challenge__affect_radioButton.text.toString()
        }
        palliative_care_experiencing_challenge__result_radioButton.setOnClickListener {
            palliative_care_experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge =
                palliative_care_experiencing_challenge__result_radioButton.text.toString()
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