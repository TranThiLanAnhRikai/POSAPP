package com.example.pos_admin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos.data.repository.OrderRepository
import com.example.pos.model.MainMenuViewModel
import com.example.pos.model.MainMenuViewModelFactory
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.databinding.FragmentMainMenuBinding
import java.text.SimpleDateFormat
import java.util.*


/** Admin側のメイン画面
 * 当日のセール数字、スタッフのリスト、天気情報を表示する
 */
class MainMenuFragment : Fragment() {
    private var binding: FragmentMainMenuBinding? = null
    private val mainMenuViewModel: MainMenuViewModel by activityViewModels {
        MainMenuViewModelFactory(
            OrderRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao()

            ),
            ShiftRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).shiftDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMainMenuBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        return fragmentBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mainMenuFragment = this
        binding?.mainMenuViewModel = mainMenuViewModel

        mainMenuViewModel.getCurrentDate()
        mainMenuViewModel.formattedDateTime.observe(viewLifecycleOwner) {
            binding?.dateTime?.text = "Today is $it"
        }

        // Shared Preferences でユーザー名を取得して、表示する
        val prefs = context?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val username = prefs?.getString("username", "")
        binding?.welcomeText?.text = "Welcome back, $username"
        // ボトムナビゲーションバーをバインドする
        binding?.bottomNavigationView?.setOnNavigationItemSelectedListener {
            handleBottomNavigation(
                it.itemId
            )
        }
        // APIで当日の天気情報をゲットして、表示する
        mainMenuViewModel.getWeatherInfo().observe(viewLifecycleOwner) { weatherInfo ->
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
        }


        // 当日のセール数字を表示する
        mainMenuViewModel.getTodayOrders().observe(viewLifecycleOwner) { orders ->
            binding?.totalNumberOfOrders?.text = "Total number of orders: ${orders.size}"
            var totalRevenue = 0.0
            var totalNoOfItems = 0
            orders.forEach { order ->
                totalRevenue += order.total
                totalNoOfItems += order.quantity
            }
            binding?.totalSales?.text = "Total Sales: $${String.format("%.2f", totalRevenue)}"
            binding?.totalNumberOfItems?.text = "Total number of items: $totalNoOfItems"
        }

        // 当日のスタッフを表示する
        val buttonsContainer = binding?.todayShifts
        buttonsContainer?.forEach { it ->
            it.setOnClickListener {
                mainMenuViewModel.getShifts(
                    mainMenuViewModel.formattedDateTime.value!!,
                    it.tag.toString().toInt()
                ).observe(viewLifecycleOwner) { shifts ->
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Staff")
                    val staffs = mutableListOf<String>()
                    shifts.forEach { shift ->
                        staffs.add(shift.shiftName)
                    }
                    val adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, staffs)
                    builder.setAdapter(adapter) { _, _ ->
                    }
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }

    }


    // ボトムナビゲーションバーを処理する
    private fun handleBottomNavigation(
        menuItemId: Int
    ): Boolean = when (menuItemId) {
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

    // この画面に戻ると、ホームボタンをハイライトする
    override fun onResume() {
        super.onResume()
        binding?.bottomNavigationView?.menu?.findItem(R.id.main_menu_home_button)?.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Sales Analysis 画面にナビゲートする
    fun toSalesAnalysis() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_salesAnalysisFragment)
    }

    // Shifts 画面にナビゲートする
    fun toShifts() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_shiftsFragment)
    }
}