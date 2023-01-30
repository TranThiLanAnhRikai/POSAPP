package com.example.pos.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.pos.data.dao.CartItemDao
import com.example.pos.data.dao.MenuItemDao
import com.example.pos.data.dao.OrderDao
import com.example.pos.data.entity.CartItem
import com.example.pos.data.entity.MenuItem
import com.example.pos.data.entity.Order


/*Repository for menu_item class */

class MenuItemRepository(private val menuItemDao: MenuItemDao, private val orderDao: OrderDao, private val cartItemDao: CartItemDao) {

    val items = menuItemDao.getAllMenuItems()

    fun getMenuItems(type: String): LiveData<List<MenuItem>> {
        return menuItemDao.getMenuItems(type)
    }

    fun getMenuItem(id: Int): LiveData<MenuItem> {

        return menuItemDao.getMenuItem(id)
    }

    suspend fun insert(menuItem: MenuItem) {
        return menuItemDao.insert(menuItem)
    }

    suspend fun update(menuItem: MenuItem) {
        return menuItemDao.update(menuItem)
    }

    suspend fun delete(menuItem: MenuItem) {
        return menuItemDao.delete(menuItem)
    }

    suspend fun insertToOrderList(order: Order) {
        return orderDao.insert(order)
    }
    suspend fun insertToCartItemList(cartItem: CartItem) {
        return cartItemDao.insert(cartItem)
    }

}