package com.example.pos_admin.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.pos_admin.const.Destination
import com.example.pos_admin.const.Role
import com.example.pos_admin.data.entity.User
import com.example.pos_admin.data.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    val inputFirstCode = MutableLiveData<String>()
    val inputSecondCode = MutableLiveData<String>()
    var userSecondLoginCode = MutableLiveData<String>()
    val user = MutableLiveData<User>()

    //Use the value of the first login code user fills in to get user(?) from database
    fun getUser(): LiveData<User> {
        return userRepository.getUser(inputFirstCode.value!!)
    }

    //Check whether the user that is achieved from getUser() function exists in the database
    fun isFirstLoginCodeValid(): Boolean {
        if (user.value == null) {
            return false
        }
        return true
    }


    // Decide the next view based on the type of user
    fun nextFragment(): Destination {
        return if (user.value != null && user.value?.role == Role.STAFF.roleName) {
            Destination.STAFF
        } else {
            Destination.NON_STAFF
        }
    }

    // Check whether the second login code is valid
    fun isSecondLoginCodeValid(): Boolean {
        Log.d(TAG, "vm code ${user.value?.secondCode}")
        if (user.value?.secondCode != inputSecondCode.value ) {
            return false
        }
        return true
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