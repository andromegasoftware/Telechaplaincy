package com.telechaplaincy.all_patients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.patient_sign_activities.UserProfile
import kotlinx.android.synthetic.main.chaplain_selection_recyclerview_model_layout.view.*

class AllPatientsListedAdapterClass(
    var patientList: List<UserProfile>,
    private val clickListener: (UserProfile) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var patients: List<UserProfile> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val myView = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_chaplains_listed_recyclerview_model_layout, parent, false)

        return ChaplainsViewHolder(myView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChaplainsViewHolder).bind(patients[position], clickListener)

    }

    override fun getItemCount(): Int {
        return patients.size
    }

    fun submitList(patientList: List<UserProfile>) {
        patients = patientList
    }

    fun updateList(patientList: List<UserProfile>) {
        patients = patientList
        notifyDataSetChanged()
    }

    class ChaplainsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val profileImage = itemView.chaplain_selection_profile_image_imageView
        private val patientName = itemView.chaplain_selection_list_name_textView
        private val patientField = itemView.chaplain_selection_list_field_textView
        private val patientUid = itemView.chaplain_selection_list_ethnic_textView
        private val patientViewDetailsButton = itemView.chaplain_selection_list_details_button

        fun bind(
            patientUserProfile: UserProfile,
            clickListener: (UserProfile) -> Unit
        ) {
            patientName.text = patientUserProfile.name + " " + patientUserProfile.surname


            patientUid.text = patientUserProfile.userId

            patientField.visibility = View.GONE

            val pictureUrl = patientUserProfile.profileImage
            if (pictureUrl != "" && pictureUrl != "null") {
                Picasso.get().load(pictureUrl).placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24).into(profileImage)
                //Log.e("pictureUrl", pictureUrl)
            }

            patientViewDetailsButton.setOnClickListener { clickListener(patientUserProfile) }
        }
    }
}