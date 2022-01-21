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
    private var chaplainCategory: String = "CollegeChaplains"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_assessment_questions_answers)

        appointmentId = intent.getStringExtra("appointment_id").toString()
        patientUserId = intent.getStringExtra("patientUserId").toString()
        //chaplainCategory = intent.getStringExtra("chaplainCategory").toString()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
        }

        dbSave = db.collection("patients").document(patientUserId)
            .collection("assessment_questions").document(appointmentId)

    }

    override fun onStart() {
        super.onStart()
        readChaplainSelectedDates()
    }

    private fun readChaplainSelectedDates() {
        if (chaplainCategory == "HumanistChaplains") {
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

        } else if (chaplainCategory == "HealthChaplains") {

        } else if (chaplainCategory == "HospiceChaplains") {

        } else if (chaplainCategory == "MentalChaplains") {

        }
        //crisis chaplain and mental health Chaplain assessment questions are same
        else if (chaplainCategory == "CrisisChaplains") {

        } else if (chaplainCategory == "PalliativeChaplains") {

        } else if (chaplainCategory == "PetChaplains") {

        } else if (chaplainCategory == "PrisonChaplains") {

        } else if (chaplainCategory == "PediatricChaplains") {

        } else if (chaplainCategory == "CollegeChaplains") {
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

        } else if (chaplainCategory == "CorporateChaplains") {

        } else if (chaplainCategory == "MilitaryChaplains") {

        } else if (chaplainCategory == "LawChaplains") {

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