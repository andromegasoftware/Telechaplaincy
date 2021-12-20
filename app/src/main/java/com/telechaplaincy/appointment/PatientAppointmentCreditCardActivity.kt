package com.telechaplaincy.appointment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.BillingAddressFields
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import com.telechaplaincy.patient_sign_activities.UserProfile
import com.telechaplaincy.payment.FirebaseEphemeralKeyProvider
import kotlinx.android.synthetic.main.activity_patient_appointment_credit_card.*
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

class PatientAppointmentCreditCardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var dbSaveAppointmentForPatient: DocumentReference
    private lateinit var dbSaveAppointmentForChaplain: DocumentReference
    private lateinit var dbSaveAppointmentForMainCollection: DocumentReference
    private val db = Firebase.firestore
    private var patientUserId: String = ""
    private var chaplainCollectionName: String = ""

    private var chaplainCategory = ""

    private var chaplainProfileFieldUserId: String = ""
    private var chaplainEarliestDate: String = ""
    private var patientSelectedTime = ""
    private var appointmentPrice = ""
    private var appointmentId = ""
    private var patientName = ""
    private var patientSurname = ""
    private var patientProfileImageLink = ""
    private var chaplainName = ""
    private var chaplainSurname = ""
    private var chaplainProfileImageLink = ""
    private var chaplainAddressingTitle = ""
    private var credentialTitleArrayList = ArrayList<String>()
    private var chaplainFieldArrayList = ArrayList<String>()
    private var patientTimeZone:String = ""
    private var chaplainUniqueUserId:String = ""
    private var patientUniqueUserId:String = ""

    private var isPaymentMethodSelected = false

    private var currentUserWillPay: FirebaseUser? = null
    private lateinit var paymentSession: PaymentSession
    private lateinit var selectedPaymentMethod: PaymentMethod
    private val stripe: Stripe by lazy { Stripe(applicationContext, "pk_test_51K1nMiJNPvINJFSH7tpyrBYdY2iD6O8FSt9B8QgtY4FSF2WfIDPajZuKd68AqvQVKI6pCzNo4OdHzZqkc0JPMlHt00sASHpQ8U")}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_credit_card)

        chaplainUniqueUserId = String.format("%010d", BigInteger(UUID.randomUUID().toString().replace("-", ""), 16))
        chaplainUniqueUserId = chaplainUniqueUserId.substring(chaplainUniqueUserId.length - 9)
        patientUniqueUserId = String.format("%010d", BigInteger(UUID.randomUUID().toString().replace("-", ""), 16))
        patientUniqueUserId = patientUniqueUserId.substring(patientUniqueUserId.length - 9)

        chaplainCollectionName = getString(R.string.chaplain_collection)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            currentUserWillPay = auth.currentUser!!
            patientUserId = user.uid
        }

        chaplainProfileFieldUserId = intent.getStringExtra("chaplain_id").toString()
        chaplainCategory = intent.getStringExtra("chaplain_category").toString()
        chaplainEarliestDate = intent.getStringExtra("readTime").toString()
        patientSelectedTime = intent.getStringExtra("patientSelectedTime").toString()
        appointmentPrice = intent.getStringExtra("appointmentPrice").toString()
        patientTimeZone = intent.getStringExtra("appointmentTimeZone").toString()

        dbSaveAppointmentForPatient =
            db.collection("patients").document(patientUserId)

        dbSaveAppointmentForChaplain =
            db.collection(chaplainCollectionName).document(chaplainProfileFieldUserId)

        dbSaveAppointmentForMainCollection = db.collection("appointment").document()

        readPatientData()
        readChaplainData()

        credit_card_page_price_textView.text = "$appointmentPrice$"

        payButton.setOnClickListener {
            if(isPaymentMethodSelected){
                confirmPayment(selectedPaymentMethod.id!!)
                credit_card_progressBar.visibility = View.VISIBLE
            }
            else{
                Toast.makeText(applicationContext, getString(R.string.payment_method__toast_message), Toast.LENGTH_LONG).show()
            }
        }

        paymentMethod.setOnClickListener {
            // Create the customer session and kick start the payment flow
            paymentSession.presentPaymentMethodSelection()
        }

        setupPaymentSession ()

    }

    private fun saveAppointmentInfoFireStore(){
        //payButton.isClickable = false

        appointmentId = dbSaveAppointmentForMainCollection.id
        val appointmentModelClass = AppointmentModelClass(
            appointmentId,
            patientUserId,
            chaplainProfileFieldUserId,
            appointmentPrice,
            patientSelectedTime,
            patientName,
            patientSurname,
            chaplainName,
            chaplainSurname,
            chaplainProfileImageLink,
            patientProfileImageLink,
            chaplainAddressingTitle,
            credentialTitleArrayList,
            chaplainFieldArrayList,
            patientTimeZone,
            chaplainUniqueUserId,
            patientUniqueUserId,
            null,
            null,
            chaplainCategory
        )

        dbSaveAppointmentForMainCollection.set(appointmentModelClass, SetOptions.merge())
            .addOnSuccessListener {

                dbSaveAppointmentForPatient
                    .collection("appointments").document(appointmentId)
                    .set(appointmentModelClass, SetOptions.merge())
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

                dbSaveAppointmentForChaplain
                    .collection("appointments").document(appointmentId)
                    .set(appointmentModelClass, SetOptions.merge())
                    .addOnSuccessListener {

                        payButton.isClickable = true
                        val intent = Intent(this, PatientAppointmentFinishActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

    }

    private fun readPatientData(){
        dbSaveAppointmentForPatient.get().addOnSuccessListener { document ->
            val result = document.toObject<UserProfile>()
            if (result != null) {
                patientName = result.name.toString()
                patientSurname = result.surname.toString()
                patientProfileImageLink = result.profileImage.toString()
            }
        }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }
    private fun readChaplainData(){
        dbSaveAppointmentForChaplain.get().addOnSuccessListener { document ->
            val result = document.toObject<ChaplainUserProfile>()
            if (result != null) {
                chaplainName = result.name.toString()
                chaplainSurname = result.surname.toString()
                chaplainProfileImageLink = result.profileImageLink.toString()
                chaplainAddressingTitle = result.addressingTitle.toString()
                if (!result.credentialsTitle.isNullOrEmpty()){
                    credentialTitleArrayList = result.credentialsTitle
                }
                if (!result.field.isNullOrEmpty()){
                    chaplainFieldArrayList = result.field
                }

            }
        }
            .addOnFailureListener { exception ->
                Log.d("deneme", "Error getting documents: ", exception)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PatientAppointmentSummaryActivity::class.java)
        intent.putExtra("chaplain_id", chaplainProfileFieldUserId)
        intent.putExtra("chaplain_category", chaplainCategory)
        intent.putExtra("readTime", chaplainEarliestDate)
        intent.putExtra("patientSelectedTime", patientSelectedTime)
        intent.putExtra("appointmentTimeZone", patientTimeZone)
        startActivity(intent)
        finish()
    }

    private fun confirmPayment(paymentMethodId: String) {
        payButton.isClickable = false

        val paymentCollection = Firebase.firestore
            .collection("stripe_customers").document(currentUserWillPay?.uid?:"")
            .collection("payments")

        // Add a new document with a generated ID
        val price = appointmentPrice + "00"
        paymentCollection.add(hashMapOf(
            "amount" to price,
            "currency" to "usd"
        ))
            .addOnSuccessListener { documentReference ->
                Log.d("payment", "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("payment", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("payment", "Current data: ${snapshot.data}")
                        val clientSecret = snapshot.data?.get("client_secret")
                        Log.d("payment", "Create paymentIntent returns $clientSecret")
                        clientSecret?.let {
                            stripe.confirmPayment(this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                paymentMethodId,
                                (it as String)
                            ))

                            //checkoutSummary.text = "Thank you for your payment"
                            Toast.makeText(applicationContext, "Payment Done!!", Toast.LENGTH_LONG).show()
                            //isPaymentDone = true
                            saveAppointmentInfoFireStore()
                        }
                    } else {
                        Log.e("payment", "Current payment intent : null")
                        payButton.isEnabled = true
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("payment", "Error adding document", e)
                payButton.isClickable = true
                checkoutSummary.text = getString(R.string.credit_card_reject_info)
                credit_card_progressBar.visibility = View.GONE
            }
    }

    private fun setupPaymentSession () {
        PaymentConfiguration.init(applicationContext, "pk_test_51K1nMiJNPvINJFSH7tpyrBYdY2iD6O8FSt9B8QgtY4FSF2WfIDPajZuKd68AqvQVKI6pCzNo4OdHzZqkc0JPMlHt00sASHpQ8U")

        // Setup Customer Session
        CustomerSession.initCustomerSession(this, FirebaseEphemeralKeyProvider())
        // Setup a payment session
        paymentSession = PaymentSession(this, PaymentSessionConfig.Builder()
            .setShippingInfoRequired(false)
            .setShippingMethodsRequired(false)
            .setBillingAddressFields(BillingAddressFields.None)
            .setShouldShowGooglePay(false)
            .build())

        paymentSession.init(
            object: PaymentSession.PaymentSessionListener {
                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    Log.d("PaymentSession", "PaymentSession has changed: $data")
                    Log.d("PaymentSession", "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}")

                    if (data.isPaymentReadyToCharge) {
                        Log.d("PaymentSession", "Ready to charge");
                        //payButton.isClickable = true
                        isPaymentMethodSelected = true
                        data.paymentMethod?.let {
                            Log.d("PaymentSession", "PaymentMethod $it selected")
                            var month = it.card?.expiryMonth.toString()
                            if(month.length == 1){
                                month = "0$month"
                            }
                            var year = it.card?.expiryYear.toString()
                            year = year.substring(2)
                            credit_card_type_textView.text = it.card?.brand
                            credit_card_last_four_textView.text = it.card?.last4
                            credit_card_exp_date_textView.text = "$month/$year"
                            // paymentMethod.text = "${it.card?.brand} card ends with ${it.card?.last4}"
                            selectedPaymentMethod = it
                        }
                    }
                }

                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    Log.d("PaymentSession",  "isCommunicating $isCommunicating")
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    Log.e("PaymentSession",  "onError: $errorCode, $errorMessage")
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        paymentSession.handlePaymentData(requestCode, resultCode, data ?: Intent())
    }

}