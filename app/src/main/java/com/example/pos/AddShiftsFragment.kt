package com.example.pos

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.R
import com.example.pos_admin.const.ShiftTime
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.databinding.FragmentAddShiftsBinding
import com.example.pos_admin.model.ShiftsViewModel
import com.example.pos_admin.model.ShiftsViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*


class AddShiftsFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var shiftsViewModel: ShiftsViewModel
    private var binding: FragmentAddShiftsBinding? = null
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy MM dd, EEEE", Locale.US)
    private val shiftOptions = arrayOf(ShiftTime.MORNING, ShiftTime.AFTERNOON, ShiftTime.NOON)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddShiftsBinding.inflate(inflater, container, false)
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
        binding?.addShiftsFragment = this
        binding?.shiftsViewModel = shiftsViewModel
        binding?.datePick?.setOnClickListener {
            val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
            val datePicker = DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = today.timeInMillis
            datePicker.show()
        }
        val options = shiftOptions.map { it.name }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a shift")
        builder.setItems(options) { _, which ->
            val selectedShiftTime = shiftOptions[which]
            binding?.shiftText?.text = selectedShiftTime.shiftName
            shiftsViewModel._shift.value = selectedShiftTime.ordinal
        }


        val dialog = builder.create()
        val container = binding?.shiftText
        container?.setOnClickListener {
            dialog.show()
        }

        shiftsViewModel.getAllUsers()
            .observe(viewLifecycleOwner) { users ->
                val nameList = mutableListOf<String>()
                users.forEach {
                    nameList.add(it.name)
                }
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, nameList)
                val autocompleteName = binding?.inputName
                autocompleteName?.setAdapter(adapter)

            }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val selectedTimeStamp = calendar.timeInMillis
        displayFormattedDate(selectedTimeStamp)
        shiftsViewModel._date.value = formatter.format(selectedTimeStamp).toString()
        shiftsViewModel._date.observe(viewLifecycleOwner) {
            binding?.datePick?.text = formatter.format(selectedTimeStamp).toString()
        }
        formatter.format(selectedTimeStamp).toString()
    }

    fun previousFragment() {
        findNavController().navigate(R.id.action_addShiftsFragment_to_shiftsFragment)
    }

    fun addNewShift() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        if (shiftsViewModel._date.value == null) {
            builder.setMessage("Please pick a date.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (shiftsViewModel.inputName.value == null) {
            builder.setMessage("Please pick a staff.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (shiftsViewModel._shift.value !in 0..2) {
            builder.setMessage("Please choose a shift.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            shiftsViewModel.insertShift()

            builder.setTitle("New Shift added")
            builder.setPositiveButton("Add another one") { dialog, _ ->
                dialog.dismiss()
                binding?.datePick?.text = "Pick a Date"
                binding?.inputName?.text = null
                binding?.shiftText?.text = "Choose a Shift"
                binding?.inputName?.clearFocus()
            }
            builder.setNegativeButton("Go back to Shifts List") { _, _ ->
                findNavController().navigate(R.id.action_addShiftsFragment_to_shiftsFragment)
                shiftsViewModel.inputName.value = null
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }


    }

    private fun displayFormattedDate(timeStamp: Long) {
        binding?.datePick?.text = formatter.format(timeStamp)
    }

}

