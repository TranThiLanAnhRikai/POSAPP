package com.example.pos.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pos.data.entity.Customer

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers")
    fun getAllCustomers(): LiveData<List<Customer>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomer(customer: Customer)
    @Query("SELECT * FROM customers WHERE order_number = :orderNumber")
    fun getCustomer(orderNumber: Long): LiveData<Customer>
}