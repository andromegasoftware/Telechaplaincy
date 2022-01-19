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
import kotlinx.android.synthetic.main.activity_community_chaplain_assessment.*
import kotlinx.android.synthetic.main.activity_pediatric_chaplain_assessment.*

class CollegeChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()
    private var psychologicalIssue: String = ""
    private var emotionalIssue: String = ""
    private var spiritualIssue: String = ""
    private var currentExperience: String = ""
    private var currentExperienceArrayList = ArrayList<String>()
    private var familyRole: String = ""
    private var familyRoleArrayList = ArrayList<String>()
    private var meaningOfLife: String = ""
    private var meaningOfLifeArrayList = ArrayList<String>()
    private var experiencingChallenge: String = ""
    private var partOfSocialCommunity: String = ""
    private var partOfSocialCommunityArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_college_chaplain_assessment)

        appointmentId = intent.getStringExtra("appointment_id").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            patientUserId = user.uid
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

        takeRadioButtonsInfo()
        currentlyExperienceSelection()
        topThreeEmotionsSelection()
        familyRoleSelection()
        partOfSocialCommunitySelection()
        meaningOfLifeSelection()
    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        college_top_three_emotions_chip_group.removeAllViews()
        college_currently_experiencing_chip_group.removeAllViews()
        college_family_role_problem_chip_group.removeAllViews()
        college_source_of_strength_chip_group.removeAllViews()
        college_are_you_part_of_spiritual_chip_group.removeAllViews()
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
                                        Chip(this@CollegeChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    college_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        college_top_three_emotions_chip_group.removeView(
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
                                        Chip(this@CollegeChaplainAssessmentActivity)
                                    chipMajorHealth.isCloseIconVisible = true
                                    chipMajorHealth.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMajorHealth.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMajorHealth.text = currentExperienceArrayList[k]
                                    college_currently_experiencing_chip_group.addView(
                                        chipMajorHealth
                                    )

                                    chipMajorHealth.setOnClickListener {
                                        college_currently_experiencing_chip_group.removeView(
                                            chipMajorHealth
                                        )
                                        currentExperienceArrayList.remove(chipMajorHealth.text)
                                    }
                                }
                            }
                        }
                    }

                    if (document["familyRole"] != null) {
                        familyRoleArrayList = document["familyRole"] as ArrayList<String>
                        if (!familyRoleArrayList.isNullOrEmpty()) {
                            for (k in familyRoleArrayList.indices) {
                                if (familyRoleArrayList[k] != "Nothing Selected") {
                                    val chipFamilyRole =
                                        Chip(this@CollegeChaplainAssessmentActivity)
                                    chipFamilyRole.isCloseIconVisible = true
                                    chipFamilyRole.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipFamilyRole.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipFamilyRole.text = familyRoleArrayList[k]
                                    college_family_role_problem_chip_group.addView(chipFamilyRole)

                                    chipFamilyRole.setOnClickListener {
                                        college_family_role_problem_chip_group.removeView(
                                            chipFamilyRole
                                        )
                                        familyRoleArrayList.remove(chipFamilyRole.text)
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
                                        Chip(this@CollegeChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    college_source_of_strength_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        college_source_of_strength_chip_group.removeView(
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
                                        Chip(this@CollegeChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = partOfSocialCommunityArrayList[k]
                                    college_are_you_part_of_spiritual_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        college_are_you_part_of_spiritual_chip_group.removeView(
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
                            college_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["psychologicalIssue"].toString() != "") {
                        psychologicalIssue = document["psychologicalIssue"].toString()
                        if (psychologicalIssue != "") {
                            if (psychologicalIssue == "Yes") {
                                college_psychological_issues_yes_radioButton.isChecked = true
                                college_psychological_issues_no_radioButton.isChecked = false
                            } else if (psychologicalIssue == "No") {
                                college_psychological_issues_yes_radioButton.isChecked = false
                                college_psychological_issues_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["emotionalIssue"].toString() != "") {
                        emotionalIssue = document["emotionalIssue"].toString()
                        if (emotionalIssue != "") {
                            if (emotionalIssue == "Yes") {
                                college_emotional_issues_yes_radioButton.isChecked = true
                                college_emotional_issues_no_radioButton.isChecked = false
                            } else if (emotionalIssue == "No") {
                                college_emotional_issues_yes_radioButton.isChecked = false
                                college_emotional_issues_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["spiritualIssue"].toString() != "") {
                        spiritualIssue = document["spiritualIssue"].toString()
                        if (spiritualIssue != "") {
                            if (spiritualIssue == "Yes") {
                                college_spiritual_issues_yes_radioButton.isChecked = true
                                college_spiritual_issues_no_radioButton.isChecked = false
                            } else if (spiritualIssue == "No") {
                                college_spiritual_issues_yes_radioButton.isChecked = false
                                college_spiritual_issues_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                college_experiencing_challenge__affect_radioButton.isChecked =
                                    true
                                college_experiencing_challenge__result_radioButton.isChecked =
                                    false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                college_experiencing_challenge__affect_radioButton.isChecked =
                                    false
                                college_experiencing_challenge__result_radioButton.isChecked =
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
            college_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "topThreeEmotions" to topThreeEmotionsArrayList,
            "psychologicalIssue" to psychologicalIssue,
            "emotionalIssue" to emotionalIssue,
            "spiritualIssue" to spiritualIssue,
            "currentExperience" to currentExperienceArrayList,
            "familyRole" to familyRoleArrayList,
            "meaningOfLife" to meaningOfLifeArrayList,
            "experiencingChallenge" to experiencingChallenge,
            "partOfSocialCommunity" to partOfSocialCommunityArrayList
        )

        dbSave.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    //top Three Emotions spinner item selection function
    private fun topThreeEmotionsSelection() {
        val spinnerTopThreeEmotions = resources.getStringArray(R.array.top_three_emotions_array)
        spinner_top_three_emotions_college.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_college.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@CollegeChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        college_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CollegeChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CollegeChaplainAssessmentActivity)
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
                                college_top_three_emotions_chip_group.addView(
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
                        college_top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //current Experience spinner item selection function
    private fun currentlyExperienceSelection() {
        val spinnerCurrentlyExperience = resources.getStringArray(R.array.current_problem_array)
        spinner_currently_experiencing_college.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerCurrentlyExperience
        )
        spinner_currently_experiencing_college.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentExperience = spinnerCurrentlyExperience[position]
                    val chipCurrentExperience = Chip(this@CollegeChaplainAssessmentActivity)
                    chipCurrentExperience.isCloseIconVisible = true
                    chipCurrentExperience.setChipBackgroundColorResource(R.color.colorAccent)
                    chipCurrentExperience.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (currentExperience != "Nothing Selected" && currentExperience != "Other") {
                        chipCurrentExperience.text = currentExperience
                        college_currently_experiencing_chip_group.addView(chipCurrentExperience)
                        currentExperienceArrayList.add(currentExperience)
                    } else if (currentExperience == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CollegeChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CollegeChaplainAssessmentActivity)
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
                                college_currently_experiencing_chip_group.addView(
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
                        college_currently_experiencing_chip_group.removeView(chipCurrentExperience)
                        currentExperienceArrayList.remove(chipCurrentExperience.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    //family role spinner item selection function
    private fun familyRoleSelection() {
        val spinnerFamilyRole = resources.getStringArray(R.array.family_role_problem_array)
        spinner_family_role_problem_college.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerFamilyRole
        )
        spinner_family_role_problem_college.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    familyRole = spinnerFamilyRole[position]
                    val chipFamilyRole = Chip(this@CollegeChaplainAssessmentActivity)
                    chipFamilyRole.isCloseIconVisible = true
                    chipFamilyRole.setChipBackgroundColorResource(R.color.colorAccent)
                    chipFamilyRole.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (familyRole != "Nothing Selected" && familyRole != "Other") {
                        chipFamilyRole.text = familyRole
                        college_family_role_problem_chip_group.addView(chipFamilyRole)
                        familyRoleArrayList.add(familyRole)
                    } else if (familyRole == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CollegeChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CollegeChaplainAssessmentActivity)
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
                                college_family_role_problem_chip_group.addView(chipFamilyRole)
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
                        college_family_role_problem_chip_group.removeView(chipFamilyRole)
                        familyRoleArrayList.remove(chipFamilyRole.text)
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
        spinner_are_you_part_of_spiritual_college.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerPartOfSocialCommunity
        )
        spinner_are_you_part_of_spiritual_college.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    partOfSocialCommunity = spinnerPartOfSocialCommunity[position]
                    val chipPartOfSocialCommunity = Chip(this@CollegeChaplainAssessmentActivity)
                    chipPartOfSocialCommunity.isCloseIconVisible = true
                    chipPartOfSocialCommunity.setChipBackgroundColorResource(R.color.colorAccent)
                    chipPartOfSocialCommunity.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (partOfSocialCommunity != "Nothing Selected" && partOfSocialCommunity != "Other") {
                        chipPartOfSocialCommunity.text = partOfSocialCommunity
                        college_are_you_part_of_spiritual_chip_group.addView(
                            chipPartOfSocialCommunity
                        )
                        partOfSocialCommunityArrayList.add(partOfSocialCommunity)
                    } else if (partOfSocialCommunity == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CollegeChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CollegeChaplainAssessmentActivity)
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
                                college_are_you_part_of_spiritual_chip_group.addView(
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
                        college_are_you_part_of_spiritual_chip_group.removeView(
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
        spinner_source_of_strength_college.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_source_of_strength_college.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@CollegeChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        college_source_of_strength_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@CollegeChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@CollegeChaplainAssessmentActivity)
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
                                college_source_of_strength_chip_group.addView(
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
                        college_source_of_strength_chip_group.removeView(chipMeaningOfLife)
                        meaningOfLifeArrayList.remove(chipMeaningOfLife.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        college_psychological_issues_yes_radioButton.setOnClickListener {
            college_psychological_issues_no_radioButton.isChecked = false
            psychologicalIssue = college_psychological_issues_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        college_psychological_issues_no_radioButton.setOnClickListener {
            college_psychological_issues_yes_radioButton.isChecked = false
            psychologicalIssue = college_psychological_issues_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        college_emotional_issues_yes_radioButton.setOnClickListener {
            college_emotional_issues_no_radioButton.isChecked = false
            emotionalIssue = college_emotional_issues_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        college_emotional_issues_no_radioButton.setOnClickListener {
            college_emotional_issues_yes_radioButton.isChecked = false
            emotionalIssue = college_emotional_issues_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        college_spiritual_issues_yes_radioButton.setOnClickListener {
            college_spiritual_issues_no_radioButton.isChecked = false
            spiritualIssue = college_spiritual_issues_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        college_spiritual_issues_no_radioButton.setOnClickListener {
            college_spiritual_issues_yes_radioButton.isChecked = false
            spiritualIssue = college_spiritual_issues_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        college_experiencing_challenge__affect_radioButton.setOnClickListener {
            college_experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge =
                college_experiencing_challenge__affect_radioButton.text.toString()
        }
        college_experiencing_challenge__result_radioButton.setOnClickListener {
            college_experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge =
                college_experiencing_challenge__result_radioButton.text.toString()
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