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
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.pos_admin.const.Destination
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentFirstLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory


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
        val firstCode = binding?.loginEditText
        loginViewModel.firstLoginCode.observe(viewLifecycleOwner, Observer {
            loginViewModel.firstLoginCode.value = firstCode.toString()
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun nextScreen() {
        if (!loginViewModel.isFirstLoginValid()) {
            Toast.makeText(
                requireContext(),
                "The login code is invalid. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val destination = loginViewModel.nextFragment()
            Log.d(TAG, "destination $destination")
            if (destination == Destination.STAFF) {
                findNavController().navigate(R.id.action_firstLoginFragment_to_orderFragment)
            } else {
                findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
            }
        }
    }
}