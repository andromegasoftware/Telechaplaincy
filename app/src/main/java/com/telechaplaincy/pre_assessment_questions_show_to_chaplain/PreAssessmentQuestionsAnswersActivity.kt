package com.telechaplaincy.pre_assessment_questions_show_to_chaplain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_future_appointments.ChaplainFutureAppointmentDetailsActivity
import kotlinx.android.synthetic.main.activity_pre_assessment_questions_answers.*

class PreAssessmentQuestionsAnswersActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var dbSave: DocumentReference
    private lateinit var user: FirebaseUser
    private var patientUserId: String = ""
    private var appointmentId: String = ""
    private var chaplainCategory: String = ""

    private var answer1: String = ""
    private var answer2: String = ""
    private var answer3: String = ""
    private var answer4: String = ""
    private var answer5: String = ""
    private var answer6: String = ""
    private var answer7: String = ""
    private var answer8: String = ""
    private var answer9: String = ""
    private var answer10: String = ""
    private var answer11: String = ""
    private var array1ArrayList = ArrayList<String>()
    private var array2ArrayList = ArrayList<String>()
    private var array3ArrayList = ArrayList<String>()
    private var array4ArrayList = ArrayList<String>()
    private var array5ArrayList = ArrayList<String>()
    private var array6ArrayList = ArrayList<String>()
    private var array7ArrayList = ArrayList<String>()
    private var array8ArrayList = ArrayList<String>()
    private var array9ArrayList = ArrayList<String>()
    private var array10ArrayList = ArrayList<String>()
    private var array11ArrayList = ArrayList<String>()
    private var array12ArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_assessment_questions_answers)

        appointmentId = intent.getStringExtra("appointment_id").toString()
        patientUserId = intent.getStringExtra("patientUserId").toString()
        chaplainCategory = intent.getStringExtra("chaplainCategory").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

    }

    override fun onStart() {
        super.onStart()
        readChaplainSelectedCategories()
    }

    private fun readChaplainSelectedCategories() {
        if (chaplainCategory == "HumanistChaplains") {
            humanistChaplainAssessmentQuestions()
        } else if (chaplainCategory == "HealthChaplains") {
            healthChaplainAssessmentQuestions()
        } else if (chaplainCategory == "HospiceChaplains") {
            hospiceChaplainAssessmentQuestions()
        } else if (chaplainCategory == "MentalChaplains") {
            mentalChaplainAssessmentQuestions()
        }
        //crisis chaplain and mental health Chaplain assessment questions are same
        else if (chaplainCategory == "CrisisChaplains") {
            mentalChaplainAssessmentQuestions()
        } else if (chaplainCategory == "PalliativeChaplains") {
            palliativeChaplainAssessmentQuestions()
        } else if (chaplainCategory == "PetChaplains") {
            petChaplainAssessmentQuestions()
        } else if (chaplainCategory == "PrisonChaplains") {
            prisonChaplainAssessmentQuestions()
        } else if (chaplainCategory == "PediatricChaplains") {
            pediatricChaplainAssessmentQuestions()
        } else if (chaplainCategory == "CollegeChaplains") {
            collegeChaplainAssessmentQuestions()
        } else if (chaplainCategory == "CorporateChaplains") {
            corporateChaplainAssessmentQuestions()
        } else if (chaplainCategory == "MilitaryChaplains") {
            militaryLawChaplainAssessmentQuestions()
        } else if (chaplainCategory == "LawChaplains") {
            militaryLawChaplainAssessmentQuestions()
        }
    }

    private fun militaryLawChaplainAssessmentQuestions() {
        textViewQuestion2.visibility = View.GONE
        textViewQuestion3.visibility = View.GONE
        textViewQuestion4.visibility = View.GONE
        textViewQuestion5.visibility = View.GONE
        textViewQuestion6.visibility = View.GONE
        textViewQuestion7.visibility = View.GONE
        textViewQuestion8.visibility = View.GONE
        textViewQuestion9.visibility = View.GONE
        textViewQuestion10.visibility = View.GONE
        textViewQuestion11.visibility = View.GONE

        textViewAnswer2.visibility = View.GONE
        textViewAnswer3.visibility = View.GONE
        textViewAnswer4.visibility = View.GONE
        textViewAnswer5.visibility = View.GONE
        textViewAnswer6.visibility = View.GONE
        textViewAnswer7.visibility = View.GONE
        textViewAnswer8.visibility = View.GONE
        textViewAnswer9.visibility = View.GONE
        textViewAnswer10.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        view2.visibility = View.GONE
        view3.visibility = View.GONE
        view4.visibility = View.GONE
        view5.visibility = View.GONE
        view6.visibility = View.GONE
        view7.visibility = View.GONE
        view8.visibility = View.GONE
        view9.visibility = View.GONE
        view10.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
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

    private fun corporateChaplainAssessmentQuestions() {
        textViewQuestion2.text =
            getString(R.string.corporate_chaplain_assessment_page_significant_issue)
        textViewQuestion3.text =
            getString(R.string.corporate_chaplain_assessment_page_significant_issue_spinner)
        textViewQuestion4.text =
            getString(R.string.corporate_chaplain_assessment_page_significant_issues_prevent)
        textViewQuestion5.text =
            getString(R.string.corporate_chaplain_assessment_page_significant_issues_currently)
        textViewQuestion6.text =
            getString(R.string.corporate_chaplain_assessment_page_your_role_at_work)
        textViewQuestion7.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion8.text =
            getString(R.string.experiencing_challenge)
        textViewQuestion9.text =
            getString(R.string.are_you_part_of_spiritual)
        textViewQuestion10.text =
            getString(R.string.top_three_emotions)

        textViewQuestion11.visibility = View.GONE

        textViewAnswer11.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["significantIssue"].toString() != "") {
                        answer2 = document["significantIssue"].toString()
                        if (answer2 != "") {
                            textViewAnswer2.text = answer2
                        }
                    }

                    if (document["workIssues"] != null) {
                        array3ArrayList = document["workIssues"] as ArrayList<String>
                        if (!array3ArrayList.isNullOrEmpty()) {
                            textViewAnswer3.text = array3ArrayList.joinToString(",")
                        }
                    }

                    if (document["responsibilityIssue"].toString() != "") {
                        answer4 = document["responsibilityIssue"].toString()
                        if (answer4 != "") {
                            textViewAnswer4.text = answer4
                        }
                    }

                    if (document["currentExperience"] != null) {
                        array5ArrayList =
                            document["currentExperience"] as ArrayList<String>
                        if (!array5ArrayList.isNullOrEmpty()) {
                            textViewAnswer5.text = array5ArrayList.joinToString(",")
                        }
                    }

                    if (document["workRole"] != null) {
                        array6ArrayList =
                            document["workRole"] as ArrayList<String>
                        if (!array6ArrayList.isNullOrEmpty()) {
                            textViewAnswer6.text = array6ArrayList.joinToString(",")
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array7ArrayList =
                            document["meaningOfLife"] as ArrayList<String>
                        if (!array7ArrayList.isNullOrEmpty()) {
                            textViewAnswer7.text = array7ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer8 = document["experiencingChallenge"].toString()
                        if (answer8 != "") {
                            textViewAnswer8.text = answer8
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array9ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array9ArrayList.isNullOrEmpty()) {
                            textViewAnswer9.text = array9ArrayList.joinToString(",")
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array10ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array10ArrayList.isNullOrEmpty()) {
                            textViewAnswer10.text = array10ArrayList.joinToString(",")
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

    private fun pediatricChaplainAssessmentQuestions() {
        textViewQuestion2.text = getString(R.string.pediatric_chaplain_assessment_page_major_health)
        textViewQuestion3.text =
            getString(R.string.pediatric_chaplain_assessment_top_three_emotions)
        textViewQuestion4.text =
            getString(R.string.pediatric_chaplain_assessment_page_conflict)
        textViewQuestion5.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion6.text =
            getString(R.string.experiencing_challenge)
        textViewQuestion7.text =
            getString(R.string.are_you_part_of_spiritual)
        textViewQuestion8.visibility = View.GONE
        textViewQuestion9.visibility = View.GONE
        textViewQuestion10.visibility = View.GONE
        textViewQuestion11.visibility = View.GONE

        textViewAnswer8.visibility = View.GONE
        textViewAnswer9.visibility = View.GONE
        textViewAnswer10.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        view8.visibility = View.GONE
        view9.visibility = View.GONE
        view10.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["majorHealth"] != null) {
                        array2ArrayList = document["majorHealth"] as ArrayList<String>
                        if (!array2ArrayList.isNullOrEmpty()) {
                            textViewAnswer2.text = array2ArrayList.joinToString(",")
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array3ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array3ArrayList.isNullOrEmpty()) {
                            textViewAnswer3.text = array3ArrayList.joinToString(",")
                        }
                    }

                    if (document["meaningfulResource"].toString() != "") {
                        answer4 = document["meaningfulResource"].toString()
                        if (answer4 != "") {
                            textViewAnswer4.text = answer4
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array5ArrayList =
                            document["meaningOfLife"] as ArrayList<String>
                        if (!array5ArrayList.isNullOrEmpty()) {
                            textViewAnswer5.text = array5ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer6 = document["experiencingChallenge"].toString()
                        if (answer6 != "") {
                            textViewAnswer6.text = answer6
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array7ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array7ArrayList.isNullOrEmpty()) {
                            textViewAnswer7.text = array7ArrayList.joinToString(",")
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

    private fun prisonChaplainAssessmentQuestions() {
        textViewQuestion2.text = getString(R.string.access_spiritual_resources)
        textViewQuestion3.text =
            getString(R.string.relationship_talk)
        textViewQuestion4.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion5.text =
            getString(R.string.experiencing_challenge)
        textViewQuestion6.text =
            getString(R.string.top_three_emotions)
        textViewQuestion7.visibility = View.GONE
        textViewQuestion8.visibility = View.GONE
        textViewQuestion9.visibility = View.GONE
        textViewQuestion10.visibility = View.GONE
        textViewQuestion11.visibility = View.GONE

        textViewAnswer7.visibility = View.GONE
        textViewAnswer8.visibility = View.GONE
        textViewAnswer9.visibility = View.GONE
        textViewAnswer10.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        view7.visibility = View.GONE
        view8.visibility = View.GONE
        view9.visibility = View.GONE
        view10.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["spiritualResource"].toString() != "") {
                        answer2 = document["spiritualResource"].toString()
                        if (answer2 != "") {
                            textViewAnswer2.text = answer2
                        }
                    }

                    if (document["relationshipTalk"].toString() != "") {
                        answer3 = document["relationshipTalk"].toString()
                        if (answer3 != "") {
                            textViewAnswer3.text = answer3
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array4ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array4ArrayList.isNullOrEmpty()) {
                            textViewAnswer4.text = array4ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer5 = document["experiencingChallenge"].toString()
                        if (answer5 != "") {
                            textViewAnswer5.text = answer5
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array6ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array6ArrayList.isNullOrEmpty()) {
                            textViewAnswer6.text = array6ArrayList.joinToString(",")
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

    private fun petChaplainAssessmentQuestions() {
        textViewQuestion2.text = getString(R.string.pet_chaplain_assessment_bring_reason)
        textViewQuestion3.text =
            getString(R.string.incident_issue)
        textViewQuestion4.text =
            getString(R.string.top_three_emotions)
        textViewQuestion5.visibility = View.GONE
        textViewQuestion6.visibility = View.GONE
        textViewQuestion7.visibility = View.GONE
        textViewQuestion8.visibility = View.GONE
        textViewQuestion9.visibility = View.GONE
        textViewQuestion10.visibility = View.GONE
        textViewQuestion11.visibility = View.GONE

        textViewAnswer5.visibility = View.GONE
        textViewAnswer6.visibility = View.GONE
        textViewAnswer7.visibility = View.GONE
        textViewAnswer8.visibility = View.GONE
        textViewAnswer9.visibility = View.GONE
        textViewAnswer10.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        view5.visibility = View.GONE
        view6.visibility = View.GONE
        view7.visibility = View.GONE
        view8.visibility = View.GONE
        view9.visibility = View.GONE
        view10.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["comeReason"] != null) {
                        array2ArrayList = document["comeReason"] as ArrayList<String>
                        if (!array2ArrayList.isNullOrEmpty()) {
                            textViewAnswer2.text = array2ArrayList.joinToString(",")
                        }
                    }

                    if (document["psychologicalIssue"].toString() != "") {
                        answer3 = document["psychologicalIssue"].toString()
                        if (answer3 != "") {
                            textViewAnswer3.text = answer3
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array4ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array4ArrayList.isNullOrEmpty()) {
                            textViewAnswer4.text = array4ArrayList.joinToString(",")
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

    private fun palliativeChaplainAssessmentQuestions() {
        textViewQuestion2.text =
            getString(R.string.palliative_care_chaplain_assessment_activity_spiritual_text)
        textViewQuestion3.text =
            getString(R.string.incident_issue)
        textViewQuestion4.text =
            getString(R.string.conflict)
        textViewQuestion5.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion6.text =
            getString(R.string.experiencing_challenge)
        textViewQuestion7.text = getString(R.string.are_you_part_of_spiritual)
        textViewQuestion8.text =
            getString(R.string.top_three_emotions)
        textViewQuestion9.visibility = View.GONE
        textViewQuestion10.visibility = View.GONE
        textViewQuestion11.visibility = View.GONE

        textViewAnswer9.visibility = View.GONE
        textViewAnswer10.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        view9.visibility = View.GONE
        view10.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["psychologicalIssue"].toString() != "") {
                        answer2 = document["psychologicalIssue"].toString()
                        if (answer2 != "") {
                            textViewAnswer2.text = answer2
                        }
                    }

                    if (document["incidentIssue"].toString() != "") {
                        answer3 = document["incidentIssue"].toString()
                        if (answer3 != "") {
                            textViewAnswer3.text = answer3
                        }
                    }

                    if (document["meaningfulResource"] != null) {
                        answer4 = document["meaningfulResource"].toString()
                        if (answer4 != "") {
                            textViewAnswer4.text = answer4
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array5ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array5ArrayList.isNullOrEmpty()) {
                            textViewAnswer5.text = array5ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer6 = document["experiencingChallenge"].toString()
                        if (answer6 != "") {
                            textViewAnswer6.text = answer6
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array7ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array7ArrayList.isNullOrEmpty()) {
                            textViewAnswer7.text = array7ArrayList.joinToString(",")
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array8ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array8ArrayList.isNullOrEmpty()) {
                            textViewAnswer8.text = array8ArrayList.joinToString(",")
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

    private fun mentalChaplainAssessmentQuestions() {
        textViewQuestion2.text =
            getString(R.string.health_care_chaplain_assessment_page_psychological_issue)
        textViewQuestion3.text =
            getString(R.string.health_care_chaplain_assessment_page_major_health_problem)
        textViewQuestion4.text =
            getString(R.string.mental_health_chaplain_assessment_page_crisis_situation)
        textViewQuestion5.text =
            getString(R.string.significant_change)
        textViewQuestion6.text =
            getString(R.string.incident_issue)
        textViewQuestion7.text = getString(R.string.suicide_thought)
        textViewQuestion8.text =
            getString(R.string.conflict)
        textViewQuestion9.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion10.text = getString(R.string.experiencing_challenge)
        textViewQuestion11.text = getString(R.string.are_you_part_of_spiritual)
        textViewQuestion12.text = getString(R.string.top_three_emotions)

        view11.visibility = View.VISIBLE
        textViewQuestion12.visibility = View.VISIBLE
        textViewAnswer12.visibility = View.VISIBLE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["psychologicalIssue"].toString() != "") {
                        answer2 = document["psychologicalIssue"].toString()
                        if (answer2 != "") {
                            textViewAnswer2.text = answer2
                        }
                    }

                    if (document["significantHealthIssue"] != null) {
                        array3ArrayList = document["significantHealthIssue"] as ArrayList<String>
                        if (!array3ArrayList.isNullOrEmpty()) {
                            textViewAnswer3.text = array3ArrayList.joinToString(",")
                        }
                    }

                    if (document["crisisIssue"] != null) {
                        array4ArrayList = document["crisisIssue"] as ArrayList<String>
                        if (!array4ArrayList.isNullOrEmpty()) {
                            textViewAnswer4.text = array4ArrayList.joinToString(",")
                        }
                    }

                    if (document["dramaticChange"].toString() != "") {
                        answer5 =
                            document["dramaticChange"].toString()
                        if (answer5 != "") {
                            textViewAnswer5.text = answer5
                        }
                    }

                    if (document["incidentIssue"].toString() != "") {
                        answer6 =
                            document["incidentIssue"].toString()
                        if (answer6 != "") {
                            textViewAnswer6.text = answer6
                        }
                    }

                    if (document["suicideIssue"].toString() != "") {
                        answer7 =
                            document["suicideIssue"].toString()
                        if (answer7 != "") {
                            textViewAnswer7.text = answer7
                        }
                    }

                    if (document["meaningfulResource"].toString() != "") {
                        answer8 =
                            document["meaningfulResource"].toString()
                        if (answer8 != "") {
                            textViewAnswer8.text = answer8
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array9ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array9ArrayList.isNullOrEmpty()) {
                            textViewAnswer9.text = array9ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer10 = document["experiencingChallenge"].toString()
                        if (answer10 != "") {
                            textViewAnswer10.text = answer10
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array11ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array11ArrayList.isNullOrEmpty()) {
                            textViewAnswer11.text = array11ArrayList.joinToString(",")
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array12ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array12ArrayList.isNullOrEmpty()) {
                            textViewAnswer12.text = array12ArrayList.joinToString(",")
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

    private fun hospiceChaplainAssessmentQuestions() {
        textViewQuestion2.text =
            getString(R.string.hospice_chaplain_assessment_page_psychological_issue)
        textViewQuestion3.text =
            getString(R.string.top_three_emotions)
        textViewQuestion4.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion5.text =
            getString(R.string.experiencing_challenge)
        textViewQuestion6.text =
            getString(R.string.are_you_part_of_spiritual)

        textViewQuestion7.visibility = View.GONE
        textViewAnswer7.visibility = View.GONE
        textViewQuestion8.visibility = View.GONE
        textViewAnswer8.visibility = View.GONE
        textViewQuestion9.visibility = View.GONE
        textViewAnswer9.visibility = View.GONE
        textViewQuestion10.visibility = View.GONE
        textViewAnswer10.visibility = View.GONE
        textViewQuestion11.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        view7.visibility = View.GONE
        view8.visibility = View.GONE
        view9.visibility = View.GONE
        view10.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["psychologicalIssueYesNo"].toString() != "") {
                        answer2 = document["psychologicalIssueYesNo"].toString()
                        if (answer2 != "") {
                            textViewAnswer2.text = answer2
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array3ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array3ArrayList.isNullOrEmpty()) {
                            textViewAnswer3.text = array3ArrayList.joinToString(",")
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array4ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array4ArrayList.isNullOrEmpty()) {
                            textViewAnswer4.text = array4ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer5 = document["experiencingChallenge"].toString()
                        if (answer5 != "") {
                            textViewAnswer5.text = answer5
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array6ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array6ArrayList.isNullOrEmpty()) {
                            textViewAnswer6.text = array6ArrayList.joinToString(",")
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

    private fun healthChaplainAssessmentQuestions() {
        textViewQuestion2.text =
            getString(R.string.health_care_chaplain_assessment_page_major_health_problem)
        textViewQuestion3.text =
            getString(R.string.health_care_chaplain_assessment_page_psychological_issue)
        textViewQuestion4.text =
            getString(R.string.health_care_chaplain_assessment_page_psychological_issues_spinner)
        textViewQuestion5.text =
            getString(R.string.health_care_chaplain_assessment_page_incident_issue)
        textViewQuestion6.text =
            getString(R.string.health_care_chaplain_assessment_page_meaningful)
        textViewQuestion7.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion8.text =
            getString(R.string.experiencing_challenge)
        textViewQuestion9.text = getString(R.string.are_you_part_of_spiritual)
        textViewQuestion10.text = getString(R.string.top_three_emotions)
        textViewQuestion11.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["significantHealthIssue"] != null) {
                        array2ArrayList = document["significantHealthIssue"] as ArrayList<String>
                        if (!array2ArrayList.isNullOrEmpty()) {
                            textViewAnswer2.text = array2ArrayList.joinToString(",")
                        }
                    }

                    if (document["psychologicalIssueYesNo"].toString() != "") {
                        answer3 = document["psychologicalIssueYesNo"].toString()
                        if (answer3 != "") {
                            textViewAnswer3.text = answer3
                        }
                    }

                    if (document["psychologicalIssue"] != null) {
                        array4ArrayList = document["psychologicalIssue"] as ArrayList<String>
                        if (!array4ArrayList.isNullOrEmpty()) {
                            textViewAnswer4.text = array4ArrayList.joinToString(",")
                        }
                    }

                    if (document["incidentIssue"].toString() != "") {
                        answer5 =
                            document["incidentIssue"].toString()
                        if (answer5 != "") {
                            textViewAnswer5.text = answer5
                        }
                    }

                    if (document["meaningfulResource"].toString() != "") {
                        answer6 =
                            document["meaningfulResource"].toString()
                        if (answer6 != "") {
                            textViewAnswer6.text = answer6
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array7ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array7ArrayList.isNullOrEmpty()) {
                            textViewAnswer7.text = array7ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer8 = document["experiencingChallenge"].toString()
                        if (answer8 != "") {
                            textViewAnswer8.text = answer8
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array9ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array9ArrayList.isNullOrEmpty()) {
                            textViewAnswer9.text = array9ArrayList.joinToString(",")
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array10ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array10ArrayList.isNullOrEmpty()) {
                            textViewAnswer10.text = array10ArrayList.joinToString(",")
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

    private fun collegeChaplainAssessmentQuestions() {
        textViewQuestion2.text = getString(R.string.top_three_emotions)
        textViewQuestion3.text =
            getString(R.string.college_chaplain_assessment_page_psychological_issues)
        textViewQuestion4.text =
            getString(R.string.college_chaplain_assessment_page_emotional_issues)
        textViewQuestion5.text =
            getString(R.string.college_chaplain_assessment_page_spiritual_issues)
        textViewQuestion6.text =
            getString(R.string.college_chaplain_assessment_page_currently_experiencing)
        textViewQuestion7.text = getString(R.string.your_role_in_family)
        textViewQuestion8.text =
            getString(R.string.health_care_chaplain_assessment_page_meaning_and_source)
        textViewQuestion9.text = getString(R.string.experiencing_challenge)
        textViewQuestion10.text = getString(R.string.are_you_part_of_spiritual)
        textViewQuestion11.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array2ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array2ArrayList.isNullOrEmpty()) {
                            textViewAnswer2.text = array2ArrayList.joinToString(",")
                        }
                    }

                    if (document["psychologicalIssue"].toString() != "") {
                        answer3 = document["psychologicalIssue"].toString()
                        if (answer3 != "") {
                            textViewAnswer3.text = answer3
                        }
                    }

                    if (document["emotionalIssue"].toString() != "") {
                        answer4 =
                            document["emotionalIssue"].toString()
                        if (answer4 != "") {
                            textViewAnswer4.text = answer4
                        }
                    }

                    if (document["spiritualIssue"].toString() != "") {
                        answer5 =
                            document["spiritualIssue"].toString()
                        if (answer5 != "") {
                            textViewAnswer5.text = answer5
                        }
                    }

                    if (document["currentExperience"] != null) {
                        array6ArrayList = document["currentExperience"] as ArrayList<String>
                        if (!array6ArrayList.isNullOrEmpty()) {
                            textViewAnswer6.text = array6ArrayList.joinToString(",")
                        }
                    }

                    if (document["familyRole"] != null) {
                        array7ArrayList = document["familyRole"] as ArrayList<String>
                        if (!array7ArrayList.isNullOrEmpty()) {
                            textViewAnswer7.text = array7ArrayList.joinToString(",")
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array8ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array8ArrayList.isNullOrEmpty()) {
                            textViewAnswer8.text = array8ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer9 = document["experiencingChallenge"].toString()
                        if (answer9 != "") {
                            textViewAnswer9.text = answer9
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array10ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array10ArrayList.isNullOrEmpty()) {
                            textViewAnswer10.text = array10ArrayList.joinToString(",")
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

    private fun humanistChaplainAssessmentQuestions() {
        textViewQuestion2.text = getString(R.string.humanist_chaplain_assessment_health_issue)
        textViewQuestion3.text = getString(R.string.do_you_feel_this_health_text)
        textViewQuestion4.text = getString(R.string.what_issues_do_you_have)
        textViewQuestion5.text = getString(R.string.what_social_issues_do_you_have)
        textViewQuestion6.text = getString(R.string.your_role_in_family)
        textViewQuestion7.text = getString(R.string.meaning_of_life)
        textViewQuestion8.text = getString(R.string.experiencing_challenge)
        textViewQuestion9.text = getString(R.string.are_you_part_of_spiritual)
        textViewQuestion10.text = getString(R.string.top_three_emotions)
        textViewQuestion11.visibility = View.GONE
        textViewAnswer11.visibility = View.GONE

        dbSave.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    if (document["whatReasonBringsYou"].toString() != "") {
                        answer1 = document["whatReasonBringsYou"].toString()
                        if (answer1 != "") {
                            textViewAnswer1.text = answer1
                        }
                    }

                    if (document["significantHealthIssue"].toString() != "") {
                        answer2 = document["significantHealthIssue"].toString()
                        if (answer2 != "") {
                            textViewAnswer2.text = answer2
                        }
                    }

                    if (document["physicalAndSpiritualResponsibility"].toString() != "") {
                        answer3 =
                            document["physicalAndSpiritualResponsibility"].toString()
                        if (answer3 != "") {
                            textViewAnswer3.text = answer3
                        }
                    }

                    if (document["familyIssues"] != null) {
                        array4ArrayList = document["familyIssues"] as ArrayList<String>
                        if (!array4ArrayList.isNullOrEmpty()) {
                            textViewAnswer4.text = array4ArrayList.joinToString(",")
                        }
                    }

                    if (document["socialIssues"] != null) {
                        array5ArrayList = document["socialIssues"] as ArrayList<String>
                        if (!array5ArrayList.isNullOrEmpty()) {
                            textViewAnswer5.text = array5ArrayList.joinToString(",")
                        }
                    }

                    if (document["familyRoles"] != null) {
                        array6ArrayList = document["familyRoles"] as ArrayList<String>
                        if (!array6ArrayList.isNullOrEmpty()) {
                            textViewAnswer6.text = array6ArrayList.joinToString(",")
                        }
                    }

                    if (document["meaningOfLife"] != null) {
                        array7ArrayList = document["meaningOfLife"] as ArrayList<String>
                        if (!array7ArrayList.isNullOrEmpty()) {
                            textViewAnswer7.text = array7ArrayList.joinToString(",")
                        }
                    }

                    if (document["experiencingChallenge"].toString() != "") {
                        answer8 = document["experiencingChallenge"].toString()
                        if (answer8 != "") {
                            textViewAnswer8.text = answer8
                        }
                    }

                    if (document["partOfSocialCommunity"] != null) {
                        array9ArrayList =
                            document["partOfSocialCommunity"] as ArrayList<String>
                        if (!array9ArrayList.isNullOrEmpty()) {
                            textViewAnswer9.text = array9ArrayList.joinToString(",")
                        }
                    }

                    if (document["topThreeEmotions"] != null) {
                        array10ArrayList =
                            document["topThreeEmotions"] as ArrayList<String>
                        if (!array10ArrayList.isNullOrEmpty()) {
                            textViewAnswer10.text = array10ArrayList.joinToString(",")
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
        val intent = Intent(this, ChaplainFutureAppointmentDetailsActivity::class.java)
        intent.putExtra("appointment_id", appointmentId)
        startActivity(intent)
        finish()
    }
}