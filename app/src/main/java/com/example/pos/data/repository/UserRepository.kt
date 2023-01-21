package com.example.pos_admin.data.repository

import androidx.lifecycle.LiveData
import com.example.pos_admin.data.dao.UserDao
import com.example.pos_admin.data.entity.User

/*Repository for user class */

class UserRepository(private val userDao: UserDao) {

        val users = userDao.getAllUsers()

    fun getUser(code: String): LiveData<User> {
        return userDao.getUser(code)
    }

        suspend fun insert(user: User) {
            return userDao.insert(user)
        }

        suspend fun update(user: User) {
            return userDao.update(user)
        }

        suspend fun delete(user: User) {
            return userDao.delete(user)
        }

}