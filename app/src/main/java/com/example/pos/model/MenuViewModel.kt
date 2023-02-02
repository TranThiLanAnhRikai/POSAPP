package com.example.pos.model

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.pos.const.Status
import com.example.pos.data.entity.CartItem
import com.example.pos.data.entity.Item

import com.example.pos.data.repository.MenuItemRepository
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.data.entity.MenuItem
import com.example.pos_admin.data.entity.Order
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MenuViewModel(private val menuItemRepository: MenuItemRepository): ViewModel() {
    val name = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val _price = MutableLiveData<String>()
    var total: Double = 0.0
    val selectedItems = MutableLiveData<MutableMap<Int, Item>>()
    var totalQuantity: Int = 0
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val currentDate = dateFormat.format(Date())
    var orderNumber = MutableLiveData<Long>()

    fun getAllOrders(): LiveData<List<Order>> {
        return menuItemRepository.getAllOrders()
    }
    fun updateOrder(order: Order) {
        viewModelScope.launch {
            menuItemRepository.update(order)
        }

    }
    fun getOrders(status: String): LiveData<List<Order>> {
        return menuItemRepository.getOrders(status)
    }

    fun insertItem() {
        viewModelScope.launch {
            menuItemRepository.insert(MenuItem(0, name.value!!, type.value!!, _price.value!!, image.value!!))
        }
        name.value = ""
        type.value = ""
        image.value = ""
        _price.value = ""

    }
    fun getOrderNumber(): LiveData<List<Order>>? {
        return menuItemRepository.getMaxOrderNumber(currentDate)
    }

    fun getAllMenuItems(): LiveData<List<MenuItem>> {
        return menuItemRepository.items
    }

    fun getMenuItems(type: String): LiveData<List<MenuItem>> {
        return menuItemRepository.getMenuItems(type)
    }


    fun decreaseQuantity(id: Int, item: Item) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap[id] = item
        selectedItems.postValue(currentMap)

    }
    fun increaseQuantity(id: Int, item: Item) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap[id] = item
        selectedItems.postValue(currentMap)

    }


    fun addToCart(id: Int, item: Item) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap[id] = item
        selectedItems.postValue(currentMap)

    }

    fun removeFromCart(id: Int) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap.remove(id)
        selectedItems.postValue(currentMap)

    }

    fun deleteOrder() {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap.clear()
        selectedItems.postValue(currentMap)

    }

    fun insertToOrderCustomerList() {
        val cartItems = selectedItems.value
        Log.d(TAG, "cartItems $cartItems")
        val keys = cartItems?.keys?.toList()
        Log.d(TAG, "keys $keys")
        var foodRevenue = 0.0
        var drinkRevenue = 0.0
        var dessertRevenue = 0.0
        cartItems?.forEach{
            when(it.value.type) {
                ItemType.FOOD.typeName -> foodRevenue += it.value.price * it.value.quantity!!
                ItemType.DRINK.typeName -> drinkRevenue += it.value.price * it.value.quantity!!
                else -> dessertRevenue += it.value.price * it.value.quantity!!
            }
        }
        viewModelScope.launch {
            menuItemRepository.insertToOrderList(Order(0, orderNumber.value!!, "%.2f".format(foodRevenue).toDouble(), "%.2f".format(drinkRevenue).toDouble(), "%.2f".format(dessertRevenue).toDouble(), totalQuantity,  "%.2f".format(total).toDouble(), Status.PROCESSING.toString(), null ))
        }
        viewModelScope.launch {
            keys?.forEach {key ->
                val item = selectedItems.value?.get(key)
                menuItemRepository.insertToCartItemList(CartItem(0, orderNumber.value!!, key, item?.quantity.toString()))
            }

        }

    }


}




class MenuViewModelFactory(private val menuItemRepository: MenuItemRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuItemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}