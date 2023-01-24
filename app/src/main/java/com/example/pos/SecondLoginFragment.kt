package com.example.pos_admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        binding = fragmentBinding
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
        if (!loginViewModel.isSecondLoginCodeValid()) {
            Toast.makeText(
                requireContext(),
                "Login code is invalid. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
            loginViewModel.inputFirstCode.value = ""
        } else {
            findNavController().navigate(R.id.action_secondLoginFragment_to_mainMenuFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}