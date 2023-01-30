package com.example.pos.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.pos.network.PosApi
import com.example.pos.network.WeatherInfo
import java.text.SimpleDateFormat
import java.util.*

class MainMenuViewModel: ViewModel() {

    private val calendar: Calendar = Calendar.getInstance()
    private val currentDateTime: Date = calendar.time

    private val dateFormat = SimpleDateFormat("EEEE, yyyy/MM/dd")
    val formattedDateTime: String = dateFormat.format(currentDateTime)
    val result = MutableLiveData<WeatherInfo>()
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status
    val status: LiveData<String> = _status
    fun getWeatherInfo(): MutableLiveData<WeatherInfo> {
        viewModelScope.launch {
            try {
                result.value = PosApi.retrofitService.getWeather()
                Log.d(TAG, "weather $result")
                _status.value = "Success retrieved"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
        return result
    }

}