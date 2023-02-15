package com.example.pos.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pos.data.repository.OrderRepository
import com.example.pos_admin.data.entity.Order
import java.text.SimpleDateFormat
import java.util.*

class SalesViewModel(private val orderRepository: OrderRepository) : ViewModel() {
    var revenueList = mutableListOf<Double>()
    var dates = mutableListOf<String>()
    var months = mutableListOf<String>()
    var numberOfOrders = mutableListOf<Int>()
    var numberOfItems = mutableListOf<Int>()
    var foodRevenueList = mutableListOf<Double>()
    var drinkRevenueList = mutableListOf<Double>()
    var dessertRevenueList = mutableListOf<Double>()
    val latestDate =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -7)
        }.time)
    val month =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().apply {
            set(Calendar.MONTH, -12)
        }.time)

    fun getOrdersByWeek(): LiveData<List<Order>> {
        return orderRepository.getOrdersByWeek(latestDate)
    }

    fun getOrdersByMonth(): LiveData<List<Order>> {
        return orderRepository.getOrdersByMonth(month)
    }


}

class SalesViewModelFactory(private val orderRepository: OrderRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesViewModel(orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}