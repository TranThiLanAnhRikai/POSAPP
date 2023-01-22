package com.example.pos_admin.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.pos_admin.const.Destination
import com.example.pos_admin.const.Role
import com.example.pos_admin.data.entity.User
import com.example.pos_admin.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    val firstLoginCode = MutableLiveData<String>()
    val user = MutableLiveData<User>()

    fun getUser(): LiveData<User> {
        return userRepository.getUser(firstLoginCode.value!!)
    }

    fun isFirstLoginCodeValid(): Boolean {
        if (user.value == null) {
            Log.d(TAG, "user ${user.value}")
            return false
        }
        return true
    }



    fun nextFragment(): Destination {
        return if (user.value != null && user?.value?.role == Role.STAFF.roleName) {
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