package com.example.pos_admin

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos.helper.CommonAdminHeaderHelper
import com.example.pos_admin.const.Role
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.AdminCommonHeaderBinding
import com.example.pos_admin.databinding.FragmentAddUsersBinding
import com.example.pos_admin.model.UsersViewModel
import com.example.pos_admin.model.UsersViewModelFactory
import kotlin.math.log

/**　新しいユーザーを作る
 * ユーザーのテーブルに保存する
 */

class AddUsersFragment : Fragment() {
    private lateinit var usersViewModel: UsersViewModel
    private var binding: FragmentAddUsersBinding? = null
    private lateinit var headerHelper: CommonAdminHeaderHelper

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
        val headerBinding = AdminCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonAdminHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.addUsersFragment = this
        binding?.usersViewModel = usersViewModel
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        usersViewModel.getAllUsers().observe(viewLifecycleOwner) { users ->
            val newFirstCodeList = usersViewModel.listOfFirstCode.value ?: mutableListOf()
            val newSecondCodeList = usersViewModel.listOfSecondCode.value ?: mutableListOf()
            users.forEach { it ->
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
                    while (randomString in usersViewModel.listOfSecondCode.value!!) {
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
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        if (usersViewModel.inputName.value == null) {
            builder.setMessage("Please fill in user name.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (usersViewModel.inputRole.value == null) {
            builder.setMessage("Please choose a role for the user.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (usersViewModel.firstCode.value == null) {
            builder.setMessage("Please fill in an 8-character code.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (usersViewModel.firstCode.value!!.length < 8) {
            builder.setMessage("Login code has to be 8 characters.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (usersViewModel.firstCode.value!! in usersViewModel.listOfFirstCode.value!!) {
            builder.setMessage("Login code already exists. Choose a different one.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            usersViewModel.insertNewUser()
            binding?.nameEdttxt?.text = null
            binding?.firstCodeEdttxt?.text = null
            builder.setTitle("New User added")
            builder.setPositiveButton("Add another one") { dialog, _ ->
                dialog.dismiss()
                binding?.nameEdttxt?.text = null
                binding?.firstCodeEdttxt?.text = null
                binding?.firstCodeEdttxt?.clearFocus()
                binding?.secondCodeEdttxt?.visibility = View.GONE
                binding?.role?.clearCheck()
            }
            builder.setNegativeButton("Go back to Users List") { _, _ ->
                findNavController().navigate(R.id.action_addUsersFragment_to_usersFragment)
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


    }
}