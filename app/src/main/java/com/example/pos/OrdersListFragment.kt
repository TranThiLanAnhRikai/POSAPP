package com.example.pos

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.OrderItemsAdapter
import com.example.pos.adapter.OrdersAdapter
import com.example.pos.const.Status
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.adapter.ShiftsAdapter
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.entity.Order
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
class OrdersListFragment : Fragment(), OrdersAdapter.SetOnClickListener {
    private var binding: FragmentOrdersListBinding? = null
    lateinit var adapter: OrdersAdapter
    private val menuViewModel: MenuViewModel by activityViewModels() /*{
        MenuViewModelFactory (
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).cartItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).customerDao()
            )
                )
    }*/
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
        binding?.menuViewModel = menuViewModel
        val recyclerView = binding?.orders
        menuViewModel.getOrders(Status.PROCESSING.name).observe(viewLifecycleOwner) { orders ->
            adapter = OrdersAdapter(requireContext(), orders, this)
            recyclerView?.adapter = adapter
        }
        val btnsContainer = binding?.btnsContainer
        btnsContainer?.forEach { it ->
            it.setOnClickListener {
                menuViewModel.getOrders(it.tag.toString())
                    .observe(viewLifecycleOwner) { selectedItems ->
                        adapter = OrdersAdapter(requireContext(), selectedItems, this)
                        recyclerView?.adapter = adapter
                    }
            }

        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun updateOrder(order: Order) {
        menuViewModel.updateOrder(order)
    }

}