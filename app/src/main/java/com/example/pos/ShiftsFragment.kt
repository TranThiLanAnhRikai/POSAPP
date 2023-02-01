package com.example.pos_admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.adapter.ShiftsAdapter
import com.example.pos_admin.databinding.FragmentShiftsBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.entity.Shift
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.model.*

class ShiftsFragment : Fragment() {
    private lateinit var shiftsViewModel: ShiftsViewModel
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy, MM, dd, EEEE", Locale.US)
    private var binding: FragmentShiftsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentShiftsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        //Get shiftsViewModel
        val shiftDao = PosAdminRoomDatabase.getDatabase(requireContext()).shiftDao()
        val userDao = PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
        val repository = ShiftRepository(shiftDao, userDao)
        val factory = ShiftsViewModelFactory(repository)
        shiftsViewModel = ViewModelProvider(this, factory)[ShiftsViewModel::class.java]
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.shiftsFragment = this
        binding?.shiftsViewModel = shiftsViewModel

/*        shiftsViewModel.getAllShifts().observe(viewLifecycleOwner, Observer {
            val adapter = ShiftsAdapter(requireContext(), it)
            val recyclerView = binding?.shifts
            recyclerView?.adapter = adapter
        })*/
        val spinner = binding?.dateSpinner
        shiftsViewModel.getAllShifts().observe(viewLifecycleOwner, Observer { shifts ->
            val dates = mutableListOf<String>()
            shifts.forEach {
                dates.add(it.shiftDate)
            }
            val datesList = dates.distinct()
            val adapter = ArrayAdapter(requireContext(), R.layout.shifts_spinner_item, datesList)
            spinner?.adapter = adapter
            var selectedDate = ""
            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedDate = formatter.format(calendar)
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedDate = parent?.getItemAtPosition(position).toString()
                    Log.d(TAG, "dateSelected $selectedDate")
                }
            }
            Log.d(TAG, "dateSelected $selectedDate")
            binding?.shiftsTime?.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {
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
        })
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
        shiftsViewModel.getAllShifts().observe(viewLifecycleOwner, Observer { shifts ->
            val shiftsList = mutableListOf<Shift>()
            shifts.forEach { shift ->
                Log.d(TAG, "sd ${shift.shiftDate}")
                Log.d(TAG, "sd ${shift.shiftTime}")
                Log.d(TAG, "sd ${selectedDate}")
                Log.d(TAG, "sd ${shiftTime}")
                if ((shift.shiftDate == selectedDate) && (shift.shiftTime == shiftTime)) {
                    shiftsList.add(shift)
                    Log.d(TAG, "shifts $shiftsList")
                }

            }

            val recyclerView = binding?.shifts
            val listOfShifts: List<Shift> = shiftsList

            val adapter = ShiftsAdapter(requireContext(), listOfShifts)
            recyclerView?.adapter = adapter
        })
    }


    fun goToNextScreen() {
        findNavController().navigate(R.id.action_shiftsFragment_to_addShiftsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}