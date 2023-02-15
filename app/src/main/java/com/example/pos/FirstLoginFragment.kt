package com.example.pos_admin

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos.const.Destination
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
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
        val builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.login_error_dialog, null)
        builder.setView(dialogView)
        val textViewError = dialogView.findViewById<TextView>(R.id.textView_error)
        val btn = dialogView.findViewById<Button>(R.id.button)
        val dialog: AlertDialog = builder.create()
        btn.setOnClickListener {
            dialog.dismiss()
        }
        if (loginViewModel.inputFirstCode.value == null) {
            textViewError.text = "Please fill in your login code."
            dialog.show()
        } else {
            loginViewModel.getUser().observe(viewLifecycleOwner) { person ->
                loginViewModel.user.value = person
                if (person == null) {
                    textViewError.text = "Login code is invalid. Please try again."
                    dialog.show()
                    binding?.loginEditText?.text = null
                } else {
                    val prefs = context?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                    prefs?.edit()?.putString("username", "${loginViewModel.user.value?.name}")
                        ?.apply()
                    loginViewModel.userSecondLoginCode.value = person.secondCode
                    val destination = loginViewModel.nextFragment()
                    Log.d(TAG, "destination $destination")
                    binding?.loginEditText?.text = null
                    if (destination == Destination.STAFF) {
                        val builderAlert = AlertDialog.Builder(requireContext())
                        val inflaterAlert = this.layoutInflater
                        val optionsDialogView =
                            inflaterAlert.inflate(R.layout.options_dialog_layout, null)
                        builderAlert.setView(optionsDialogView)
                        val toListLayout =
                            optionsDialogView.findViewById<View>(R.id.to_list_container)
                        val toOrderLayout =
                            optionsDialogView.findViewById<View>(R.id.to_order_container)
                        val optionsDialog: AlertDialog = builderAlert.create()
                        toListLayout.setOnClickListener {
                            findNavController().navigate(R.id.action_firstLoginFragment_to_ordersListFragment)
                            optionsDialog.dismiss()
                        }
                        toOrderLayout.setOnClickListener {
                            findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
                            optionsDialog.dismiss()
                        }
                        optionsDialog.show()

                    } else {
                        findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
                    }
                }
            }

        }
    }
}