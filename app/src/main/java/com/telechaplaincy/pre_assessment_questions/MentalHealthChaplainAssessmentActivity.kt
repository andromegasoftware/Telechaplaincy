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
import kotlinx.android.synthetic.main.activity_community_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_health_care_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_mental_health_chaplain_assessment.*

class MentalHealthChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var psychologicalIssue: String = ""
    private var significantHealthIssue: String =
        "" //for psychological issues, not for the physical health issues
    private var significantHealthIssueArrayList =
        ArrayList<String>() //for psychological issues, not for the physical health issues
    private var crisisIssue: String = ""
    private var crisisArrayList = ArrayList<String>()
    private var dramaticChange: String = ""
    private var incidentIssue: String = ""
    private var suicideIssue: String = ""
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
        setContentView(R.layout.activity_mental_health_chaplain_assessment)

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
        significantHealthIssuesSelection()
        crisisIssuesSelection()
    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        mental_health_problem_chip_group.removeAllViews()
        mental_health_crisis_chip_group.removeAllViews()
        mental_health_source_of_strength_chip_group.removeAllViews()
        mental_health_are_you_part_of_spiritual_chip_group.removeAllViews()
        mental_health_top_three_emotions_chip_group.removeAllViews()
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
                                        Chip(this@MentalHealthChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    mental_health_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        mental_health_top_three_emotions_chip_group.removeView(
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
                                        Chip(this@MentalHealthChaplainAssessmentActivity)
                                    chipPartOfSocialCommunity.isCloseIconVisible = true
                                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipPartOfSocialCommunity.text =
                                        partOfSocialCommunityArrayList[k]
                                    mental_health_are_you_part_of_spiritual_chip_group.addView(
                                        chipPartOfSocialCommunity
                                    )

                                    chipPartOfSocialCommunity.setOnClickListener {
                                        mental_health_are_you_part_of_spiritual_chip_group.removeView(
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
                                        Chip(this@MentalHealthChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    mental_health_source_of_strength_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        mental_health_source_of_strength_chip_group.removeView(
                                            chipMeaningOfLife
                                        )
                                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["crisisIssue"] != null) {
                        crisisArrayList = document["crisisIssue"] as ArrayList<String>
                        if (!crisisArrayList.isNullOrEmpty()) {
                            for (k in crisisArrayList.indices) {
                                if (crisisArrayList[k] != "Nothing Selected") {
                                    val chipCrisis =
                                        Chip(this@MentalHealthChaplainAssessmentActivity)
                                    chipCrisis.isCloseIconVisible = true
                                    chipCrisis.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipCrisis.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipCrisis.text = crisisArrayList[k]
                                    mental_health_crisis_chip_group.addView(chipCrisis)

                                    chipCrisis.setOnClickListener {
                                        mental_health_crisis_chip_group.removeView(chipCrisis)
                                        crisisArrayList.remove(chipCrisis.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["significantHealthIssue"] != null) {
                        significantHealthIssueArrayList =
                            document["significantHealthIssue"] as ArrayList<String>
                        if (!significantHealthIssueArrayList.isNullOrEmpty()) {
                            for (k in significantHealthIssueArrayList.indices) {
                                if (significantHealthIssueArrayList[k] != "Nothing Selected") {
                                    val chipSignificantHealthIssue =
                                        Chip(this@MentalHealthChaplainAssessmentActivity)
                                    chipSignificantHealthIssue.isCloseIconVisible = true
                                    chipSignificantHealthIssue.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipSignificantHealthIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipSignificantHealthIssue.text =
                                        significantHealthIssueArrayList[k]
                                    mental_health_problem_chip_group.addView(
                                        chipSignificantHealthIssue
                                    )

                                    chipSignificantHealthIssue.setOnClickListener {
                                        mental_health_problem_chip_group.removeView(
                                            chipSignificantHealthIssue
                                        )
                                        significantHealthIssueArrayList.remove(
                                            chipSignificantHealthIssue.text
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (document["whatReasonBringsYou"].toString() != "") {
                        whatReasonBringsYou = document["whatReasonBringsYou"].toString()
                        if (whatReasonBringsYou != "") {
                            mental_health_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["psychologicalIssue"].toString() != "") {
                        psychologicalIssue = document["psychologicalIssue"].toString()
                        if (psychologicalIssue != "") {
                            if (psychologicalIssue == "Yes") {
                                mental_health_issue_yes_radioButton.isChecked = true
                                mental_health_issue_no_radioButton.isChecked = false
                            } else if (psychologicalIssue == "No") {
                                mental_health_issue_yes_radioButton.isChecked = false
                                mental_health_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["dramaticChange"].toString() != "") {
                        dramaticChange =
                            document["dramaticChange"].toString()
                        if (dramaticChange != "") {
                            if (dramaticChange == "Yes") {
                                mental_health_significant_issue_yes_radioButton.isChecked = true
                                mental_health_significant_issue_no_radioButton.isChecked = false
                            } else if (dramaticChange == "No") {
                                mental_health_significant_issue_yes_radioButton.isChecked = false
                                mental_health_significant_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["incidentIssue"].toString() != "") {
                        incidentIssue = document["incidentIssue"].toString()
                        if (incidentIssue != "") {
                            if (incidentIssue == "Yes") {
                                mental_health_incident_issue_yes_radioButton.isChecked = true
                                mental_health_incident_issue_no_radioButton.isChecked = false
                            } else if (incidentIssue == "No") {
                                mental_health_incident_issue_yes_radioButton.isChecked = false
                                mental_health_incident_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["suicideIssue"].toString() != "") {
                        suicideIssue = document["suicideIssue"].toString()
                        if (suicideIssue != "") {
                            if (suicideIssue == "Yes") {
                                mental_health_suicide_issue_yes_radioButton.isChecked = true
                                mental_health_suicide_issue_no_radioButton.isChecked = false
                            } else if (suicideIssue == "No") {
                                mental_health_suicide_issue_yes_radioButton.isChecked = false
                                mental_health_suicide_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["meaningfulResource"].toString() != "") {
                        meaningfulResource = document["meaningfulResource"].toString()
                        if (meaningfulResource != "") {
                            if (meaningfulResource == "Yes") {
                                mental_health_meaningful_issue_yes_radioButton.isChecked = true
                                mental_health_meaningful_issue_no_radioButton.isChecked = false
                            } else if (meaningfulResource == "No") {
                                mental_health_meaningful_issue_yes_radioButton.isChecked = false
                                mental_health_meaningful_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                mental_health_experiencing_challenge__affect_radioButton.isChecked =
                                    true
                                mental_health_experiencing_challenge__result_radioButton.isChecked =
                                    false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                mental_health_experiencing_challenge__affect_radioButton.isChecked =
                                    false
                                mental_health_experiencing_challenge__result_radioButton.isChecked =
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
            mental_health_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "psychologicalIssue" to psychologicalIssue,
            "significantHealthIssue" to significantHealthIssueArrayList,
            "crisisIssue" to crisisArrayList,
            "dramaticChange" to dramaticChange,
            "incidentIssue" to incidentIssue,
            "suicideIssue" to suicideIssue,
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

    //crisis Issues Selection spinner item selection function
    private fun crisisIssuesSelection() {
        val spinnerCrisisIssue =
            resources.getStringArray(R.array.crisis_situation_array)
        spinner_mental_health_crisis.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerCrisisIssue
        )
        spinner_mental_health_crisis.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    crisisIssue = spinnerCrisisIssue[position]
                    val chipCrisisIssue = Chip(this@MentalHealthChaplainAssessmentActivity)
                    chipCrisisIssue.isCloseIconVisible = true
                    chipCrisisIssue.setChipBackgroundColorResource(R.color.colorAccent)
                    chipCrisisIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (crisisIssue != "Nothing Selected" && crisisIssue != "Other") {
                        chipCrisisIssue.text = crisisIssue
                        mental_health_crisis_chip_group.addView(chipCrisisIssue)
                        crisisArrayList.add(crisisIssue)
                    } else if (crisisIssue == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@MentalHealthChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@MentalHealthChaplainAssessmentActivity)
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
                                chipCrisisIssue.text = selection
                                crisisIssue = selection
                                mental_health_crisis_chip_group.addView(
                                    chipCrisisIssue
                                )
                                crisisArrayList.add(crisisIssue)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })
                        builder.show()
                    }
                    chipCrisisIssue.setOnClickListener {
                        mental_health_crisis_chip_group.removeView(chipCrisisIssue)
                        crisisArrayList.remove(chipCrisisIssue.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //significant Health Issues Selection spinner item selection function
    private fun significantHealthIssuesSelection() {
        val spinnerSignificantHealthIssues =
            resources.getStringArray(R.array.psychological_problems_array)
        spinner_mental_health.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerSignificantHealthIssues
        )
        spinner_mental_health.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    significantHealthIssue = spinnerSignificantHealthIssues[position]
                    val chipSignificantHealthIssue =
                        Chip(this@MentalHealthChaplainAssessmentActivity)
                    chipSignificantHealthIssue.isCloseIconVisible = true
                    chipSignificantHealthIssue.setChipBackgroundColorResource(R.color.colorAccent)
                    chipSignificantHealthIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (significantHealthIssue != "Nothing Selected" && significantHealthIssue != "Other") {
                        chipSignificantHealthIssue.text = significantHealthIssue
                        mental_health_problem_chip_group.addView(chipSignificantHealthIssue)
                        significantHealthIssueArrayList.add(significantHealthIssue)
                    } else if (significantHealthIssue == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@MentalHealthChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@MentalHealthChaplainAssessmentActivity)
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
                                chipSignificantHealthIssue.text = selection
                                significantHealthIssue = selection
                                mental_health_problem_chip_group.addView(
                                    chipSignificantHealthIssue
                                )
                                significantHealthIssueArrayList.add(significantHealthIssue)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })
                        builder.show()
                    }
                    chipSignificantHealthIssue.setOnClickListener {
                        mental_health_problem_chip_group.removeView(chipSignificantHealthIssue)
                        significantHealthIssueArrayList.remove(chipSignificantHealthIssue.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //meaning Of Life spinner item selection function
    private fun meaningOfLifeSelection() {
        val spinnerMeaningOfLife = resources.getStringArray(R.array.meaning_of_life_array)
        spinner_source_of_strength_mental_health.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_source_of_strength_mental_health.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@MentalHealthChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        mental_health_source_of_strength_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@MentalHealthChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@MentalHealthChaplainAssessmentActivity)
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
                                mental_health_source_of_strength_chip_group.addView(
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
                        mental_health_source_of_strength_chip_group.removeView(chipMeaningOfLife)
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
        spinner_are_you_part_of_spiritual_mental_health.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerPartOfSocialCommunity
        )
        spinner_are_you_part_of_spiritual_mental_health.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partOfSocialCommunity = spinnerPartOfSocialCommunity[position]
                    val chipPartOfSocialCommunity =
                        Chip(this@MentalHealthChaplainAssessmentActivity)
                    chipPartOfSocialCommunity.isCloseIconVisible = true
                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (partOfSocialCommunity != "Nothing Selected" && partOfSocialCommunity != "Other") {
                        chipPartOfSocialCommunity.text = partOfSocialCommunity
                        mental_health_are_you_part_of_spiritual_chip_group.addView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                    } else if (partOfSocialCommunity == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@MentalHealthChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@MentalHealthChaplainAssessmentActivity)
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
                                mental_health_are_you_part_of_spiritual_chip_group.addView(
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
                        mental_health_are_you_part_of_spiritual_chip_group.removeView(
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
        spinner_top_three_emotions_mental_health.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_mental_health.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@MentalHealthChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        mental_health_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder =
                            AlertDialog.Builder(this@MentalHealthChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@MentalHealthChaplainAssessmentActivity)
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
                                mental_health_top_three_emotions_chip_group.addView(
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
                        mental_health_top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        mental_health_issue_yes_radioButton.setOnClickListener {
            mental_health_issue_no_radioButton.isChecked = false
            psychologicalIssue = mental_health_issue_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        mental_health_issue_no_radioButton.setOnClickListener {
            mental_health_issue_yes_radioButton.isChecked = false
            psychologicalIssue = mental_health_issue_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        mental_health_significant_issue_yes_radioButton.setOnClickListener {
            mental_health_significant_issue_no_radioButton.isChecked = false
            dramaticChange =
                mental_health_significant_issue_yes_radioButton.text.toString()
        }
        mental_health_significant_issue_no_radioButton.setOnClickListener {
            mental_health_significant_issue_yes_radioButton.isChecked = false
            dramaticChange =
                mental_health_significant_issue_no_radioButton.text.toString()
        }

        mental_health_incident_issue_yes_radioButton.setOnClickListener {
            mental_health_incident_issue_no_radioButton.isChecked = false
            incidentIssue = mental_health_incident_issue_yes_radioButton.text.toString()
        }
        mental_health_incident_issue_no_radioButton.setOnClickListener {
            mental_health_incident_issue_yes_radioButton.isChecked = false
            incidentIssue = mental_health_incident_issue_no_radioButton.text.toString()
        }
        mental_health_suicide_issue_yes_radioButton.setOnClickListener {
            mental_health_suicide_issue_no_radioButton.isChecked = false
            suicideIssue = mental_health_suicide_issue_yes_radioButton.text.toString()
        }
        mental_health_suicide_issue_no_radioButton.setOnClickListener {
            mental_health_suicide_issue_yes_radioButton.isChecked = false
            suicideIssue = mental_health_suicide_issue_no_radioButton.text.toString()
        }
        mental_health_meaningful_issue_yes_radioButton.setOnClickListener {
            mental_health_meaningful_issue_no_radioButton.isChecked = false
            meaningfulResource = mental_health_meaningful_issue_yes_radioButton.text.toString()
        }
        mental_health_meaningful_issue_no_radioButton.setOnClickListener {
            mental_health_meaningful_issue_yes_radioButton.isChecked = false
            meaningfulResource = mental_health_meaningful_issue_no_radioButton.text.toString()
        }
        mental_health_experiencing_challenge__affect_radioButton.setOnClickListener {
            mental_health_experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge =
                mental_health_experiencing_challenge__affect_radioButton.text.toString()
        }
        mental_health_experiencing_challenge__result_radioButton.setOnClickListener {
            mental_health_experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge =
                mental_health_experiencing_challenge__result_radioButton.text.toString()
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