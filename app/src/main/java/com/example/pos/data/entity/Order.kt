package com.example.pos.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "order_number")
    var orderNumber: Long,
    @ColumnInfo(name = "quantity")
    var quantity: Int,
    @ColumnInfo(name = "total")
    var total: Double,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "note")
    var note: String?
)