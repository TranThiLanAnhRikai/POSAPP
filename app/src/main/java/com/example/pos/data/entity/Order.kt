package com.example.pos_admin.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "order_number")
    var orderNumber: Long,
    @ColumnInfo(name = "food_revenue")
    var foodRevenue: Double?,
    @ColumnInfo(name = "drink_revenue")
    var drinkRevenue: Double?,
    @ColumnInfo(name = "dessert_revenue")
    var dessertRevenue: Double?,
    @ColumnInfo(name = "quantity")
    var quantity: Int,
    @ColumnInfo(name = "total")
    var total: Double,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "delivery_method")
    var deliveryMethod: String,
    @ColumnInfo(name = "payment_method")
    var paymentMethod: String,
    @ColumnInfo(name = "request")
    var request: String?,
)