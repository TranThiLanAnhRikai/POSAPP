package com.example.pos.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pos.const.Status

@Entity(tableName = "orders")
data class Order (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "order_number")
    var orderNumber: Long,
    @ColumnInfo(name = "quantity")
    var quantity: String,
    @ColumnInfo(name = "total")
    var total: String,
    @ColumnInfo(name = "status")
    var status: String
)