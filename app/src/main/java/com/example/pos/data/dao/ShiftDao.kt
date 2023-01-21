package com.example.pos_admin.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos_admin.data.entity.Shift

@Dao
interface ShiftDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shift: Shift)
    @Update
    suspend fun update(shift: Shift)
    @Delete
    suspend fun delete(shift: Shift)
    @Query("SELECT * FROM shifts")
    fun getAllShifts(): LiveData<List<Shift>>
    @Query("SELECT * FROM shifts WHERE date = :date AND shift_time = :shift")
    fun getShifts(date: String, shift: Int): LiveData<List<Shift>>
}