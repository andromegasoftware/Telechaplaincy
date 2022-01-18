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
import kotlinx.android.synthetic.main.activity_prison_chaplain_assessment.*

class PrisonChaplainAssessmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""

    private var whatReasonBringsYou: String = ""
    private var spiritualResource: String = ""
    private var relationshipTalk: String = ""
    private var meaningOfLife: String = ""
    private var meaningOfLifeArrayList = ArrayList<String>()
    private var experiencingChallenge: String = ""
    private var topThreeEmotions: String = ""
    private var topThreeEmotionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prison_chaplain_assessment)

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
        meaningOfLifeSelection()
    }

    override fun onStart() {
        super.onStart()
        readDataFromFireStore()
    }

    private fun readDataFromFireStore() {
        prison_source_of_strength_chip_group.removeAllViews()
        prison_top_three_emotions_chip_group.removeAllViews()
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
                                        Chip(this@PrisonChaplainAssessmentActivity)
                                    chipTopThreeEmotions.isCloseIconVisible = true
                                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipTopThreeEmotions.text = topThreeEmotionsArrayList[k]
                                    prison_top_three_emotions_chip_group.addView(
                                        chipTopThreeEmotions
                                    )

                                    chipTopThreeEmotions.setOnClickListener {
                                        prison_top_three_emotions_chip_group.removeView(
                                            chipTopThreeEmotions
                                        )
                                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
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
                                        Chip(this@PrisonChaplainAssessmentActivity)
                                    chipMeaningOfLife.isCloseIconVisible = true
                                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                                    chipMeaningOfLife.text = meaningOfLifeArrayList[k]
                                    prison_source_of_strength_chip_group.addView(
                                        chipMeaningOfLife
                                    )

                                    chipMeaningOfLife.setOnClickListener {
                                        prison_source_of_strength_chip_group.removeView(
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
                            prison_chaplain_assessment_page_what_brings_editText.setText(
                                whatReasonBringsYou
                            )
                        }
                    }
                    if (document["spiritualResource"].toString() != "") {
                        spiritualResource = document["spiritualResource"].toString()
                        if (spiritualResource != "") {
                            if (spiritualResource == "Yes") {
                                prison_spiritual_yes_radioButton.isChecked = true
                                prison_spiritual_no_radioButton.isChecked = false
                            } else if (spiritualResource == "No") {
                                prison_spiritual_yes_radioButton.isChecked = false
                                prison_spiritual_no_radioButton.isChecked = true
                            }
                        }
                    }
                    if (document["relationshipTalk"].toString() != "") {
                        relationshipTalk = document["relationshipTalk"].toString()
                        if (relationshipTalk != "") {
                            if (relationshipTalk == "Yes") {
                                prison_relationship_yes_radioButton.isChecked = true
                                prison_relationship_no_radioButton.isChecked = false
                            } else if (relationshipTalk == "No") {
                                prison_relationship_yes_radioButton.isChecked = false
                                prison_relationship_no_radioButton.isChecked = true
                            }
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        experiencingChallenge = document["experiencingChallenge"].toString()
                        if (experiencingChallenge != "") {
                            if (experiencingChallenge == "Affect your belief and relationship with the Divine?") {
                                prison_experiencing_challenge__affect_radioButton.isChecked =
                                    true
                                prison_experiencing_challenge__result_radioButton.isChecked =
                                    false
                            } else if (experiencingChallenge == "Result in a change in your rituals and practice?") {
                                prison_experiencing_challenge__affect_radioButton.isChecked =
                                    false
                                prison_experiencing_challenge__result_radioButton.isChecked =
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
            prison_chaplain_assessment_page_what_brings_editText.text.toString() //to take data from editText
        val data = hashMapOf(
            "whatReasonBringsYou" to whatReasonBringsYou,
            "spiritualResource" to spiritualResource,
            "relationshipTalk" to relationshipTalk,
            "meaningOfLife" to meaningOfLifeArrayList,
            "experiencingChallenge" to experiencingChallenge,
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
        spinner_source_of_strength_prison.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerMeaningOfLife
        )
        spinner_source_of_strength_prison.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meaningOfLife = spinnerMeaningOfLife[position]
                    val chipMeaningOfLife = Chip(this@PrisonChaplainAssessmentActivity)
                    chipMeaningOfLife.isCloseIconVisible = true
                    chipMeaningOfLife.setChipBackgroundColorResource(R.color.colorAccent)
                    chipMeaningOfLife.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (meaningOfLife != "Nothing Selected" && meaningOfLife != "Other") {
                        chipMeaningOfLife.text = meaningOfLife
                        prison_source_of_strength_chip_group.addView(chipMeaningOfLife)
                        meaningOfLifeArrayList.add(meaningOfLife)
                    } else if (meaningOfLife == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@PrisonChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PrisonChaplainAssessmentActivity)
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
                                prison_source_of_strength_chip_group.addView(
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
                        prison_source_of_strength_chip_group.removeView(chipMeaningOfLife)
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
        spinner_top_three_emotions_prison.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerTopThreeEmotions
        )
        spinner_top_three_emotions_prison.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    topThreeEmotions = spinnerTopThreeEmotions[position]
                    val chipTopThreeEmotions = Chip(this@PrisonChaplainAssessmentActivity)
                    chipTopThreeEmotions.isCloseIconVisible = true
                    chipTopThreeEmotions.setChipBackgroundColorResource(R.color.colorAccent)
                    chipTopThreeEmotions.setTextColor(resources.getColor(R.color.colorPrimary))
                    if (topThreeEmotions != "Nothing Selected" && topThreeEmotions != "Other") {
                        chipTopThreeEmotions.text = topThreeEmotions
                        prison_top_three_emotions_chip_group.addView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.add(topThreeEmotions)
                    } else if (topThreeEmotions == "Other") {

                        //alert dialog create
                        val builder = AlertDialog.Builder(this@PrisonChaplainAssessmentActivity)
                        builder.setTitle(R.string.social_problem_alert_title)

                        // Set up the input
                        val input = EditText(this@PrisonChaplainAssessmentActivity)
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
                                prison_top_three_emotions_chip_group.addView(
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
                        prison_top_three_emotions_chip_group.removeView(chipTopThreeEmotions)
                        topThreeEmotionsArrayList.remove(chipTopThreeEmotions.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun takeRadioButtonsInfo() {
        prison_spiritual_yes_radioButton.setOnClickListener {
            prison_spiritual_no_radioButton.isChecked = false
            spiritualResource = prison_spiritual_yes_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }
        prison_spiritual_no_radioButton.setOnClickListener {
            prison_spiritual_yes_radioButton.isChecked = false
            spiritualResource = prison_spiritual_no_radioButton.text.toString()
            //Log.d("significantHealthIssue", significantHealthIssue)
        }

        prison_relationship_yes_radioButton.setOnClickListener {
            prison_relationship_no_radioButton.isChecked = false
            relationshipTalk = prison_relationship_yes_radioButton.text.toString()
        }
        prison_relationship_no_radioButton.setOnClickListener {
            prison_relationship_yes_radioButton.isChecked = false
            relationshipTalk = prison_relationship_no_radioButton.text.toString()
        }
        prison_experiencing_challenge__affect_radioButton.setOnClickListener {
            prison_experiencing_challenge__result_radioButton.isChecked = false
            experiencingChallenge =
                prison_experiencing_challenge__affect_radioButton.text.toString()
        }
        prison_experiencing_challenge__result_radioButton.setOnClickListener {
            prison_experiencing_challenge__affect_radioButton.isChecked = false
            experiencingChallenge =
                prison_experiencing_challenge__result_radioButton.text.toString()
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