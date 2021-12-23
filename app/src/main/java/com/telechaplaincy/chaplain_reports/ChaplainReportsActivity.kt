package com.telechaplaincy.chaplain_reports

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_profile.ChaplainProfileActivity
import kotlinx.android.synthetic.main.activity_chaplain_reports.*

class ChaplainReportsActivity : AppCompatActivity() {

    private var chaplainSpinnerSelection: String = ""
    private var totalIncome: String = ""
    private var totalAppointmentsNumber: String = ""
    private var priceForPerAppointment: String = ""
    private var commissionForPerAppointment: String = ""
    private var canceledAppointmentsNumberByYou: String = ""
    private var canceledAppointmentsNumberByPatient: String = ""
    private var fineForPerCanceledAppointment: String = ""
    private var fineTotalForCanceledAppointment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_reports)

        chaplainReportsFrequencySelectionMethod()
    }

    private fun chaplainReportsFrequencySelectionMethod() {
        val optionsFrequencies = resources.getStringArray(R.array.reports_frequency)
        spinner_chaplain_reports_frequency.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionsFrequencies
        )
        spinner_chaplain_reports_frequency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    chaplainSpinnerSelection = optionsFrequencies[position]
                    chaplain_reports_date_frequency_textView.text = chaplainSpinnerSelection
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChaplainProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}