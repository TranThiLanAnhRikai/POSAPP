package com.example.pos.model

import androidx.lifecycle.*
import com.example.pos.data.entity.Item
import com.example.pos.data.entity.MenuItem
import com.example.pos_admin.data.repository.MenuItemRepository
import kotlinx.coroutines.launch
import java.util.*


class MenuViewModel(private val menuItemRepository: MenuItemRepository): ViewModel() {
    val name = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val _price = MutableLiveData<String>()
    var total: Double = 0.0
    val orderNumber = System.currentTimeMillis().toString()
    val selectedItems = MutableLiveData<MutableMap<Int, Item>>()

/*    fun calculateTotal() {
        val items = selectedItems.values
        items.forEach{ item ->
            subTotal += (item.price.toDouble() * item.quantity!!)
        }
        total = subTotal.toString()
    }*/

    fun insertItem() {
        viewModelScope.launch {
            menuItemRepository.insert(MenuItem(0, name.value!!, type.value!!, _price.value!!, image.value!!))
        }
        name.value = ""
        type.value = ""
        image.value = ""
        _price.value = ""

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
        /*selectedItems.value?.set(id, item)*/
    }
    fun increaseQuantity(id: Int, item: Item) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap[id] = item
        selectedItems.postValue(currentMap)
        /*selectedItems.value?.set(id, item)*/
    }


    fun addToCart(id: Int, item: Item) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap[id] = item
        selectedItems.postValue(currentMap)
   /*     selectedItems.value?.set(id, item)*/
    }

    fun removeFromCart(id: Int) {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap.remove(id)
        selectedItems.postValue(currentMap)
/*        selectedItems.value?.remove(id)*/
    }

    fun deleteOrder() {
        val currentMap = selectedItems.value ?: mutableMapOf()
        currentMap.clear()
        selectedItems.postValue(currentMap)
/*        selectedItems.value?.clear()*/
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
