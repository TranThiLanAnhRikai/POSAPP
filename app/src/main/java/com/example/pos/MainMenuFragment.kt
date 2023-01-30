package com.example.pos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pos.model.MainMenuViewModel
import com.example.pos_admin.R
import com.example.pos_admin.databinding.FragmentMainMenuBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class MainMenuFragment : Fragment() {
    private var binding: FragmentMainMenuBinding? = null
    val mainMenuViewModel: MainMenuViewModel = MainMenuViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainMenuBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mainMenuFragment = this
        binding?.mainMenuViewModel = mainMenuViewModel
        mainMenuViewModel.getWeatherInfo().observe(viewLifecycleOwner, Observer {weatherInfo ->
            Log.d(TAG, "result ${mainMenuViewModel.result.value}")
            val tempMax = weatherInfo.main.temp_max
            val tempMin = weatherInfo.main.temp_min
            val humidity = weatherInfo.main.humidity
            val description = weatherInfo.weather[0].description
            val temp = weatherInfo.main.temp
            val sunrise = weatherInfo.sys.sunrise
            val sunset = weatherInfo.sys.sunset
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.JAPAN)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            val wind = weatherInfo.wind.speed
            binding?.tempMax?.text = "Max temp: $tempMax°C"
            binding?.tempMin?.text = "Min temp: $tempMin°C"
            binding?.weatherCondition?.text = description
            binding?.weatherTemp?.text = "$temp°C"
            binding?.sunrise?.text = simpleDateFormat.format(Date(sunrise * 1000L))
            binding?.sunset?.text = simpleDateFormat.format(Date(sunset * 1000L))
            binding?.humidity?.text = humidity.toString()
            binding?.wind?.text = wind.toString()
        })
        binding?.bottomNavigationView?.setOnNavigationItemSelectedListener {
            handleBottomNavigation(
                it.itemId
            )
        }
        binding?.bottomNavigationView?.selectedItemId = R.id.bottom_navigation_view
    }

    private fun handleBottomNavigation(
        menuItemId: Int
    ): Boolean = when(menuItemId) {
        R.id.main_menu_users_button -> {
            findNavController().navigate(R.id.action_mainMenuFragment_to_usersFragment)
            true
        }
        R.id.main_menu_menu_button -> {
            findNavController().navigate(R.id.action_mainMenuFragment_to_menuFragment)
            true
        }
        R.id.main_menu_notifications_button -> {
            findNavController().navigate(R.id.action_mainMenuFragment_to_notificationsFragment)
            true
        }
        else -> false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun toSalesAnalysisFragment() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_salesAnalysisFragment)
    }

    fun toShiftsFragment() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_shiftsFragment)
    }
}