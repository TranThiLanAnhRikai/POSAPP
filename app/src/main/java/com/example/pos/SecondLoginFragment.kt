package com.example.pos_admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentSecondLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory



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
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.secondLoginFragment = this
        binding?.loginViewModel = loginViewModel


    }


    fun previousFragment() {
        findNavController().navigate(R.id.action_secondLoginFragment_to_firstLoginFragment)
    }

    // If the second login code is valid, log the user in as admin. Otherwise display a toast message.
    fun nextFragment() {
        val builder = AlertDialog.Builder(requireContext())
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
            val prefs = context?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
            prefs?.edit()?.putString("username", "${loginViewModel.user.value?.name}")?.apply()
            findNavController().navigate(R.id.action_secondLoginFragment_to_mainMenuFragment)
        }
    }
}
/*  findNavController().navigate(R.id.action_secondLoginFragment_to_mainMenuFragment)*//*

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}*/
