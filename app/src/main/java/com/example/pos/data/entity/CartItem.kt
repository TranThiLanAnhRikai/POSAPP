package com.example.pos.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItem (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "oder_number")
    val orderNumber: String,
    @ColumnInfo(name="img_path")
    val image: String,
    @ColumnInfo(name = "price")
    val price: String,
    @ColumnInfo(name = "quantity")
    val quantity: String,
    @ColumnInfo(name = "sub_total")
    val subTotal: String
    )