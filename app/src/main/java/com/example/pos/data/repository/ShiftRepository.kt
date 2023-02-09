package com.example.pos_admin.data.repository

import androidx.lifecycle.LiveData
import com.example.pos_admin.data.dao.ShiftDao
import com.example.pos_admin.data.dao.UserDao
import com.example.pos_admin.data.entity.Shift
import com.example.pos_admin.data.entity.User


/*Repository for shift class */

class ShiftRepository(private val shiftDao: ShiftDao, private val userDao: UserDao) {

    val shifts = shiftDao.getAllShifts()

    fun getShifts(date: String, shift: Int): LiveData<List<Shift>> {
        return shiftDao.getShifts(date, shift)
    }

    fun getTodayShifts(date: String): LiveData<List<Shift>> {
        return shiftDao.getTodayShifts(date)
    }

    suspend fun insert(shift: Shift) {
        return shiftDao.insert(shift)
    }

    suspend fun update(shift: Shift) {
        return shiftDao.update(shift)
    }

    suspend fun delete(shift: Shift) {
        return shiftDao.delete(shift)
    }

    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

}
