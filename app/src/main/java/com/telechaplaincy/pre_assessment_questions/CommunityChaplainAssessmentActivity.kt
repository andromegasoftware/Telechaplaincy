package com.telechaplaincy.pre_assessment_questions

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
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
import kotlinx.android.synthetic.main.activity_chaplain_sign_up_second_part.*
import kotlinx.android.synthetic.main.activity_community_chaplain_assessment.*

class CommunityChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var significantHealthIssue: String = ""
    private var physicalAndSpiritualResponsibility: String = ""
    private var familyIssue: String = ""
    private var familyIssueArrayList = ArrayList<String>()
    private var socialIssue: String = ""
    private var socialIssueArrayList = ArrayList<String>()
    private var familyRole: String = ""
    private var familyRoleArrayList = ArrayList<String>()
    private var meaningOfLife: String = ""
    private var meaningOfLifeArrayList = ArrayList<String>()
    private var experiencingChallenge: String = ""
    private var partOfSocialCommunity: String = ""
    private var partOfSocialCommunityArrayList = ArrayList<String>()
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_chaplain_assessment)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

        familyProblemSelection()
        socialProblemSelection()
        familyRoleSelection()
        meaningOfLifeSelection()
        partOfSocialCommunitySelection()
        topThreeEmotionsSelection()
        takeRadioButtonsInfo()
    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        family_problem_chip_group.removeAllViews()
        social_problem_chip_group.removeAllViews()
        family_role_problem_chip_group.removeAllViews()
        meaning_of_life_chip_group.removeAllViews()
        are_you_part_of_spiritual_chip_group.removeAllViews()
        top_three_emotions_chip_group.removeAllViews()
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
                                        Chip(this@CommunityChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    top_three_emotions_chip_group.addView(chipTopThreeEmotions)

                                    chipTopThreeEmotions.setOnClickListener {
                                        top_three_emotions_chip_group.removeView(
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
                                        Chip(this@CommunityChaplainAssessmentActivity)
                                    chipPartOfSocialCommunity.isCloseIconVisible = true
                                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipPartOfSocialCommunity.text =
                                        partOfSocialCommunityArrayList[k]
                                    are_you_part_of_spiritual_chip_group.addView(
                                        chipPartOfSocialCommunity
                                    )

                                    chipPartOfSocialCommunity.setOnClickListener {
                                        are_you_part_of_spiritual_chip_group.removeView(
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
                                        Chip(this@CommunityChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    meaning_of_life_chip_group.addView(chipMeaningOfLife)

                                    chipMeaningOfLife.setOnClickListener {
                                        meaning_of_life_chip_group.removeView(chipMeaningOfLife)
                                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["familyRoles"] != null) {
                        familyRoleArrayList = document["familyRoles"] as ArrayList<String>
                        if (!familyRoleArrayList.isNullOrEmpty()) {
                            for (k in familyRoleArrayList.indices) {
                                if (familyRoleArrayList[k] != "Nothing Selected") {
                                    val chipFamilyRole =
                                        Chip(this@CommunityChaplainAssessmentActivity)
                                    chipFamilyRole.isCloseIconVisible = true
                                    chipFamilyRole.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipFamilyRole.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipFamilyRole.text = familyRoleArrayList[k]
                                    family_role_problem_chip_group.addView(chipFamilyRole)

                                    chipFamilyRole.setOnClickListener {
                                        family_role_problem_chip_group.removeView(chipFamilyRole)
                                        familyRoleArrayList.remove(chipFamilyRole.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["socialIssues"] != null) {
                        socialIssueArrayList = document["socialIssues"] as ArrayList<String>
                        if (!socialIssueArrayList.isNullOrEmpty()) {
                            for (k in socialIssueArrayList.indices) {
                                if (socialIssueArrayList[k] != "Nothing Selected") {
                                    val chipSocialIssue =
                                        Chip(this@CommunityChaplainAssessmentActivity)
                                    chipSocialIssue.isCloseIconVisible = true
                                    chipSocialIssue.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipSocialIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipSocialIssue.text = socialIssueArrayList[k]
                                    social_problem_chip_group.addView(chipSocialIssue)

                                    chipSocialIssue.setOnClickListener {
                                        social_problem_chip_group.removeView(chipSocialIssue)
                                        socialIssueArrayList.remove(chipSocialIssue.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["familyIssues"] != null) {
                        familyIssueArrayList = document["familyIssues"] as ArrayList<String>
                        if (!familyIssueArrayList.isNullOrEmpty()) {
                            for (k in familyIssueArrayList.indices) {
                                if (familyIssueArrayList[k] != "Nothing Selected") {
                                    val chipFamilyRole =
                                        Chip(this@CommunityChaplainAssessmentActivity)
                                    chipFamilyRole.isCloseIconVisible = true
                                    chipFamilyRole.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipFamilyRole.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipFamilyRole.text = familyIssueArrayList[k]
                                    family_problem_chip_group.addView(chipFamilyRole)

                                    chipFamilyRole.setOnClickListener {
                                        family_problem_chip_group.removeView(chipFamilyRole)
                                        familyIssueArrayList.remove(chipFamilyRole.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["whatReasonBringsYou"].toString() != "") {
                        whatReasonBringsYou = document["whatReasonBringsYou"].toString()
                        if (whatReasonBringsYou != "") {
                            community_chaplain_assessment_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["significantHealthIssue"].toString() != "") {
                        significantHealthIssue = document["significantHealthIssue"].toString()
                        if (significantHealthIssue != "") {
                            if (significantHealthIssue == "Yes") {
                                significant_health_issue_yes_radioButton.isChecked = true
                                significant_health_issue_no_radioButton.isChecked = false
                            } else if (significantHealthIssue == "No") {
                                significant_health_issue_yes_radioButton.isChecked = false
                                significant_health_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["physicalAndSpiritualResponsibility"].toString() != "") {
                        physicalAndSpiritualResponsibility =
                            document["physicalAndSpiritualResponsibility"].toString()
                        if (physicalAndSpiritualResponsibility != "") {
                            if (physicalAndSpiritualResponsibility == "Yes") {
                                medical_issue_yes_radioButton.isChecked = true
                                medical_issue_no_radioButton.isChecked = false
                            } else if (physicalAndSpiritualResponsibility == "No") {
                                medical_issue_yes_radioButton.isChecked = false
                                medical_issue_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                experiencing_challenge__affect_radioButton.isChecked = true
                                experiencing_challenge__result_radioButton.isChecked = false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                experiencing_challenge__affect_radioButton.isChecked = false
                                experiencing_challenge__result_radioButton.isChecked = true
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
            community_chaplain_assessment_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "significantHealthIssue" to significantHealthIssue,
            "physicalAndSpiritualResponsibility" to physicalAndSpiritualResponsibility,
            "experiencingChallenge" to experiencingChallenge,
            "familyIssues" to familyIssueArrayList,
            "socialIssues" to socialIssueArrayList,
            "familyRoles" to familyRoleArrayList,
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

    private fun takeRadioButtonsInfo() {

        significant_health_issue_yes_radioButton.setOnClickListener {
            significant_health_issue_no_radioButton.isChecked = false
            significantHealthIssue = significant_health_issue_yes_radioButton.text.toString()
            Log.d("significantHealthIssue", significantHealthIssue)
        }
        significant_health_issue_no_radioButton.setOnClickListener {
            significant_health_issue_yes_radioButton.isChecked = false
            significantHealthIssue = significant_health_issue_no_radioButton.text.toString()
            Log.d("significantHealthIssue", significantHealthIssue)
        }

        medical_issue_yes_radioButton.setOnClickListener {
            medical_issue_no_radioButton.isChecked = false
            physicalAndSpiritualResponsibility =
                significant_health_issue_yes_radioButton.text.toString()
        }
        medical_issue_no_radioButton.setOnClickListener {
            medical_issue_yes_radioButton.isChecked = false
            physicalAndSpiritualResponsibility =
                significant_health_issue_no_radioButton.text.toString()
        }

        experiencing_challenge__affect_radioButton.setOnClickListener {
            experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge = experiencing_challenge__affect_radioButton.text.toString()
        }
        experiencing_challenge__result_radioButton.setOnClickListener {
            experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge = experiencing_challenge__result_radioButton.text.toString()
        }
    }

    //top Three Emotions spinner item selection function
    private fun topThreeEmotionsSelection() {
        val spinnerTopThreeEmotions = resources.getStringArray(R.array.top_three_emotions_array)
        spinner_top_three_emotions.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@CommunityChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CommunityChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CommunityChaplainAssessmentActivity)
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
                                top_three_emotions_chip_group.addView(chipTopThreeEmotions)
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
                        top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
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
        spinner_are_you_part_of_spiritual.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerPartOfSocialCommunity
        )
        spinner_are_you_part_of_spiritual.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partOfSocialCommunity = spinnerPartOfSocialCommunity[position]
                    val chipPartOfSocialCommunity = Chip(this@CommunityChaplainAssessmentActivity)
                    chipPartOfSocialCommunity.isCloseIconVisible = true
                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (partOfSocialCommunity != "Nothing Selected" && partOfSocialCommunity != "Other") {
                        chipPartOfSocialCommunity.text = partOfSocialCommunity
                        are_you_part_of_spiritual_chip_group.addView(chipPartOfSocialCommunity)
                        partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                    } else if (partOfSocialCommunity == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CommunityChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CommunityChaplainAssessmentActivity)
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
                                are_you_part_of_spiritual_chip_group.addView(
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
                        are_you_part_of_spiritual_chip_group.removeView(chipPartOfSocialCommunity)
                        partOfSocialCommunityArrayList.remove(chipPartOfSocialCommunity.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    //meaning Of Life spinner item selection function
    private fun meaningOfLifeSelection() {
        val spinnerMeaningOfLife = resources.getStringArray(R.array.meaning_of_life_array)
        spinner_meaning_of_life.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_meaning_of_life.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@CommunityChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        meaning_of_life_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CommunityChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CommunityChaplainAssessmentActivity)
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
                                meaning_of_life_chip_group.addView(chipMeaningOfLife)
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
                        meaning_of_life_chip_group.removeView(chipMeaningOfLife)
                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    //family role spinner item selection function
    private fun familyRoleSelection() {
        val spinnerFamilyRole = resources.getStringArray(R.array.family_role_problem_array)
        spinner_family_role_problem.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerFamilyRole
        )
        spinner_family_role_problem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    familyRole = spinnerFamilyRole[position]
                    val chipFamilyRole = Chip(this@CommunityChaplainAssessmentActivity)
                    chipFamilyRole.isCloseIconVisible = true
                    chipFamilyRole.setChipBackgroundColorResource(R.color.colorAccent)
                    chipFamilyRole.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (familyRole != "Nothing Selected" && familyRole != "Other") {
                        chipFamilyRole.text = familyRole
                        family_role_problem_chip_group.addView(chipFamilyRole)
                        familyRoleArrayList.add(familyRole)
                    } else if (familyRole == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CommunityChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CommunityChaplainAssessmentActivity)
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
                                chipFamilyRole.text = selection
                                familyRole = selection
                                family_role_problem_chip_group.addView(chipFamilyRole)
                                familyRoleArrayList.add(familyRole)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }

                    chipFamilyRole.setOnClickListener {
                        family_role_problem_chip_group.removeView(chipFamilyRole)
                        familyRoleArrayList.remove(chipFamilyRole.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    //social problem spinner item selection function
    private fun socialProblemSelection() {
        val socialProblem = resources.getStringArray(R.array.social_problem_array)
        spinner_social_problem.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            socialProblem
        )
        spinner_social_problem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    socialIssue = socialProblem[position]
                    val chipSocialIssue = Chip(this@CommunityChaplainAssessmentActivity)
                    chipSocialIssue.isCloseIconVisible = true
                    chipSocialIssue.setChipBackgroundColorResource(R.color.colorAccent)
                    chipSocialIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (socialIssue != "Nothing Selected" && socialIssue != "Other") {
                        chipSocialIssue.text = socialIssue
                        social_problem_chip_group.addView(chipSocialIssue)
                        socialIssueArrayList.add(socialIssue)
                    } else if (socialIssue == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CommunityChaplainAssessmentActivity)
                        builder.setTitle(getString(R.string.social_problem_alert_title))

                        // Set up the input
                        val input = EditText(this@CommunityChaplainAssessmentActivity)
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
                                chipSocialIssue.text = selection
                                socialIssue = selection
                                social_problem_chip_group.addView(chipSocialIssue)
                                socialIssueArrayList.add(socialIssue)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }

                    chipSocialIssue.setOnClickListener {
                        social_problem_chip_group.removeView(chipSocialIssue)
                        socialIssueArrayList.remove(chipSocialIssue.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    //family problem spinner item selection function
    private fun familyProblemSelection() {
        val familyProblem = resources.getStringArray(R.array.family_problem_array)
        spinner_family_problem.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            familyProblem
        )
        spinner_family_problem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    familyIssue = familyProblem[position]
                    val chipFamilyIssue = Chip(this@CommunityChaplainAssessmentActivity)
                    chipFamilyIssue.isCloseIconVisible = true
                    chipFamilyIssue.setChipBackgroundColorResource(R.color.colorAccent)
                    chipFamilyIssue.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (familyIssue != "Nothing Selected" && familyIssue != "Other") {
                        chipFamilyIssue.text = familyIssue
                        family_problem_chip_group.addView(chipFamilyIssue)
                        familyIssueArrayList.add(familyIssue)
                    } else if (familyIssue == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CommunityChaplainAssessmentActivity)
                        builder.setTitle(R.string.chaplain_sign_up_credential_title_textView_text)

                        // Set up the input
                        val input = EditText(this@CommunityChaplainAssessmentActivity)
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.hint =
                            getString(R.string.chaplain_sign_up_credential_title_textView_text)
                        input.inputType = InputType.TYPE_CLASS_TEXT
                        builder.setView(input)

                        // Set up the buttons
                        builder.setPositiveButton(
                            getString(R.string.chaplain_sign_up_alert_ok_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                // Here you get get input text from the Edittext
                                val selection = input.text.toString()

                                //assign input to chip and add chip
                                chipFamilyIssue.text = selection
                                familyIssue = selection
                                family_problem_chip_group.addView(chipFamilyIssue)
                                familyIssueArrayList.add(familyIssue)
                            })
                        builder.setNegativeButton(
                            getString(R.string.chaplain_sign_up_alert_cancel_button),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.cancel()
                            })

                        builder.show()
                    }

                    chipFamilyIssue.setOnClickListener {
                        family_problem_chip_group.removeView(chipFamilyIssue)
                        familyIssueArrayList.remove(chipFamilyIssue.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

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