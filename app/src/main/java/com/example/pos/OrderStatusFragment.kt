package com.example.pos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.OrderItemsAdapter
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.helper.CommonHeaderHelper
import com.example.pos_admin.databinding.FragmentOrderStatusBinding
import com.example.pos_admin.databinding.StaffCommonHeaderBinding


/**
 * A simple [Fragment] subclass.
 * Use the [PaymentStatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderStatusFragment : Fragment() {

    private var binding: FragmentOrderStatusBinding? = null
    private lateinit var headerHelper: CommonHeaderHelper
    private val menuViewModel: MenuViewModel by activityViewModels {
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).cartItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).customerDao()
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = StaffCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        Log.d(TAG, "orderNo ${menuViewModel.orderNumber.value}")
        binding?.orderPlacedText?.text =
            "Order ${menuViewModel.orderNumber.value} has been placed successfully."
        return fragmentBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderStatusFragment = this
        binding?.menuViewModel = menuViewModel
    }

    fun toOrderList() {

        menuViewModel.orderNumber.value = null
        findNavController().navigate(R.id.action_orderStatusFragment_to_ordersListFragment)
    }

    fun toPlaceOrder() {

        menuViewModel.orderNumber.value = null
        menuViewModel.selectedItems.value = mutableMapOf()
        findNavController().navigate(R.id.action_orderStatusFragment_to_orderFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}