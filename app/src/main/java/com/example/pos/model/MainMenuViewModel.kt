package com.example.pos.model

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.data.repository.OrderRepository
import kotlinx.coroutines.launch
import com.example.pos.network.PosApi
import com.example.pos.network.WeatherInfo
import com.example.pos_admin.data.entity.Order
import com.example.pos_admin.data.entity.Shift
import com.example.pos_admin.data.repository.ShiftRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainMenuViewModel(private val orderRepository: OrderRepository, private val shiftRepository: ShiftRepository): ViewModel() {
    val formattedDateTime = MutableLiveData<String>()
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate() {
         formattedDateTime.value = ZonedDateTime.now(ZoneId.of("Asia/Tokyo")).format(DateTimeFormatter.ofPattern("yyyy MMM dd, EEEE"))
    }



    fun getShifts(date: String, shift: Int): LiveData<List<Shift>> {
        return shiftRepository.getShifts(date, shift)
    }

    val result = MutableLiveData<WeatherInfo>()
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status
    val status: LiveData<String> = _status


    fun getTodayOrders(): LiveData<List<Order>> {
        return orderRepository.getTodayOrders(formattedDateTime.value!!)
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

class MainMenuViewModelFactory(private val orderRepository: OrderRepository, private val shiftRepository: ShiftRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainMenuViewModel(orderRepository, shiftRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}