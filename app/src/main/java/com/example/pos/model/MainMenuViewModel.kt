package com.example.pos_admin.model

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainMenuViewModel: ViewModel() {

    private val calendar: Calendar = Calendar.getInstance()
    private val currentDateTime: Date = calendar.time

    private val dateFormat = SimpleDateFormat("EEEE, yyyy/MM/dd")
    val formattedDateTime: String = dateFormat.format(currentDateTime)

/*    val API: String = "6a238ea1bff80bfc12ddc7be3d2a0641"
    val CITY: String = "Tokyo,jp"
    val url = "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API"

    val jsonString = url.readText()
    val jsonObj = JSONObject(jsonString)
    val main = jsonObj.getJSONObject("main")
    val sys = jsonObj.getJSONObject("sys")
    val wind = jsonObj.getJSONObject("wind")
    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
    val temp = main.getString("temp")+"°C"
    val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
    val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
    val windSpeed = wind.getString("speed")
    val humidity = main.getString("humidity")*/

}