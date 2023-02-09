package com.example.pos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.CartItemsAdapter
import com.example.pos.data.entity.Item
import com.example.pos.model.MenuViewModel
import com.example.pos_admin.R
import com.example.pos.helper.CommonStaffHeaderHelper
import com.example.pos_admin.databinding.FragmentCartBinding
import com.example.pos_admin.databinding.StaffCommonHeaderBinding


/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment(), CartItemsAdapter.OnClickListener {
    private var binding: FragmentCartBinding? = null
    private lateinit var headerHelper: CommonStaffHeaderHelper
    private val menuViewModel: MenuViewModel by activityViewModels() /*{
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).cartItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).customerDao()
            )
        )
    }*/
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CartItemsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentCartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = StaffCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonStaffHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        (((activity as AppCompatActivity?) ?: return null).supportActionBar ?: return null).hide()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        recyclerView = binding?.orderReview!!
        return fragmentBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.cartFragment = this
        binding?.menuViewModel = menuViewModel
        menuViewModel.selectedItems.observe(viewLifecycleOwner, Observer { selectedItems ->
            if (selectedItems.isEmpty()) {
                binding?.orderNumber?.visibility = View.GONE
                binding?.total?.visibility = View.GONE
            }
            adapter = CartItemsAdapter(requireContext(), selectedItems, this)
            recyclerView.adapter = adapter
            val items = selectedItems.values.toList()
            menuViewModel.total = 0.0
            items.forEach { item ->
                menuViewModel.total += item.subTotal
            }
            binding?.total?.text = "TOTAL: $" + "%.2f".format(menuViewModel.total)

        })
        menuViewModel.getOrderNumber()?.observe(viewLifecycleOwner) { orders ->
            if (orders.isEmpty()) {
                menuViewModel.orderNumber.value = (menuViewModel.currentDate + "001").toLong()
            } else {
                val latestOrder = orders[0]
                menuViewModel.orderNumber.value = latestOrder.orderNumber + 1
            }


        }
        if (menuViewModel.selectedItems.value != null) {
            Log.d(TAG, "selectedItems ${menuViewModel.selectedItems.value}")
            binding?.orderNumber?.text =
                "Order Number: ${menuViewModel.orderNumber.value.toString()}"
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun increaseQuantity(id: Int, item: Item) {
        menuViewModel.increaseQuantity(id, item)
    }

    override fun decreaseQuantity(id: Int, item: Item) {
        menuViewModel.decreaseQuantity(id, item)
    }

    override fun removeFromCart(id: Int) {
        menuViewModel.removeFromCart(id)
    }

    fun cancelOrder() {
        findNavController().navigate(R.id.action_cartFragment_to_orderFragment)
    }

    fun deleteOrder() {
        menuViewModel.deleteOrder()
        findNavController().navigate(R.id.action_cartFragment_to_orderFragment)
    }

    fun payOrder() {
        val prefs = context?.getSharedPreferences("order_number", Context.MODE_PRIVATE)
        prefs?.edit()?.putString("order_number", "${menuViewModel.orderNumber.value}")?.apply()
        findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
    }

}


