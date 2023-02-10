package com.example.pos_admin.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.pos.const.Destination
import com.example.pos_admin.const.Role
import com.example.pos_admin.data.entity.User
import com.example.pos_admin.data.repository.UserRepository

/** ふたつのログイン画面のViewModel
 * ユーザーの有効の確認
 */
class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // 一つ目コードのフィールド
    val inputFirstCode: MutableLiveData<String> = MutableLiveData<String>()

    // 二つ目コードのフィールド
    val inputSecondCode: MutableLiveData<String> = MutableLiveData<String>()

    // 一つ目コードでユーザーテーブルからユーザーをゲート出来れば、あのユーザーのテーブルでの二つ目コード
    var userSecondLoginCode: MutableLiveData<String> = MutableLiveData<String>()

    // ユーザーのテーブルからユーザーをゲットする変数
    val user: MutableLiveData<User> = MutableLiveData<User>()

    // 記入された一つ目コードでユーザーテーブルからユーザーをゲートする
    fun getUser(): LiveData<User> {
        return userRepository.getUser(inputFirstCode.value!!)
    }

    // ユーザーのタイプによって、次の画面をアサインする
    fun nextFragment(): Destination {
        return if (user.value != null && user.value?.role == Role.STAFF.roleName) {
            Destination.STAFF
        } else {
            Destination.NON_STAFF
        }
    }

    // 記入された二つ目コードの有効をチェックする
    fun isSecondLoginCodeValid(): Boolean {
        if (user.value?.secondCode != inputSecondCode.value) {
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