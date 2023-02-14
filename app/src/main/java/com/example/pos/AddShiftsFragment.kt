package com.example.pos

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.R
import com.example.pos_admin.const.ShiftTime
import com.example.pos_admin.databinding.FragmentAddShiftsBinding
import com.example.pos_admin.model.ShiftsViewModel
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
        (((activity as AppCompatActivity?) ?: return).supportActionBar ?: return).show()
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
            shiftsViewModel.currentStaff.value?.clear()
            shiftsViewModel.getShifts(
                shiftsViewModel._date.value ?: return@setItems,
                shiftsViewModel._shift.value ?: return@setItems
            )
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
                if (shiftsViewModel.currentStaff.value?.isNotEmpty() == true) {
                    (shiftsViewModel.currentStaff.value ?: return@observe).forEach {
                        if (it in nameList) {
                            nameList.remove(it)
                        }
                    }
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    nameList
                )
                val autocompleteName = binding?.inputName
                autocompleteName?.setAdapter(adapter)
                autocompleteName?.threshold = 1
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
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.login_error_dialog, null)
        builder.setView(dialogView)
        val textViewError = dialogView.findViewById<TextView>(R.id.textView_error)
        val btn = dialogView.findViewById<Button>(R.id.button)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        btn.setOnClickListener {
            dialog.dismiss()
        }
        if (shiftsViewModel._date.value == null) {
            textViewError.text = "Please pick a date."
            dialog.show()
        } else if (shiftsViewModel.inputName.value == null) {
            textViewError.text = "Please pick a staff."
            dialog.show()
        } else if (shiftsViewModel._shift.value !in 0..2) {
            textViewError.text = "Please choose a shift."
            dialog.show()
        } else if ((shiftsViewModel.inputName.value
                ?: return) in (shiftsViewModel.currentStaff.value
                ?: return)
        ) {
            textViewError.text = "This staff already is in this shift."
            dialog.show()
        } else {
            shiftsViewModel.insertShift()
            val builderAlert = AlertDialog.Builder(requireContext())
            val inflaterAlert = this.layoutInflater
            val successDialogView = inflaterAlert.inflate(R.layout.success_dialog_layout, null)
            builderAlert.setView(successDialogView)
            val title = successDialogView.findViewById<TextView>(R.id.title)
            title.text = "NEW SHIFT ADDED"
            val detail: TextView = successDialogView.findViewById(R.id.detail)
            detail.text =
                "${shiftsViewModel.inputName.value} - ${shiftsViewModel._date.value} - ${binding?.shiftText?.text}"
            val continueBtn = successDialogView.findViewById<Button>(R.id.continue_button)
            val backBtn = successDialogView.findViewById<Button>(R.id.back_button)
            val successDialog: AlertDialog = builderAlert.create()
            continueBtn.setOnClickListener {
                successDialog.dismiss()
            }
            backBtn.setOnClickListener {
                successDialog.dismiss()
                findNavController().navigate(R.id.action_addShiftsFragment_to_shiftsFragment)
            }
            successDialog.show()
            binding?.datePick?.text = "Pick a Date"
            binding?.inputName?.text = null
            binding?.shiftText?.text = "Choose a Shift"
            binding?.inputName?.clearFocus()


        }


    }

    private fun displayFormattedDate(timeStamp: Long) {
        binding?.datePick?.text = formatter.format(timeStamp)
    }


}

