package com.example.pos.data.repository

import androidx.lifecycle.LiveData
import com.example.pos.const.Status
import com.example.pos.data.dao.OrderDao
import com.example.pos.data.entity.Order

class OrderRepository(private val orderDao: OrderDao) {
    fun getAllOrders(): LiveData<List<Order>> {
        return orderDao.getAllOrders()
    }
    fun getOrders(status: String): LiveData<List<Order>> {
        return orderDao.getOrders(status)
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