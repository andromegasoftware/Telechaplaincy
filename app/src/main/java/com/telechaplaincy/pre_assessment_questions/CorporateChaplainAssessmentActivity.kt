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
import kotlinx.android.synthetic.main.activity_college_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_corporate_chaplain_assessment.*

class CorporateChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var significantIssue: String = ""
    private var workIssues: String = ""
    private var workIssuesArrayList = ArrayList<String>()
    private var responsibilityIssue: String = ""
    private var currentExperience: String = ""
    private var currentExperienceArrayList = ArrayList<String>()
    private var workRole: String = ""
    private var workRoleArrayList = ArrayList<String>()
    private var meaningOfLife: String = ""
    private var meaningOfLifeArrayList = ArrayList<String>()
    private var experiencingChallenge: String = ""
    private var partOfSocialCommunity: String = ""
    private var partOfSocialCommunityArrayList = ArrayList<String>()
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corporate_chaplain_assessment)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

        takeRadioButtonsInfo()
        workIssuesSelection()
        currentlyExperienceSelection()
        topThreeEmotionsSelection()
        workRoleSelection()
        partOfSocialCommunitySelection()
        meaningOfLifeSelection()

    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        corporate_related_issues_chip_group.removeAllViews()
        corporate_currently_experiencing_chip_group.removeAllViews()
        corporate_work_role_problem_chip_group.removeAllViews()
        corporate_source_of_strength_chip_group.removeAllViews()
        corporate_are_you_part_of_spiritual_chip_group.removeAllViews()
        corporate_top_three_emotions_chip_group.removeAllViews()
        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["workIssues"] != null) {
                        workIssuesArrayList =
                            document["workIssues"] as ArrayList<String>
                        if (!workIssuesArrayList.isNullOrEmpty()) {
                            for (k in workIssuesArrayList.indices) {
                                if (workIssuesArrayList[k] != "Nothing Selected") {
                                    val chipWorkIssues =
                                        Chip(this@CorporateChaplainAssessmentActivity)
                                    chipWorkIssues.isCloseIconVisible = true
                                    chipWorkIssues.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipWorkIssues.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipWorkIssues.text = workIssuesArrayList[k]
                                    corporate_related_issues_chip_group.addView(
                                        chipWorkIssues
                                    )

                                    chipWorkIssues.setOnClickListener {
                                        corporate_related_issues_chip_group.removeView(
                                            chipWorkIssues
                                        )
                                        workIssuesArrayList.remove(chipWorkIssues.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        topThreeEmotionsArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!topThreeEmotionsArrayList.isNullOrEmpty()) {
                            for (k in topThreeEmotionsArrayList.indices) {
                                if (topThreeEmotionsArrayList[k] != "Nothing Selected") {
                                    val chipTopThreeEmotions =
                                        Chip(this@CorporateChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    corporate_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        corporate_top_three_emotions_chip_group.removeView(
                                            chipTopThreeEmotions
                                        )
                                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["currentExperience"] != null) {
                        currentExperienceArrayList =
                            document["currentExperience"] as ArrayList<String>
                        if (!currentExperienceArrayList.isNullOrEmpty()) {
                            for (k in currentExperienceArrayList.indices) {
                                if (currentExperienceArrayList[k] != "Nothing Selected") {
                                    val chipMajorHealth =
                                        Chip(this@CorporateChaplainAssessmentActivity)
                                    chipMajorHealth.isCloseIconVisible = true
                                    chipMajorHealth.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMajorHealth.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMajorHealth.text = currentExperienceArrayList[k]
                                    corporate_currently_experiencing_chip_group.addView(
                                        chipMajorHealth
                                    )

                                    chipMajorHealth.setOnClickListener {
                                        corporate_currently_experiencing_chip_group.removeView(
                                            chipMajorHealth
                                        )
                                        currentExperienceArrayList.remove(chipMajorHealth.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["workRole"] != null) {
                        workRoleArrayList = document["workRole"] as ArrayList<String>
                        if (!workRoleArrayList.isNullOrEmpty()) {
                            for (k in workRoleArrayList.indices) {
                                if (workRoleArrayList[k] != "Nothing Selected") {
                                    val chipWorkRole =
                                        Chip(this@CorporateChaplainAssessmentActivity)
                                    chipWorkRole.isCloseIconVisible = true
                                    chipWorkRole.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipWorkRole.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipWorkRole.text = workRoleArrayList[k]
                                    corporate_work_role_problem_chip_group.addView(chipWorkRole)

                                    chipWorkRole.setOnClickListener {
                                        corporate_work_role_problem_chip_group.removeView(
                                            chipWorkRole
                                        )
                                        workRoleArrayList.remove(chipWorkRole.text)
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
                                        Chip(this@CorporateChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    corporate_source_of_strength_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        corporate_source_of_strength_chip_group.removeView(
                                            chipMeaningOfLife
                                        )
                                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
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
                                    val chipMeaningOfLife =
                                        Chip(this@CorporateChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = partOfSocialCommunityArrayList[k]
                                    corporate_are_you_part_of_spiritual_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        corporate_are_you_part_of_spiritual_chip_group.removeView(
                                            chipMeaningOfLife
                                        )
                                        partOfSocialCommunityArrayList.remove(chipMeaningOfLife.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["whatReasonBringsYou"].toString() != "") {
                        whatReasonBringsYou = document["whatReasonBringsYou"].toString()
                        if (whatReasonBringsYou != "") {
                            corporate_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }

                    if (document["significantIssue"].toString() != "") {
                        significantIssue = document["significantIssue"].toString()
                        if (significantIssue != "") {
                            if (significantIssue == "Yes") {
                                corporate_chaplain_significant_issues_yes_radioButton.isChecked =
                                    true
                                corporate_chaplain_significant_issues_no_radioButton.isChecked =
                                    false
                            } else if (significantIssue == "No") {
                                corporate_chaplain_significant_issues_yes_radioButton.isChecked =
                                    false
                                corporate_chaplain_significant_issues_no_radioButton.isChecked =
                                    true
                            }
                        }
                    }

                    if (document["responsibilityIssue"].toString() != "") {
                        responsibilityIssue = document["responsibilityIssue"].toString()
                        if (responsibilityIssue != "") {
                            if (responsibilityIssue == "Yes") {
                                corporate_responsibility_issues_yes_radioButton.isChecked = true
                                corporate_responsibility_issues_no_radioButton.isChecked = false
                            } else if (responsibilityIssue == "No") {
                                corporate_responsibility_issues_yes_radioButton.isChecked = false
                                corporate_responsibility_issues_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                corporate_experiencing_challenge__affect_radioButton.isChecked =
                                    true
                                corporate_experiencing_challenge__result_radioButton.isChecked =
                                    false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                corporate_experiencing_challenge__affect_radioButton.isChecked =
                                    false
                                corporate_experiencing_challenge__result_radioButton.isChecked =
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
            corporate_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "significantIssue" to significantIssue,
            "workIssues" to workIssuesArrayList,
            "responsibilityIssue" to responsibilityIssue,
            "currentExperience" to currentExperienceArrayList,
            "workRole" to workRoleArrayList,
            "workIssues" to workIssuesArrayList,
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

    //top Three Emotions spinner item selection function
    private fun workIssuesSelection() {
        val spinnerWorkIssues = resources.getStringArray(R.array.issues_array)
        spinner_related_issues_corporate.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerWorkIssues
        )
        spinner_related_issues_corporate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    workIssues = spinnerWorkIssues[position]
                    val chipWorkIssue = Chip(this@CorporateChaplainAssessmentActivity)
                    chipWorkIssue.isCloseIconVisible = true
                    chipWorkIssue.setChipBackgroundColorResource(R.color.colorAccent)
                    chipWorkIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (workIssues != "Nothing Selected" && workIssues != "Other") {
                        chipWorkIssue.text = workIssues
                        corporate_related_issues_chip_group.addView(chipWorkIssue)
                        workIssuesArrayList.add(workIssues)
                    } else if (workIssues == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CorporateChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CorporateChaplainAssessmentActivity)
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
                                chipWorkIssue.text = selection
                                workIssues = selection
                                corporate_related_issues_chip_group.addView(
                                    chipWorkIssue
                                )
                                workIssuesArrayList.add(workIssues)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }
                    chipWorkIssue.setOnClickListener {
                        corporate_related_issues_chip_group.removeView(chipWorkIssue)
                        workIssuesArrayList.remove(chipWorkIssue.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //top Three Emotions spinner item selection function
    private fun topThreeEmotionsSelection() {
        val spinnerTopThreeEmotions = resources.getStringArray(R.array.top_three_emotions_array)
        spinner_top_three_emotions_corporate.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_corporate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@CorporateChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        corporate_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CorporateChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CorporateChaplainAssessmentActivity)
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
                                corporate_top_three_emotions_chip_group.addView(
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
                        corporate_top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //current Experience spinner item selection function
    private fun currentlyExperienceSelection() {
        val spinnerCurrentlyExperience = resources.getStringArray(R.array.work_issues_problem_array)
        spinner_currently_experiencing_corporate.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerCurrentlyExperience
        )
        spinner_currently_experiencing_corporate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentExperience = spinnerCurrentlyExperience[position]
                    val chipCurrentExperience = Chip(this@CorporateChaplainAssessmentActivity)
                    chipCurrentExperience.isCloseIconVisible = true
                    chipCurrentExperience.setChipBackgroundColorResource(R.color.colorAccent)
                    chipCurrentExperience.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (currentExperience != "Nothing Selected" && currentExperience != "Other") {
                        chipCurrentExperience.text = currentExperience
                        corporate_currently_experiencing_chip_group.addView(chipCurrentExperience)
                        currentExperienceArrayList.add(currentExperience)
                    } else if (currentExperience == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CorporateChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CorporateChaplainAssessmentActivity)
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
                                chipCurrentExperience.text = selection
                                currentExperience = selection
                                corporate_currently_experiencing_chip_group.addView(
                                    chipCurrentExperience
                                )
                                currentExperienceArrayList.add(currentExperience)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }
                    chipCurrentExperience.setOnClickListener {
                        corporate_currently_experiencing_chip_group.removeView(chipCurrentExperience)
                        currentExperienceArrayList.remove(chipCurrentExperience.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //work role spinner item selection function
    private fun workRoleSelection() {
        val spinnerWorkRole = resources.getStringArray(R.array.family_role_problem_array)
        spinner_work_role_problem_corporate.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerWorkRole
        )
        spinner_work_role_problem_corporate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    workRole = spinnerWorkRole[position]
                    val chipWorkRoleArrayList = Chip(this@CorporateChaplainAssessmentActivity)
                    chipWorkRoleArrayList.isCloseIconVisible = true
                    chipWorkRoleArrayList.setChipBackgroundColorResource(R.color.colorAccent)
                    chipWorkRoleArrayList.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (workRole != "Nothing Selected" && workRole != "Other") {
                        chipWorkRoleArrayList.text = workRole
                        corporate_work_role_problem_chip_group.addView(chipWorkRoleArrayList)
                        workRoleArrayList.add(workRole)
                    } else if (workRole == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CorporateChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CorporateChaplainAssessmentActivity)
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
                                chipWorkRoleArrayList.text = selection
                                workRole = selection
                                corporate_work_role_problem_chip_group.addView(chipWorkRoleArrayList)
                                workRoleArrayList.add(workRole)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }

                    chipWorkRoleArrayList.setOnClickListener {
                        corporate_work_role_problem_chip_group.removeView(chipWorkRoleArrayList)
                        workRoleArrayList.remove(chipWorkRoleArrayList.text)
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
        spinner_are_you_part_of_spiritual_corporate.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerPartOfSocialCommunity
        )
        spinner_are_you_part_of_spiritual_corporate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partOfSocialCommunity = spinnerPartOfSocialCommunity[position]
                    val chipPartOfSocialCommunity = Chip(this@CorporateChaplainAssessmentActivity)
                    chipPartOfSocialCommunity.isCloseIconVisible = true
                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (partOfSocialCommunity != "Nothing Selected" && partOfSocialCommunity != "Other") {
                        chipPartOfSocialCommunity.text = partOfSocialCommunity
                        corporate_are_you_part_of_spiritual_chip_group.addView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                    } else if (partOfSocialCommunity == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CorporateChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CorporateChaplainAssessmentActivity)
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
                                corporate_are_you_part_of_spiritual_chip_group.addView(
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
                        corporate_are_you_part_of_spiritual_chip_group.removeView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.remove(chipPartOfSocialCommunity.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //meaning Of Life spinner item selection function
    private fun meaningOfLifeSelection() {
        val spinnerMeaningOfLife = resources.getStringArray(R.array.source_of_strength_array)
        spinner_source_of_strength_corporate.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_source_of_strength_corporate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@CorporateChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        corporate_source_of_strength_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CorporateChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CorporateChaplainAssessmentActivity)
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
                                corporate_source_of_strength_chip_group.addView(
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
                        corporate_source_of_strength_chip_group.removeView(chipMeaningOfLife)
                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        corporate_chaplain_significant_issues_yes_radioButton.setOnClickListener {
            corporate_chaplain_significant_issues_no_radioButton.isChecked = false
            significantIssue = corporate_chaplain_significant_issues_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        corporate_chaplain_significant_issues_no_radioButton.setOnClickListener {
            corporate_chaplain_significant_issues_yes_radioButton.isChecked = false
            significantIssue = corporate_chaplain_significant_issues_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        corporate_responsibility_issues_yes_radioButton.setOnClickListener {
            corporate_responsibility_issues_no_radioButton.isChecked = false
            responsibilityIssue = corporate_responsibility_issues_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        corporate_responsibility_issues_no_radioButton.setOnClickListener {
            corporate_responsibility_issues_yes_radioButton.isChecked = false
            responsibilityIssue = corporate_responsibility_issues_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        corporate_experiencing_challenge__affect_radioButton.setOnClickListener {
            corporate_experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge =
                corporate_experiencing_challenge__affect_radioButton.text.toString()
        }
        corporate_experiencing_challenge__result_radioButton.setOnClickListener {
            corporate_experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge =
                corporate_experiencing_challenge__result_radioButton.text.toString()
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