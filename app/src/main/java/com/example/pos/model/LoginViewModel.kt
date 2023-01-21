package com.example.pos_admin.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pos_admin.const.Destination
import com.example.pos_admin.const.Role
import com.example.pos_admin.data.entity.User
import com.example.pos_admin.data.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    val firstLoginCode = MutableLiveData<String>()
    val user = firstLoginCode.value?.let { userRepository.getUser(it) }
    fun isFirstLoginValid(): Boolean {
        if (user != null) {
            return true
        }
        return false
    }


    fun nextFragment(): Destination {
        return if (user != null && user?.value?.role == Role.STAFF.roleName) {
            Destination.STAFF
        } else {
            Destination.NON_STAFF
        }
    }
}

class LoginViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}