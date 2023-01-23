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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.const.Destination
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentFirstLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory


class FirstLoginFragment : Fragment() {

    private var binding: FragmentFirstLoginBinding? = null
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
        if (loginViewModel.inputFirstCode.value == null) {
            Log.d(TAG, "login ${loginViewModel.inputFirstCode.value}")
            Toast.makeText(requireContext(), "Please fill in your login code.", Toast.LENGTH_SHORT)
                .show()
        } else {
            loginViewModel.getUser().observe(viewLifecycleOwner, Observer { person ->
                loginViewModel.user.value = person
                loginViewModel.userSecondLoginCode.value = person.secondCode
                if (!loginViewModel.isFirstLoginCodeValid()) {
                    Toast.makeText(
                        requireContext(),
                        "Your login code is invalid. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val destination = loginViewModel.nextFragment()
                    Log.d(TAG, "1user ${loginViewModel.user.value}")
                    if (destination == Destination.NON_STAFF) {
                        findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
                    } else {
                        findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
                    }
                }
            })

        }
    }
}


