package com.example.pos.data.repository

import androidx.lifecycle.LiveData
import com.example.pos.data.dao.CartItemDao
import com.example.pos.data.dao.CustomerDao
import com.example.pos.data.dao.MenuItemDao
import com.example.pos.data.dao.OrderDao
import com.example.pos.data.entity.CartItem
import com.example.pos.data.entity.Customer
import com.example.pos_admin.data.entity.MenuItem
import com.example.pos_admin.data.entity.Order


/*Repository for menu_item class */

class MenuItemRepository(private val menuItemDao: MenuItemDao, private val orderDao: OrderDao, private val cartItemDao: CartItemDao, private val customerDao: CustomerDao) {

    val items = menuItemDao.getAllMenuItems()

    fun getMenuItems(type: String): LiveData<List<MenuItem>> {
        return menuItemDao.getMenuItems(type)
    }

    fun getMenuItem(id: Int): LiveData<MenuItem> {

        return menuItemDao.getMenuItem(id)
    }

    suspend fun insertMenuItem(menuItem: MenuItem) {
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

    fun getMaxOrderNumber(date: String): LiveData<List<Order>> {
        val constructedDate = "%$date%"
        return orderDao.getMaxOrderNumber(constructedDate)
    }

    fun getAllOrders(): LiveData<List<Order>> {
        return orderDao.getAllOrders()
    }
    fun getOrders(status: String): LiveData<List<Order>> {
        return orderDao.getOrders(status)
    }
    fun getLatestOrders(latestDate: String): LiveData<List<Order>> {
        return orderDao.getOrdersByWeek(latestDate)
    }
    fun getTodayOrders(date: String): LiveData<List<Order>> {
        val constructedDate = "%$date%"
        return orderDao.getTodayOrders(constructedDate)
    }
    suspend fun delete(order: Order) {
        return orderDao.delete(order)
    }
    suspend fun update(order: Order) {
        return orderDao.update(order)
    }

    suspend fun insertCustomer(customer: Customer) {
        return customerDao.insertCustomer(customer)
    }

    fun getOrderById(orderNumber: Long): LiveData<Order> {
        return orderDao.getOrderById(orderNumber)
    }

    fun getCartItemsByOrderNumber(orderNumber: Long): LiveData<List<CartItem>> {
        return cartItemDao.getCart(orderNumber)
    }

    fun getCustomerByOrderNumber(orderNumber: Long): LiveData<Customer> {
        return customerDao.getCustomer(orderNumber)
    }

}
