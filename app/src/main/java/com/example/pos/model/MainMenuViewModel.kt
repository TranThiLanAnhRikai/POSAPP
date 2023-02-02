package com.example.pos.model

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.data.repository.OrderRepository
import kotlinx.coroutines.launch
import com.example.pos.network.PosApi
import com.example.pos.network.WeatherInfo
import com.example.pos_admin.data.entity.Order
import java.text.SimpleDateFormat
import java.util.*

class MainMenuViewModel(private val orderRepository: OrderRepository): ViewModel() {
    lateinit var formattedDateTime: String

    fun getCurrentDate() {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
        val currentDateTime: Date = calendar.time
        val dateFormat = SimpleDateFormat("EEEE, yyyy/MM/dd")
        @SuppressLint("SimpleDateFormat")
        formattedDateTime = dateFormat.format(currentDateTime)
    }





    @SuppressLint("SimpleDateFormat")
    val currentDateFormat = SimpleDateFormat("yyyyMMdd")
    val currentDate = currentDateFormat.format(Date())
    val result = MutableLiveData<WeatherInfo>()
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status
    val status: LiveData<String> = _status


    fun getTodayOrders(): LiveData<List<Order>> {
        return orderRepository.getTodayOrders(currentDate)
    }
    fun getWeatherInfo(): MutableLiveData<WeatherInfo> {
        viewModelScope.launch {
            try {
                result.value = PosApi.retrofitService.getWeather()
                _status.value = "Success retrieved"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
        return result
    }

}

class MainMenuViewModelFactory(private val orderRepository: OrderRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainMenuViewModel(orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}