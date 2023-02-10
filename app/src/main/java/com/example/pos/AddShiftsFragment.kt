package com.example.pos

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos.helper.CommonAdminHeaderHelper
import com.example.pos_admin.R
import com.example.pos_admin.const.ShiftTime
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.databinding.AdminCommonHeaderBinding
import com.example.pos_admin.databinding.FragmentAddShiftsBinding
import com.example.pos_admin.model.ShiftsViewModel
import com.example.pos_admin.model.ShiftsViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/** 新しいシフトを作る
 *　日付をユーザーにせんたくさせる
 * 名前はユーザーテーブルからautocompleteオプションとして供給する
 * 新しく作ったシフトはシフトテーブルに保存する
 */
class AddShiftsFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private val shiftsViewModel: ShiftsViewModel by activityViewModels()
    private var binding: FragmentAddShiftsBinding? = null
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy MMM dd, EEEE", Locale.US)
    private val shiftOptions = arrayOf(ShiftTime.MORNING, ShiftTime.AFTERNOON, ShiftTime.NOON)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddShiftsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.addShiftsFragment = this
        binding?.shiftsViewModel = shiftsViewModel
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        //　カレンダーを表示する。今日以前の日付を無効にする
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
        // シフトのオプションを表示する
        val options = shiftOptions.map { it.name }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a shift")
        builder.setItems(options) { _, which ->
            val selectedShiftTime = shiftOptions[which]
            binding?.shiftText?.text = selectedShiftTime.shiftName
            shiftsViewModel._shift.value = selectedShiftTime.ordinal
            shiftsViewModel.getShifts(shiftsViewModel._date.value!!, shiftsViewModel._shift.value!!)
                .observe(viewLifecycleOwner) { shifts ->
                    val list = shiftsViewModel.currentStaff.value ?: mutableListOf()
                    shifts.forEach { shift ->
                        list.add(shift.shiftName)
                    }
                    shiftsViewModel.currentStaff.value = list
                }
        }
        val dialog = builder.create()
        val container = binding?.shiftText
        container?.setOnClickListener {
            dialog.show()
        }

        // 全員のユーザーをテーブルからゲートして、autocompleteで供給する
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

    // 新しいシフトをテーブルに保存する前に全てのフィールドに記入されたかチェックする。されなければエラーメッセージを表示する。
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
        } else if (shiftsViewModel.inputName.value!! in (shiftsViewModel.currentStaff.value
                ?: return)) {
            builder.setMessage("This staff already is in this shift.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }  else {
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

