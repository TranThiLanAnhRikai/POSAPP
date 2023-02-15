package com.example.pos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.CartItemsAdapter
import com.example.pos.data.entity.Item
import com.example.pos.model.MenuViewModel
import com.example.pos_admin.R
import com.example.pos.helper.CommonStaffHeaderHelper
import com.example.pos_admin.databinding.FragmentCartBinding
import com.example.pos_admin.databinding.StaffCommonHeaderBinding


/** オーダーに入った物を表示する
 *　カートの内容をアップデートできる
 */
class CartFragment : Fragment(), CartItemsAdapter.OnClickListener {
    private var binding: FragmentCartBinding? = null
    private lateinit var headerHelper: CommonStaffHeaderHelper
    private val menuViewModel: MenuViewModel by activityViewModels()
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
        menuViewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
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

        }
        menuViewModel.getOrderNumber()?.observe(viewLifecycleOwner) { orders ->
            if (orders.isEmpty()) {
                menuViewModel.orderNumber.value = (menuViewModel.currentDate + "001").toLong()
            } else {
                val latestOrder = orders[0]
                menuViewModel.orderNumber.value = latestOrder.orderNumber + 1
            }
            if (menuViewModel.selectedItems.value != null) {
                binding?.orderNumber?.text =
                    "Order Number: ${menuViewModel.orderNumber.value.toString()}"
            }


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
        val builderAlert = AlertDialog.Builder(requireContext())
        val inflaterAlert = this.layoutInflater
        val confirmDialogView = inflaterAlert.inflate(R.layout.confirm_dialog_layout, null)
        builderAlert.setView(confirmDialogView)
        val title = confirmDialogView.findViewById<TextView>(R.id.title)
        title.text = "Confirm delete order ${menuViewModel.orderNumber.value}?"
        val continueBtn = confirmDialogView.findViewById<ImageView>(R.id.continue_button)
        val backBtn = confirmDialogView.findViewById<ImageView>(R.id.back_button)
        val confirmDialog: AlertDialog = builderAlert.create()
        continueBtn.setOnClickListener {
            menuViewModel.deleteOrder()
            findNavController().navigate(R.id.action_cartFragment_to_orderFragment)
            confirmDialog.dismiss()

        }
        backBtn.setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog.show()

    }

    fun placeOrder() {
        val prefs = context?.getSharedPreferences("order_number", Context.MODE_PRIVATE)
        prefs?.edit()?.putString("order_number", "${menuViewModel.orderNumber.value}")?.apply()
        findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
    }

}


