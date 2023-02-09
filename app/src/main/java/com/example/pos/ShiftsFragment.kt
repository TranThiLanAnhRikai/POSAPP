package com.example.pos_admin

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.adapter.ShiftsAdapter
import com.example.pos_admin.databinding.FragmentShiftsBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pos.helper.CommonAdminHeaderHelper
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.entity.Shift
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.databinding.AdminCommonHeaderBinding
import com.example.pos_admin.model.*

class ShiftsFragment : Fragment() {
    private val shiftsViewModel: ShiftsViewModel by activityViewModels {
        ShiftsViewModelFactory(
            ShiftRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).shiftDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).userDao(),
            )
        )
    }
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy, MM, dd, EEEE", Locale.JAPAN)
    private var binding: FragmentShiftsBinding? = null
    private lateinit var headerHelper: CommonAdminHeaderHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentShiftsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = AdminCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonAdminHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)

        return fragmentBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.shiftsFragment = this
        binding?.shiftsViewModel = shiftsViewModel
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        shiftsViewModel.getTodayShifts(shiftsViewModel.today).observe(viewLifecycleOwner) {
            val adapter = ShiftsAdapter(requireContext(), it)
            val recyclerView = binding?.shifts
            recyclerView?.adapter = adapter
        }
        val spinner = binding?.dateSpinner
        shiftsViewModel.getAllShifts().observe(viewLifecycleOwner) { shifts ->
            val dates = mutableListOf<String>()
            shifts.forEach {
                dates.add(it.shiftDate)
            }
            val datesList = dates.distinct()
            val adapter = ArrayAdapter(requireContext(), R.layout.shifts_spinner_item, datesList)
            spinner?.adapter = adapter
            val index = datesList.indexOf(shiftsViewModel.today)
            spinner?.setSelection(index)
            var selectedDate = ""
            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedDate = formatter.format(calendar)
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedDate = parent?.getItemAtPosition(position).toString()
                }
            }
            binding?.shiftsTime?.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {
                    R.id.all_shifts -> {
                        shiftsViewModel.getTodayShifts(selectedDate)
                            .observe(viewLifecycleOwner) {
                                val adapter = ShiftsAdapter(requireContext(), it)
                                val recyclerView = binding?.shifts
                                recyclerView?.adapter = adapter
                            }
                    }
                    R.id.morning_shift -> {
                        showShifts(selectedDate, 0)
                    }
                    R.id.afternoon_shift -> {
                        showShifts(selectedDate, 1)
                    }
                    R.id.noon_shift -> {
                        showShifts(selectedDate, 2)
                    }
                }

            }


            /*val recyclerView = binding?.shifts
            shiftsViewModel.getAllShifts().observe(viewLifecycleOwner, Observer { shifts ->
                val adapter = ShiftsAdapter(requireContext(), shifts)
                recyclerView?.adapter = adapter
            })*/
        }
    }

/*    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val selectedTimeStamp = calendar.timeInMillis
        val selectedDate = formatter.format(selectedTimeStamp).toString()
        binding?.shiftsTime?.setOnCheckedChangeListener{ _, checkedId ->
            when(checkedId) {
                R.id.morning_shift -> {
                    showShifts(selectedDate, 1)
                }
                R.id.afternoon_shift -> {
                    showShifts(selectedDate, 2)
                }
                R.id.noon_shift -> {
                    showShifts(selectedDate, 3)
                }
            }
        }

    }*/

    private fun showShifts(selectedDate: String, shiftTime: Int) {
        shiftsViewModel.getAllShifts().observe(viewLifecycleOwner) { shifts ->
            val shiftsList = mutableListOf<Shift>()
            shifts.forEach { shift ->
                if ((shift.shiftDate == selectedDate) && (shift.shiftTime == shiftTime)) {
                    shiftsList.add(shift)
                }

            }

            val recyclerView = binding?.shifts
            val listOfShifts: List<Shift> = shiftsList

            val adapter = ShiftsAdapter(requireContext(), listOfShifts)
            recyclerView?.adapter = adapter
        }
    }


    fun goToNextScreen() {
        findNavController().navigate(R.id.action_shiftsFragment_to_addShiftsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}