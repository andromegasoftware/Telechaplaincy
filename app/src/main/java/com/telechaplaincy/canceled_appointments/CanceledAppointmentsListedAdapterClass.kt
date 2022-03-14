package com.telechaplaincy.canceled_appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.telechaplaincy.R
import com.telechaplaincy.appointment.AppointmentModelClass
import kotlinx.android.synthetic.main.canceled_appointments_listed_recyclerview_model_layout.view.*

class CanceledAppointmentsListedAdapterClass(
    var appointmentList: List<AppointmentModelClass>,
    private val clickListener: (AppointmentModelClass) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var appointments: List<AppointmentModelClass> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val myView = LayoutInflater.from(parent.context)
            .inflate(R.layout.canceled_appointments_listed_recyclerview_model_layout, parent, false)

        return ChaplainsViewHolder(myView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChaplainsViewHolder).bind(appointments[position], clickListener)

    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun submitList(appointmentList: List<AppointmentModelClass>) {
        appointments = appointmentList
    }

    fun updateList(appointmentList: List<AppointmentModelClass>) {
        appointments = appointmentList
        notifyDataSetChanged()
    }

    class ChaplainsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val appointmentNumber = itemView.canceled_appointment_no_textView
        private val patientName = itemView.canceled_appointment_patient_name_textView
        private val chaplainName = itemView.canceled_appointment_chaplain_name_textView
        private val appointmentStatus = itemView.canceled_appointment_status_textView
        private val appointmentDetailsButton = itemView.canceled_appointment_list_details_button

        fun bind(
            appointmentInfo: AppointmentModelClass,
            clickListener: (AppointmentModelClass) -> Unit
        ) {
            appointmentNumber.text = itemView.resources.getString(
                R.string.canceled_appointments_appointment_no_textView,
                appointmentInfo.appointmentId
            )

            patientName.text = itemView.resources.getString(
                R.string.canceled_appointments_patient_name_textView,
                appointmentInfo.patientName, appointmentInfo.patientSurname
            )

            chaplainName.text = itemView.resources.getString(
                R.string.canceled_appointments_chaplain_name_textView,
                appointmentInfo.chaplainName, appointmentInfo.chaplainSurname
            )

            appointmentStatus.text = itemView.resources.getString(
                R.string.canceled_appointments_appointment_status_textView,
                appointmentInfo.appointmentStatus
            )

            appointmentDetailsButton.setOnClickListener { clickListener(appointmentInfo) }
        }
    }

}