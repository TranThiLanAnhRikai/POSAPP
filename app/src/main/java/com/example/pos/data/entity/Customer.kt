package com.example.pos.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "customer_name")
    val customerName: String,
    @ColumnInfo(name = "order_number")
    val orderNumber: Long,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "address")
    val address: String?
    )