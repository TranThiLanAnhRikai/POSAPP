package com.example.pos.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pos.data.entity.Order
import com.example.pos.data.repository.OrderRepository
import java.text.SimpleDateFormat
import java.util.*

class SalesViewModel(private val orderRepository: OrderRepository): ViewModel() {
    var revenueList = mutableListOf<Double>()
    var dates = mutableListOf<String>()
    var numberOfOrders = mutableListOf<Float>()
    var numberOfItems = mutableListOf<Int>()
    val latestDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.time)
    fun getLatestOrders(): LiveData<List<Order>> {
        return orderRepository.getLatestOrders(100, latestDate)
    }
/*
    fun getDatePatterns(): MutableList<String> {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val currentDate = dateFormat.format(calendar.time)

        val datePatterns = mutableListOf<String>()

        while (true) {
            datePatterns.add(calendar.get(Calendar.YEAR).toString() +
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1) +
                    "01")
            calendar.add(Calendar.MONTH, -1)
            if (dateFormat.format(calendar.time) <= currentDate) {
                break
            }
        }
        return datePatterns
    }
*/







}

class SalesViewModelFactory(private val orderRepository: OrderRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesViewModel(orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}