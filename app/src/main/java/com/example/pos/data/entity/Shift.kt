package com.example.pos_admin.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class Shift(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val shiftName: String,
    @ColumnInfo(name = "date")
    val shiftDate: String,
    @ColumnInfo(name = "shift_time")
    val shiftTime: Int
)