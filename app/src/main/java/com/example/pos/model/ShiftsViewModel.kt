package com.example.pos_admin.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pos_admin.data.entity.Shift
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.MutableLiveData
import com.example.pos_admin.data.entity.User
import com.example.pos_admin.data.repository.ShiftRepository

class ShiftsViewModel(private val shiftRepository: ShiftRepository): ViewModel() {

    val inputName = MutableLiveData<String>()
    val _date = MutableLiveData<String>()
    val _shift = MutableLiveData<Int>()

    fun getAllShifts(): LiveData<List<Shift>> {
            return shiftRepository.shifts
    }

   fun insertShift() {
        val name = inputName.value!!
        viewModelScope.launch {
            shiftRepository.insert(Shift(0, name, _date.value!!, _shift.value!!))
        }

    }

    fun getShifts(date: String, shift: Int): LiveData<List<Shift>> {
        return shiftRepository.getShifts(date, shift)
    }

    fun getAllUsers(): LiveData<List<User>> {
        return shiftRepository.getAllUsers()
    }



}

class ShiftsViewModelFactory(private val shiftRepository: ShiftRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShiftsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShiftsViewModel(shiftRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
