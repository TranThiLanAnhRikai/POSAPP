package com.example.pos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.OrderAdapter
import com.example.pos.data.entity.Item
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.helper.CommonStaffHeaderHelper
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.databinding.FragmentOrderBinding
import com.example.pos_admin.databinding.StaffCommonHeaderBinding

/**
 * オーダーをする
 */

class OrderFragment : Fragment(), OrderAdapter.OnClickListener {
    private var binding: FragmentOrderBinding? = null
    private lateinit var headerHelper: CommonStaffHeaderHelper

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
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val fragmentBinding = FragmentOrderBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = StaffCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonStaffHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        (((activity as AppCompatActivity?) ?: return null).supportActionBar ?: return null).hide()
        recyclerView = binding?.orderItems!!
        menuViewModel.getMenuItems(ItemType.FOOD.typeName)
            .observe(viewLifecycleOwner) { items ->
                adapter =
                    OrderAdapter(requireContext(), items, this, menuViewModel.selectedItems.value)
                recyclerView.adapter = adapter
            }
        return fragmentBinding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderFragment = this
        binding?.menuViewModel = menuViewModel
        val btnsContainer = binding?.btnsContainer
        btnsContainer?.forEach { it ->
            it.setOnClickListener {
                menuViewModel.getMenuItems(it.tag.toString())
                    .observe(viewLifecycleOwner, Observer { selectedItems ->
                        adapter = OrderAdapter(
                            requireContext(),
                            selectedItems,
                            this,
                            menuViewModel.selectedItems.value
                        )
                        adapter.notifyDataSetChanged()
                        recyclerView.adapter = adapter
                    })
            }

        }
        menuViewModel.selectedItems.observe(viewLifecycleOwner) { items ->
            val values = items.values
            if (items.isEmpty()) {
                binding?.totalQuantity?.visibility = View.GONE
            } else {
                menuViewModel.totalQuantity = 0
                values.forEach { item ->
                    menuViewModel.totalQuantity += (item.quantity ?: return@forEach)
                    Log.d(TAG, "quantity ${menuViewModel.totalQuantity}")
                }
                binding?.totalQuantity?.visibility = View.VISIBLE
                binding?.totalQuantity?.text = menuViewModel.totalQuantity.toString()
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun addToCart(id: Int, item: Item) {
        menuViewModel.addToCart(id, item)
    }

    override fun removeFromCart(id: Int) {
        menuViewModel.removeFromCart(id)
    }

    fun toCart() {
        findNavController().navigate(R.id.action_orderFragment_to_cartFragment)
    }

    fun toOrdersList() {
        findNavController().navigate(R.id.action_orderFragment_to_ordersListFragment)
    }


}