package com.example.pos.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*

import com.example.pos_admin.data.entity.Order

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    fun getAllOrders(): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status")
    fun getOrders(status: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE order_number LIKE :date ORDER BY order_number DESC")
    fun getMaxOrderNumber(date: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE order_number LIKE :date")
    fun getTodayOrders(date: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE substr(order_number, 1, 8) >= :latestDate")
    fun getOrdersByWeek(latestDate: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE substr(order_number, 1, 6) >= :latestDate")
    fun getOrdersByMonth(latestDate: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE order_number = :orderNumber")
    fun getOrderById(orderNumber: Long): LiveData<Order>

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(order: Order)
}