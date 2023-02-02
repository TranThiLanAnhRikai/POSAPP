package com.example.pos.data.entity

import com.example.pos_admin.const.ItemType

data class Item (
    val name: String,
    val type: String,
    val quantity: Int?,
    val price: Double,
    val image: String,
    val subTotal: Double
)