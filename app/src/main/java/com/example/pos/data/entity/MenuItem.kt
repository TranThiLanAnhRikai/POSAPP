package com.example.pos_admin.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pos_admin.const.ItemType
import java.io.File

@Entity(tableName = "menu_items")
data class MenuItem (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "price")
    val price: String,
    @ColumnInfo(name = "img_path")
    val image: String
)
