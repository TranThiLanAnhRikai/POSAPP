package com.example.pos.model

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.pos.data.entity.MenuItem
import com.example.pos_admin.data.repository.MenuItemRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat


class MenuViewModel(private val menuItemRepository: MenuItemRepository, private val context: Context): ViewModel() {
    val name = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val _price = MutableLiveData<String>()
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it.toDouble())
    }
    val quantity = MutableLiveData<String>()

    fun updateQuantity() {

    }


    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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

    fun addQuantity(itemId: String) {
        val currentQuantity = getSavedQuantity(itemId)
        val newQuantity = currentQuantity + 1
        Log.d(TAG, "newQuantity $newQuantity")
        saveQuantity(itemId, newQuantity)
    }

    fun decreaseQuantity(itemId: String) {
        val currentQuantity = getSavedQuantity(itemId)
        val newQuantity = if (currentQuantity > 0) currentQuantity - 1 else 0
        Log.d(TAG, "newQuantity $newQuantity")
        saveQuantity(itemId, newQuantity)
    }

    fun saveQuantity(itemId: String,quantity: Int) {
        sharedPreferences.edit().putInt(itemId, quantity).apply()
    }

    fun getSavedQuantity(itemId: String): Int {
        return sharedPreferences.getInt(itemId, 0)
    }
}




class MenuViewModelFactory(private val menuItemRepository: MenuItemRepository, private val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuItemRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
