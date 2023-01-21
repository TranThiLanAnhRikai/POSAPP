package com.example.pos_admin.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pos_admin.data.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)
    @Update
    suspend fun update(user: User)
    @Delete
    suspend fun delete(user: User)
    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>
    @Query("SELECT * FROM users WHERE user_first_code = :code")
    fun getUser(code: String): LiveData<User>
}