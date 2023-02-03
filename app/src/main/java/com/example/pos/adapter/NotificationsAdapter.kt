package com.example.pos.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pos_admin.R
import com.example.pos_admin.data.entity.Notification

class NotificationsAdapter(private val context: Context, private val notifications: List<Notification>)
    : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
        class NotificationViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
            val date: TextView = view.findViewById(R.id.noti_date)
            val content: TextView = view.findViewById(R.id.noti_content)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.notification, parent, false)
        return NotificationViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        Log.d(TAG, "notification in adapter $notification")
        holder.date.text = notification.date
        holder.content.text = notification.content
    }

    override fun getItemCount(): Int = notifications.size
}