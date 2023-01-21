package com.example.pos_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.databinding.FragmentSecondLoginBinding


class SecondLoginFragment : Fragment() {

    private var binding: FragmentSecondLoginBinding? = null


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
    }

    fun previousFragment() {
        findNavController().navigate(R.id.action_secondLoginFragment_to_firstLoginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun nextFragment() {
        findNavController().navigate(R.id.action_secondLoginFragment_to_mainMenuFragment)

   }
}