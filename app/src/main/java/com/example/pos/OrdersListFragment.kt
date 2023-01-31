package com.example.pos

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.R
import com.example.pos_admin.adapter.ShiftsAdapter
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.ShiftRepository
import com.example.pos_admin.databinding.FragmentOrdersListBinding
import com.example.pos_admin.databinding.FragmentShiftsBinding
import com.example.pos_admin.model.ShiftsViewModel
import com.example.pos_admin.model.ShiftsViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [OrdersListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersListFragment : Fragment() {
    private var binding: FragmentOrdersListBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOrdersListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ordersListFragment = this


    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}