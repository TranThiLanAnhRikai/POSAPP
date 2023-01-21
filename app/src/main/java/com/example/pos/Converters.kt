package com.example.pos_admin

import androidx.room.TypeConverter
import android.util.Base64


class Converters {
    @TypeConverter
    fun fromByteArray(value: ByteArray): String {
        return Base64.encodeToString(value, Base64.DEFAULT)
    }

    @TypeConverter
    fun toByteArray(value: String): ByteArray {
        return Base64.decode(value, Base64.DEFAULT)
    }
}