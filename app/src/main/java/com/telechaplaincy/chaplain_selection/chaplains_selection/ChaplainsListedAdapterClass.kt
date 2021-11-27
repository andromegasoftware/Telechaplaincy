package com.telechaplaincy.chaplain_selection.chaplains_selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import com.telechaplaincy.chaplain_sign_activities.ChaplainUserProfile
import kotlinx.android.synthetic.main.chaplain_selection_recyclerview_model_layout.view.*

class ChaplainsListedAdapterClass(var chaplainList: List<ChaplainUserProfile>, private val clickListener: (ChaplainUserProfile) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var chaplains: List<ChaplainUserProfile> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val myView = LayoutInflater.from(parent.context).inflate(R.layout.chaplain_selection_recyclerview_model_layout, parent, false)

        return ChaplainsViewHolder(myView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChaplainsViewHolder).bind(chaplains[position], clickListener)

    }
    override fun getItemCount(): Int {

        return chaplains.size

    }

    fun submitList(chaplainList: List<ChaplainUserProfile>){
        chaplains = chaplainList
    }

    class ChaplainsViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){

        private val profileImage = itemView.chaplain_selection_profile_image_imageView
        private val chaplainName = itemView.chaplain_selection_list_name_textView
        private val chaplainField = itemView.chaplain_selection_list_field_textView
        private val chaplainEthnic = itemView.chaplain_selection_list_ethnic_textView
        private val chaplainReligion = itemView.chaplain_selection_list_religion_textView
        private val chaplainViewDetailsButton = itemView.chaplain_selection_list_details_button

        fun bind(chaplainUserProfile: ChaplainUserProfile, clickListener: (ChaplainUserProfile) -> Unit){
            var cridentialTitles = ""
            if (chaplainUserProfile.credentialsTitle != null){
                for (k in chaplainUserProfile.credentialsTitle){
                    cridentialTitles += ", $k"
                }
            }
            chaplainName.text = chaplainUserProfile.addressingTitle + " " + chaplainUserProfile.name+ " "+
                    chaplainUserProfile.surname + cridentialTitles

            var field = ""
            if (chaplainUserProfile.field != null){
                for (k in chaplainUserProfile.field){
                    field += "$k "
                }
            }
            chaplainField.text = field

            var ethnicBackground = ""
            if (chaplainUserProfile.ethnic != null){
                for (k in chaplainUserProfile.ethnic){
                    ethnicBackground += "$k "
                }
            }
            chaplainEthnic.text = ethnicBackground

            var religion = ""
            if (chaplainUserProfile.faith != null){
                for (k in chaplainUserProfile.faith){
                    religion += "$k "
                }
            }
            chaplainReligion.text = religion

            val pictureUrl = chaplainUserProfile.profileImageLink
            Picasso.get().load(pictureUrl).into(profileImage)
            //Log.e("pictureUrl", pictureUrl)

            chaplainViewDetailsButton.setOnClickListener { clickListener(chaplainUserProfile) }
        }
    }
}