package com.example.pos.data.entity

import androidx.lifecycle.LiveData
import com.example.pos.data.dao.CustomerDao

class CustomerRepository(private val customerDao: CustomerDao) {

    fun getAllCustomers(): LiveData<List<Customer>> {
        return customerDao.getAllCustomers()
    }

    fun getCustomer(orderNumber: Long): LiveData<Customer> {
        return customerDao.getCustomer(orderNumber)
    }

    suspend fun insertCustomer(customer: Customer) {
        return customerDao.insertCustomer(customer)
    }
}