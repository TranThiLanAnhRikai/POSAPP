package com.example.pos_admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.const.Role
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentAddUsersBinding
import com.example.pos_admin.model.UsersViewModel
import com.example.pos_admin.model.UsersViewModelFactory

/**　新しいユーザーを作る
 * ユーザーのテーブルに保存する
 */

class AddUsersFragment : Fragment() {
    private lateinit var usersViewModel: UsersViewModel
    private var binding: FragmentAddUsersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddUsersBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(dao)
        val factory = UsersViewModelFactory(repository)
        usersViewModel = ViewModelProvider(this, factory)[UsersViewModel::class.java]
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.addUsersFragment = this
        binding?.usersViewModel = usersViewModel
        (((activity as AppCompatActivity?) ?: return).supportActionBar ?: return).show()
        usersViewModel.getAllUsers().observe(viewLifecycleOwner) { users ->
            val newFirstCodeList = usersViewModel.listOfFirstCode.value ?: mutableListOf()
            val newSecondCodeList = usersViewModel.listOfSecondCode.value ?: mutableListOf()
            users.forEach {
                newFirstCodeList.add(it.firstCode)
                newSecondCodeList.add(it.secondCode)
            }
            usersViewModel.listOfFirstCode.value = newFirstCodeList
            usersViewModel.listOfSecondCode.value = newSecondCodeList

        }
        // 新しいユーザーの役目を選択させる。Adminであれば、自動的に2回目パスワードを発生する
        binding?.role?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.admin -> {
                    usersViewModel.inputRole.value = Role.ADMIN.roleName
                    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    var randomString = (1..8).map { characters.random() }.joinToString("")
                    while (randomString in (usersViewModel.listOfSecondCode.value
                            ?: return@setOnCheckedChangeListener)
                    ) {
                        randomString = (1..8).map { characters.random() }.joinToString("")
                    }
                    usersViewModel.secondCode.value = randomString
                    binding?.secondCodeEdttxt?.text = "Second Code: $randomString"
                    binding?.secondCodeEdttxt?.visibility = View.VISIBLE

                }

                else -> {
                    usersViewModel.inputRole.value = Role.STAFF.roleName
                    binding?.secondCodeEdttxt?.visibility = View.GONE
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // 新しいユーザーをテーブルに保存する前に全てのフィールドに正しく記入されたかチェックする。されなければエラーメッセージを表示する
    fun addNewUser() {
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
        if (usersViewModel.inputName.value == null) {
            textViewError.text = "Please fill in user name."
            dialog.show()
        } else if (usersViewModel.inputRole.value == null) {
            textViewError.text = "Please choose a role for the user."
            dialog.show()
        } else if (usersViewModel.firstCode.value == null) {
            textViewError.text = "Please fill in an 8-character code."
            dialog.show()
        } else if ((usersViewModel.firstCode.value ?: return).length < 8) {
            textViewError.text = "Login code has to be 8 characters."
            dialog.show()
        } else if ((usersViewModel.firstCode.value
                ?: return) in (usersViewModel.listOfFirstCode.value
                ?: return)
        ) {
            textViewError.text = "Login code already exists. Choose a different one."
            dialog.show()
        } else {
            usersViewModel.insertNewUser()
            val builderAlert = AlertDialog.Builder(requireContext())
            val inflaterAlert = this.layoutInflater
            val successDialogView = inflaterAlert.inflate(R.layout.success_dialog_layout, null)
            builderAlert.setView(successDialogView)
            val title = successDialogView.findViewById<TextView>(R.id.title)
            title.text = "NEW USER ADDED"
            val detail: TextView = successDialogView.findViewById(R.id.detail)
            detail.text = "${usersViewModel.inputName.value} - ${usersViewModel.inputRole.value}"
            val continueBtn = successDialogView.findViewById<Button>(R.id.continue_button)
            val backBtn = successDialogView.findViewById<Button>(R.id.back_button)
            val successDialog: AlertDialog = builderAlert.create()
            continueBtn.setOnClickListener {
                successDialog.dismiss()
            }
            backBtn.setOnClickListener {
                successDialog.dismiss()
                findNavController().navigate(R.id.action_addUsersFragment_to_usersFragment)
            }
            successDialog.show()
            binding?.nameEdttxt?.text = null
            binding?.firstCodeEdttxt?.text = null
            binding?.firstCodeEdttxt?.clearFocus()
            binding?.secondCodeEdttxt?.visibility = View.GONE
            binding?.role?.clearCheck()
        }


    }
}