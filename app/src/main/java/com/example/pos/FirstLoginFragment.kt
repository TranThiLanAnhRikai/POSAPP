package com.example.pos_admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos.const.Destination
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentFirstLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory

/** 最初のログインコードを記入させる
 * エラーメッセージを表示する
 * 適切な画面にナビゲートする
 */

class FirstLoginFragment : Fragment() {

    private var binding: FragmentFirstLoginBinding? = null

    // ViewModelをし始める
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
        val fragmentBinding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        (((activity as AppCompatActivity?) ?: return null).supportActionBar ?: return null).hide()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.firstLoginFragment = this
        binding?.loginViewModel = loginViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    // コードがブランクまたは無効の場合はエラーメッセージを表示する。コードの最大文字数は8文字にする。
    // ログインコードが有効な場合、ユーザーの種類によって適切の画面にナビゲートする

    fun nextScreen() {
        /*val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        if (loginViewModel.inputFirstCode.value == null) {
            builder.setMessage("Please fill in your login code.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            loginViewModel.getUser().observe(viewLifecycleOwner) { person ->
                loginViewModel.user.value = person
                if (person == null) {
                    builder.setMessage("Login code is invalid. Please try again.")
                    builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    binding?.loginEditText?.text = null
                } else {
                    val prefs = context?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                    prefs?.edit()?.putString("username", "${loginViewModel.user.value?.name}")
                        ?.apply()
                    loginViewModel.userSecondLoginCode.value = person.secondCode
                    val destination = loginViewModel.nextFragment()
                    binding?.loginEditText?.text = null
                    if (destination == Destination.NON_STAFF) {
                        findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
                    } else {
                        findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
                    }

                }
            }
        }*/
        findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
    }
}