package com.example.pos.data.repository

import androidx.lifecycle.LiveData
import com.example.pos.data.dao.OrderDao
import com.example.pos_admin.data.entity.Order


class OrderRepository(private val orderDao: OrderDao) {
    fun getAllOrders(): LiveData<List<Order>> {
        return orderDao.getAllOrders()
    }
    fun getOrders(status: String): LiveData<List<Order>> {
        return orderDao.getOrders(status)
    }
    fun getOrdersByWeek(date: String): LiveData<List<Order>> {
        return orderDao.getOrdersByWeek(date)
    }
    fun getOrdersByMonth(month: String): LiveData<List<Order>> {
        return orderDao.getOrdersByMonth(month)
    }
    fun getTodayOrders(date: String): LiveData<List<Order>> {
        val constructedDate = "%$date%"
        return orderDao.getTodayOrders(constructedDate)
    }
    suspend fun delete(order: Order) {
        return orderDao.delete(order)
    }
    suspend fun update(order: Order) {
        return orderDao.update(order)
    }
    suspend fun insert(order: Order) {
        return orderDao.insert(order)
    }
}
