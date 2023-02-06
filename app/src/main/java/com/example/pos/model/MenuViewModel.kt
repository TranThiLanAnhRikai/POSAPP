package com.example.pos.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.pos.const.Status
import com.example.pos.data.entity.CartItem
import com.example.pos.data.entity.Customer
import com.example.pos.data.entity.Item

import com.example.pos.data.repository.MenuItemRepository
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.data.entity.MenuItem
import com.example.pos_admin.data.entity.Order
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MenuViewModel(private val menuItemRepository: MenuItemRepository) : ViewModel() {
    val itemName = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val _price = MutableLiveData<String>()
    var total = 0.0
    var totalWithDelivery = MutableLiveData<Double>()
    val selectedItems = MutableLiveData<MutableMap<Int, Item>>()
    var totalQuantity: Int = 0
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val currentDate = dateFormat.format(Date())
    var orderNumber = MutableLiveData<Long>()
    val customerName = MutableLiveData<String>()
    val customerPhoneNumber = MutableLiveData<String>()
    val customerAddress = MutableLiveData<String>()
    val customerZipCode = MutableLiveData<String?>()
    val pickupTime = MutableLiveData<String?>()
    val request = MutableLiveData<String?>()
    val deliveryMethod = MutableLiveData<String>()
    val paymentMethod = MutableLiveData<String>()

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
            menuItemRepository.insertMenuItem(
                MenuItem(
                    0,
                    itemName.value!!,
                    type.value!!,
                    _price.value!!,
                    image.value!!
                )
            )
        }

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
        val keys = cartItems?.keys?.toList()
        var foodRevenue = 0.0
        var drinkRevenue = 0.0
        var dessertRevenue = 0.0
        cartItems?.forEach {
            when (it.value.type) {
                ItemType.FOOD.typeName -> foodRevenue += it.value.price * it.value.quantity!!
                ItemType.DRINK.typeName -> drinkRevenue += it.value.price * it.value.quantity!!
                else -> dessertRevenue += it.value.price * it.value.quantity!!
            }
        }
        viewModelScope.launch {
            menuItemRepository.insertToOrderList(
                Order(
                    0,
                    orderNumber.value!!,
                    foodRevenue,
                    drinkRevenue,
                    dessertRevenue,
                    totalQuantity,
                    totalWithDelivery.value!!,
                    Status.PROCESSING.toString(),
                    deliveryMethod.value!!,
                    paymentMethod.value!!,
                    request.value
                )
            )
        }
        viewModelScope.launch {
            keys?.forEach { key ->
                val item = selectedItems.value?.get(key)
                menuItemRepository.insertToCartItemList(
                    CartItem(
                        0,
                        orderNumber.value!!,
                        key,
                        item?.quantity.toString()
                    )
                )
            }

        }

    }

    fun insertCustomer() {
        val address = customerAddress.value + " Zip code: ${customerZipCode.value}"
        viewModelScope.launch {
            menuItemRepository.insertCustomer(
                Customer(
                    0,
                    customerName.value!!,
                    orderNumber.value!!,
                    customerPhoneNumber.value!!,
                    address
                )
            )
        }
    }


}


class MenuViewModelFactory(private val menuItemRepository: MenuItemRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuItemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}