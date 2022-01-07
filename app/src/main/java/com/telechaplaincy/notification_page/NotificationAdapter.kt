package com.telechaplaincy.notification_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.notification_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(
    var notificationList: List<NotificationModelClass>,
    private val clickListener: (NotificationModelClass) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var notifications: List<NotificationModelClass> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val myView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_layout, parent, false)

        return ChaplainsViewHolder(myView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChaplainsViewHolder).bind(notifications[position], clickListener)

    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun submitList(notificationList: List<NotificationModelClass>) {
        notifications = notificationList
    }

    class ChaplainsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val notificationTitle = itemView.title
        private val notificationMessage = itemView.message
        private val notificationTime = itemView.messageTime
        private val notificationImage = itemView.logo
        private val notificationCardView = itemView.notificationCardView
        private val notificationMessageRead = itemView.imageViewMessageRead

        fun bind(
            notificationInfo: NotificationModelClass,
            clickListener: (NotificationModelClass) -> Unit
        ) {
            notificationTitle.text = notificationInfo.title

            notificationMessage.text = notificationInfo.body

            val messageTime = notificationInfo.dateSent
            val dateFormatLocalZone = SimpleDateFormat("HH:mm - MMM dd, yyyy")
            dateFormatLocalZone.timeZone = TimeZone.getDefault()
            val timeRangeFirst = dateFormatLocalZone.format(Date(messageTime!!.toLong()))
            notificationTime.text = timeRangeFirst.toString()

            Picasso.get().load(R.drawable.logo).into(notificationImage)

            if (notificationInfo.messageRead!!) {
                notificationMessageRead.visibility = View.GONE
            }

            notificationCardView.setOnClickListener {
                clickListener(notificationInfo)
            }

        }
    }
}