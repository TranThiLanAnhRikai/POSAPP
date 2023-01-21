package com.example.pos_admin

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.adapter.ShiftsAdapter
import com.example.pos_admin.databinding.FragmentShiftsBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.model.*

class ShiftsFragment : Fragment(), DatePickerDialog.OnDateSetListener {
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
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).shiftDao()
        val repository = ShiftRepository(dao)
        val factory = ShiftsViewModelFactory(repository)
        shiftsViewModel = ViewModelProvider(this, factory)[ShiftsViewModel::class.java]
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.shiftsFragment = this
        binding?.shiftsViewModel = shiftsViewModel
        binding?.shiftsDate?.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
        val recyclerView = binding?.shifts
        shiftsViewModel.getAllShifts().observe(viewLifecycleOwner, Observer { shifts ->
            val adapter = ShiftsAdapter(requireContext(), shifts)
            recyclerView?.adapter = adapter
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val selectedTimeStamp = calendar.timeInMillis
        displayFormattedDate(selectedTimeStamp)
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

    }

    private fun showShifts(selectedDate: String, shiftTime: Int) {
        val listOfShifts = shiftsViewModel.getShifts(selectedDate, shiftTime)
        listOfShifts.observe(viewLifecycleOwner, Observer { shifts ->
            val recyclerView = binding?.shifts
            val adapter = ShiftsAdapter(requireContext(), shifts)
            recyclerView?.adapter = adapter
        })
    }

    private fun displayFormattedDate(timeStamp: Long) {
        binding?.shiftsDate?.text = formatter.format(timeStamp)
    }

    fun goToNextScreen() {
        findNavController().navigate(R.id.action_shiftsFragment_to_addShiftsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}