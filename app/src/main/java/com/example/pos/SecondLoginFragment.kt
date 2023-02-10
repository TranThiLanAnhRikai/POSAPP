package com.example.pos_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentSecondLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory


/** 2回目のログインコードを記入させる
 * Admin側にナビゲートする
 */

class SecondLoginFragment : Fragment() {

    private var binding: FragmentSecondLoginBinding? = null
    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(
            UserRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSecondLoginBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.secondLoginFragment = this
        binding?.loginViewModel = loginViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // First login fragment に戻す
    fun backToFirstLogin() {
        findNavController().navigate(R.id.action_secondLoginFragment_to_firstLoginFragment)
    }

    // コードがブランクまたは無効の場合はエラーメッセージを表示する。コードの最大文字数は8文字にする。
    // ログインコードが有効な場合、ユーザーをメインメニューフラグメントにナビゲートする

    fun toMainMenu() {
        /* val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        if (loginViewModel.inputSecondCode.value == null) {
            builder.setMessage("You need to fill in your second code.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            binding?.loginEditText?.text = null
        } else if (!loginViewModel.isSecondLoginCodeValid()) {
            builder.setMessage("Login code is invalid. Please try again.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            binding?.loginEditText?.text = null
        } else {

            findNavController().navigate(R.id.action_secondLoginFragment_to_mainMenuFragment)
            binding?.loginEditText?.text = null

        }


*/
        findNavController().navigate(R.id.action_secondLoginFragment_to_mainMenuFragment)


    }


}

