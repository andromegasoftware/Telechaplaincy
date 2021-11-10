package com.telechaplaincy.chaplain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_chaplain_calendar.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.*
import kotlinx.android.synthetic.main.activity_patient_appointment_personal_info.spinner_patient_marital_status
import java.util.*

class ChaplainCalendarActivity : AppCompatActivity() {

    private var chaplainTimeZone:String = ""
    private var chaplainAvailableTimes = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chaplain_calendar)

        chaplain_enter_calendar_progressBar.visibility = View.GONE

        chaplainTimeZoneSelection()

        chaplain_enter_calender_chip_group.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            chip.setChipBackgroundColorResource(R.color.colorAccent)
            chip.setTextColor(resources.getColor(R.color.colorPrimary))
        }

    }

    private fun chaplainTimeZoneSelection(){
        chaplainTimeZone = TimeZone.getDefault().id
        Log.d("timezone", chaplainTimeZone)
        val optionChaplainTimeZoneSelection = TimeZone.getAvailableIDs()
        spinnerChaplainTimeZone.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            optionChaplainTimeZoneSelection
        )
        spinnerChaplainTimeZone.setSelection(optionChaplainTimeZoneSelection.indexOf(chaplainTimeZone))
        spinnerChaplainTimeZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chaplainTimeZone = optionChaplainTimeZoneSelection[position]

                Log.d("timezone", chaplainTimeZone)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}