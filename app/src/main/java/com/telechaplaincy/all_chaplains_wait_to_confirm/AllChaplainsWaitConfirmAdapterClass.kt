package com.telechaplaincy.all_chaplains_wait_to_confirm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import kotlinx.android.synthetic.main.chaplain_selection_recyclerview_model_layout.view.*

class AllChaplainsWaitConfirmAdapterClass(
    var chaplainList: List<ChaplainUserProfile>,
    private val clickListener: (ChaplainUserProfile) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var chaplains: List<ChaplainUserProfile> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val myView = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_chaplains_listed_recyclerview_model_layout, parent, false)

        return ChaplainsViewHolder(myView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChaplainsViewHolder).bind(chaplains[position], clickListener)

    }

    override fun getItemCount(): Int {
        return if (chaplains.size < 20) {
            chaplains.size
        } else {
            20
        }

    }

    fun submitList(chaplainList: List<ChaplainUserProfile>) {
        chaplains = chaplainList
    }

    fun updateList(chaplainList: List<ChaplainUserProfile>) {
        chaplains = chaplainList
        notifyDataSetChanged()
    }

    class ChaplainsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val profileImage = itemView.chaplain_selection_profile_image_imageView
        private val chaplainName = itemView.chaplain_selection_list_name_textView
        private val chaplainField = itemView.chaplain_selection_list_field_textView
        private val chaplainUid = itemView.chaplain_selection_list_ethnic_textView
        private val chaplainViewDetailsButton = itemView.chaplain_selection_list_details_button

        fun bind(
            chaplainUserProfile: ChaplainUserProfile,
            clickListener: (ChaplainUserProfile) -> Unit
        ) {
            var cridentialTitles = ""
            if (chaplainUserProfile.credentialsTitle != null) {
                for (k in chaplainUserProfile.credentialsTitle) {
                    cridentialTitles += ", $k"
                }
            }
            chaplainName.text =
                chaplainUserProfile.addressingTitle + " " + chaplainUserProfile.name + " " +
                        chaplainUserProfile.surname + cridentialTitles

            var field = ""
            if (chaplainUserProfile.field != null) {
                for (k in chaplainUserProfile.field) {
                    field += "$k "
                }
            }
            chaplainField.text = field

            chaplainUid.text = chaplainUserProfile.userId

            val pictureUrl = chaplainUserProfile.profileImageLink
            Picasso.get().load(pictureUrl).placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24).into(profileImage)
            //Log.e("pictureUrl", pictureUrl)

            chaplainViewDetailsButton.setOnClickListener { clickListener(chaplainUserProfile) }
        }
    }
}