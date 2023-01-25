package com.example.pos.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pos.data.entity.MenuItem

@Dao
interface MenuItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(menuItem: MenuItem)
    @Update
    suspend fun update(menuItem: MenuItem)
    @Delete
    suspend fun delete(menuItem: MenuItem)
    @Query("SELECT * FROM menu_items")
    fun getAllMenuItems(): LiveData<List<MenuItem>>
    @Query("SELECT * FROM menu_items WHERE id = :id")
    fun getMenuItem(id: Int): LiveData<MenuItem>
    @Query("SELECT * FROM menu_items WHERE type = :type")
    fun getMenuItems(type: String): LiveData<List<MenuItem>>
}