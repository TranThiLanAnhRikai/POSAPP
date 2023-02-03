package com.example.pos.data.repository

import androidx.lifecycle.LiveData
import com.example.pos_admin.data.dao.NotificationDao
import com.example.pos_admin.data.entity.Notification

class NotificationRepository(private val notificationDao: NotificationDao) {
    suspend fun insert(notification: Notification) {
        return notificationDao.insert(notification)
    }

    fun getAllNotifications(): LiveData<List<Notification>> {
        return notificationDao.getAllNotifications()
    }

}