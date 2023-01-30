package com.example.pos.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItem (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "oder_number")
    val orderNumber: Long,
    @ColumnInfo (name = "item_id")
    val itemId: Int,
    @ColumnInfo(name = "quantity")
    val quantity: String,
    )