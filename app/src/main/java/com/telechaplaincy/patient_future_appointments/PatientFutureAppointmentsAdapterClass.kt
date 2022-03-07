package com.telechaplaincy.patient_future_appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.chaplain_selection_recyclerview_model_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PatientFutureAppointmentsAdapterClass(var chaplainList: List<PatientFutureAppointmentsModelClass>, private val clickListener: (PatientFutureAppointmentsModelClass) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var chaplains: List<PatientFutureAppointmentsModelClass> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val myView = LayoutInflater.from(parent.context).inflate(R.layout.patient_future_appointment_recyclerview_model_layout, parent, false)

        return ChaplainsViewHolder(myView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChaplainsViewHolder).bind(chaplains[position], clickListener)

    }
    override fun getItemCount(): Int {

        return chaplains.size

    }

    fun submitList(chaplainList: List<PatientFutureAppointmentsModelClass>){
        chaplains = chaplainList
    }

    class ChaplainsViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){

        private val profileImage = itemView.chaplain_selection_profile_image_imageView
        private val chaplainName = itemView.chaplain_selection_list_name_textView
        private val chaplainField = itemView.chaplain_selection_list_field_textView
        private val appointmentTime = itemView.chaplain_selection_list_religion_textView
        private val chaplainViewDetailsButton = itemView.chaplain_selection_list_details_button

        fun bind(patientFutureAppointmentsModelClass: PatientFutureAppointmentsModelClass, clickListener: (PatientFutureAppointmentsModelClass) -> Unit){
            var cridentialTitles = ""
            if (patientFutureAppointmentsModelClass.chaplainCredentialsTitle != null){
                for (k in patientFutureAppointmentsModelClass.chaplainCredentialsTitle){
                    cridentialTitles += ", $k"
                }
            }
            chaplainName.text = patientFutureAppointmentsModelClass.chaplainAddressingTitle + " " + patientFutureAppointmentsModelClass.chaplainName+ " "+
                    patientFutureAppointmentsModelClass.chaplainSurname + cridentialTitles

            var field = ""
            if (patientFutureAppointmentsModelClass.chaplainField != null){
                for (k in patientFutureAppointmentsModelClass.chaplainField){
                    field += "$k "
                }
            }
            chaplainField.text = field

            if (patientFutureAppointmentsModelClass.appointmentDate != null) {
                val appointmentTimeLong = patientFutureAppointmentsModelClass.appointmentDate
                val dateFormatLocalZone = SimpleDateFormat("EEE HH:mm aaa, dd-MM-yyyy z")
                val timeZone =
                    patientFutureAppointmentsModelClass.patientAppointmentTimeZone ?: "UTC"
                dateFormatLocalZone.timeZone = TimeZone.getTimeZone(timeZone)
                val chipTimeLocale = dateFormatLocalZone.format(Date(appointmentTimeLong.toLong()))
                appointmentTime.text = chipTimeLocale
            }

            val pictureUrl = patientFutureAppointmentsModelClass.chaplainProfileImageLink
            if (pictureUrl != null || pictureUrl != "" || pictureUrl != "null") {
                Picasso.get().load(pictureUrl).placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24).into(profileImage)
            }

            //Log.e("pictureUrl", pictureUrl)

            chaplainViewDetailsButton.setOnClickListener {
                clickListener(
                    patientFutureAppointmentsModelClass
                )
            }
        }
    }
}