package com.example.pos.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pos.data.entity.Order

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    fun getAllOrders(): LiveData<List<Order>>
    @Query("SELECT * FROM orders WHERE status = :status")
    fun getOrders(status: String): LiveData<List<Order>>
    @Update
    suspend fun update(order: Order)
    @Delete
    suspend fun delete(order: Order)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(order: Order)
}