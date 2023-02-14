package com.example.pos

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.OrderDetailsDialogAdapter
import com.example.pos.adapter.OrdersAdapter
import com.example.pos.const.Status
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.helper.CommonStaffHeaderHelper
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.entity.Order
import com.example.pos_admin.databinding.FragmentOrdersListBinding
import com.example.pos_admin.databinding.StaffCommonHeaderBinding
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [OrdersListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersListFragment : Fragment(), OrdersAdapter.SetOnClickListener {
    private var binding: FragmentOrdersListBinding? = null
    lateinit var adapter: OrdersAdapter
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOrdersListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = StaffCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonStaffHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
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

    @SuppressLint("SetTextI18n")
    override fun showOrder(orderNumber: Long) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.order_details_layout, null)
        dialogBuilder.setView(dialogView)
        val orderNo = dialogView.findViewById<TextView>(R.id.order_number)
        orderNo.text = "Order Number: $orderNumber"
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.order_details)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val total = dialogView.findViewById<TextView>(R.id.total_amount)
        val customerName = dialogView.findViewById<TextView>(R.id.customer_name)
        val customerPhoneNumber = dialogView.findViewById<TextView>(R.id.customer_phone_number)
        val paymentMethod = dialogView.findViewById<TextView>(R.id.payment_method)
        val deliveryMethod = dialogView.findViewById<TextView>(R.id.delivery_method)
        val address = dialogView.findViewById<TextView>(R.id.customer_address)
        val request = dialogView.findViewById<TextView>(R.id.request)
        menuViewModel.getOrderByOrderNumber(orderNumber).observe(viewLifecycleOwner) {
            total.text = "Total: $${it.total}"
            paymentMethod.text = it.paymentMethod
            deliveryMethod.text = it.deliveryMethod
            if (it.request != null) {
                request.visibility = View.VISIBLE
                request.text = it.request
            }
        }

        menuViewModel.getCartItemsByOrderNumber(orderNumber).observe(viewLifecycleOwner) {
            val itemsAdapter = OrderDetailsDialogAdapter(requireContext(), it)
            recyclerView.adapter = itemsAdapter
        }

        menuViewModel.getCustomerByOrderNumber(orderNumber).observe(viewLifecycleOwner) {
            customerName.text = it.customerName
            customerPhoneNumber.text = it.phoneNumber
            if (!it.address!!.contains("null")) {
                address.visibility = View.VISIBLE
                address.text = it.address
            }
        }


        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    fun toOrder() {
        menuViewModel.selectedItems.value?.clear()
        findNavController().navigate(R.id.action_ordersListFragment_to_orderFragment)
    }

}