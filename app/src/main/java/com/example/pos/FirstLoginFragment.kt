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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.pos_admin.const.Destination
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentFirstLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory
import kotlinx.coroutines.launch
import kotlin.math.log


class FirstLoginFragment : Fragment() {

    private var binding: FragmentFirstLoginBinding? = null
    private lateinit var loginViewModel: LoginViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(dao)
        val factory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
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

    fun nextScreen() {
        if (loginViewModel.firstLoginCode.value == null) {
            Log.d(TAG, "login ${loginViewModel.firstLoginCode.value}")
            Toast.makeText(requireContext(), "Please fill in your login code.", Toast.LENGTH_SHORT).show()
        }
        else {
            loginViewModel.getUser().observe(viewLifecycleOwner, Observer { person ->
                loginViewModel.user.value = person
                if (!loginViewModel.isFirstLoginCodeValid()) {
                Toast.makeText(requireContext(), "Your login code is invalid. Please try again.", Toast.LENGTH_SHORT).show()
                }
                else {
                    val destination = loginViewModel.nextFragment()
                if (destination == Destination.NON_STAFF) {
                    findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
                }
                else {
                    findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
                }

            }

        })



    }
}
}