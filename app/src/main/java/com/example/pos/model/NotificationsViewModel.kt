package com.example.pos.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pos.data.repository.NotificationRepository
import com.example.pos.data.repository.OrderRepository
import com.example.pos_admin.data.entity.Notification
import com.example.pos_admin.data.repository.ShiftRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(private val notificationRepository: NotificationRepository): ViewModel() {

    fun insertNewNoti(notification: Notification) {
        viewModelScope.launch {
            notificationRepository.insert(notification)
        }
    }

    fun getAllNotifications(): LiveData<List<Notification>> {
        return notificationRepository.getAllNotifications()
    }
}

class NotificationsViewModelFactory(private val notificationRepository: NotificationRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationsViewModel(notificationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}