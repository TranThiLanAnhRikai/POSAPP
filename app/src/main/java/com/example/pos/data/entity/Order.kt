package com.example.pos.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pos.const.Status

@Entity(tableName = "orders")
data class Order (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "total")
    val total: String,
    @ColumnInfo(name = "status")
    val status: Status
)