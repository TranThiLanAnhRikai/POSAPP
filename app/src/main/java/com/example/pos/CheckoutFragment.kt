package com.example.pos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.CheckoutItemsAdapter
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.helper.CommonHeaderHelper
import com.example.pos_admin.databinding.FragmentCheckoutBinding
import com.example.pos_admin.databinding.StaffCommonHeaderBinding


/**
 * A simple [Fragment] subclass.
 * Use LoginViewModel.
 *
 */
class CheckoutFragment : Fragment() {

    private var binding: FragmentCheckoutBinding? = null
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: CheckoutItemsAdapter


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = StaffCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        recyclerView = binding?.cartItems!!
        binding?.tvSubtotalAmount?.text = "$" + "%.2f".format(menuViewModel.total)
        binding?.tvTotalAmount?.text = "$" + "%.2f".format(menuViewModel.total)
        menuViewModel.totalWithDelivery.observe(viewLifecycleOwner) {
            binding?.tvTotalAmount?.text =
                "$" + "%.2f".format((it))
        }
        return fragmentBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.checkoutFragment = this
        binding?.menuViewModel = menuViewModel
        if (menuViewModel.selectedItems.value?.isEmpty() != true) {
            binding?.orderNumber?.text = "Order Number: ${menuViewModel.orderNumber.value}"
        }
        menuViewModel.selectedItems.observe(viewLifecycleOwner) { selectedItems ->
            itemsAdapter = CheckoutItemsAdapter(requireContext(), selectedItems)
            recyclerView.adapter = itemsAdapter
        }
        val spinner = binding?.spinnerInner
        val options = resources.getStringArray(R.array.payment_options)
        val adapter = ArrayAdapter(requireContext(), R.layout.payment_spinner_item, options)
        spinner?.adapter = adapter
        menuViewModel?.totalWithDelivery?.value = menuViewModel?.total!!
        menuViewModel?.deliveryMethod?.value = "Pickup"
        binding?.switchDelivery?.setOnCheckedChangeListener { it, isChecked ->
            if (isChecked) {
                binding?.apply {
                    inputAddress.visibility = View.VISIBLE
                    inputZip.visibility = View.VISIBLE
                    inputPickupTime.visibility = View.GONE
                    tvDeliveryCharge.visibility = View.VISIBLE
                    tvDeliveryChargeAmount.visibility = View.VISIBLE
                    tvDeliveryChargeAmount.text = "$5.00"
                    menuViewModel?.totalWithDelivery?.value = menuViewModel?.total?.plus(5)!!
                    menuViewModel?.deliveryMethod?.value = "Delivery"
                }


            } else {
                binding?.apply {
                    inputAddress.visibility = View.GONE
                    inputZip.visibility = View.GONE
                    inputPickupTime.visibility = View.VISIBLE
                    tvDeliveryCharge.visibility = View.GONE
                    tvDeliveryChargeAmount.visibility = View.GONE
                    menuViewModel?.deliveryMethod?.value = "Pickup"
                    menuViewModel?.totalWithDelivery?.value = menuViewModel?.total
                }
            }



            }
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedOption = options[position]
                menuViewModel.paymentMethod.value = selectedOption
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}








        }
    }

    fun placeOrder() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        if (menuViewModel.selectedItems.value?.isEmpty() == true) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
            builder.setMessage("Order is empty.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.customerName.value == null) {
            builder.setMessage("Customer name needed.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.customerPhoneNumber.value == null) {
            builder.setMessage("Customer phone number needed.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.paymentMethod.value == "Payment Method") {
            builder.setMessage("Choose a payment method.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.deliveryMethod.value == "Pickup" && menuViewModel.pickupTime.value == null) {
            builder.setMessage("Pickup time needed.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.deliveryMethod.value == "Delivery" && menuViewModel.customerAddress.value == null) {
            builder.setMessage("Customer address needed.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.deliveryMethod.value == "Delivery" && menuViewModel.customerZipCode.value == null) {
            builder.setMessage("Zip code needed.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        } else {
            menuViewModel.insertToOrderCustomerList()
            menuViewModel.insertCustomer()
            findNavController().navigate(R.id.action_checkoutFragment_to_orderStatusFragment)
            binding?.inputName?.text = null
            binding?.phoneNumberEdttxt?.text = null
            binding?.request?.text = null
            binding?.inputPickupTime?.text = null
            binding?.inputAddress?.text = null
            binding?.inputZip?.text = null
        }

    }

    fun cancelOrder() {
        menuViewModel.selectedItems.value?.clear()
        findNavController().navigate(R.id.action_checkoutFragment_to_orderFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}