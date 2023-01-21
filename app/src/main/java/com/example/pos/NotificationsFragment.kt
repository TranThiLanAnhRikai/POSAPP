package com.example.pos_admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pos_admin.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {
    private var binding: FragmentNotificationsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.notificationsFragment = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}