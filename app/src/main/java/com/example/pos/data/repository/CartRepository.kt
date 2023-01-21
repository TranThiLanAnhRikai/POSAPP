package com.example.pos.data.repository


import androidx.lifecycle.LiveData
import com.example.pos.data.dao.CartItemDao
import com.example.pos.data.entity.CartItem


class CartItemRepository(private val cartItemDao: CartItemDao) {

    fun getCart(orderNumber: Int): LiveData<List<CartItem>> {
        return cartItemDao.getCart(orderNumber)
    }

    suspend fun insert(cartItem: CartItem) {
        return cartItemDao.insert(cartItem)
    }

    suspend fun update(cartItem: CartItem) {
        return cartItemDao.update(cartItem)
    }

    suspend fun delete(cartItem: CartItem) {
        return cartItemDao.delete(cartItem)
    }
    suspend fun deleteAll() {
        return cartItemDao.deleteAll()
    }

}