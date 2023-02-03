package com.example.pos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
 * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
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
        menuViewModel.totalWithDelivery.observe(viewLifecycleOwner, Observer {
            menuViewModel.totalWithDelivery.value?.plus(
                menuViewModel.total)
            binding?.tvTotalAmount?.text = "$" + "%.2f".format((menuViewModel.totalWithDelivery.value))
        })

        return fragmentBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.checkoutFragment = this
        binding?.menuViewModel = menuViewModel
        val spinner = binding?.spinnerInner
        val options = resources.getStringArray(R.array.payment_options)
        val adapter = ArrayAdapter(requireContext(), R.layout.payment_spinner_item, options)
        spinner?.adapter = adapter
        binding?.switchDelivery?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding?.apply {
                    inputAddress.visibility = View.VISIBLE
                    inputZip.visibility = View.VISIBLE
                    inputPickupTime.visibility = View.GONE
                    request.visibility = View.GONE
                    tvDeliveryCharge.visibility = View.VISIBLE
                    tvDeliveryChargeAmount.visibility = View.VISIBLE
                    tvDeliveryChargeAmount.text = "$5.0"
                    menuViewModel?.totalWithDelivery?.value = menuViewModel?.total?.plus(5)

                }


            } else {
                binding?.apply {
                    inputAddress.visibility = View.GONE
                    inputZip.visibility = View.GONE
                    inputPickupTime.visibility = View.VISIBLE
                    request.visibility = View.VISIBLE
                    tvDeliveryCharge.visibility = View.GONE
                    tvDeliveryChargeAmount.visibility = View.GONE
                    menuViewModel?.totalWithDelivery?.value = menuViewModel?.total

                }
            }



        }





        menuViewModel.selectedItems.observe(viewLifecycleOwner, Observer { selectedItems ->
            itemsAdapter = CheckoutItemsAdapter(requireContext(), selectedItems)
            recyclerView.adapter = itemsAdapter
        })
        binding?.orderNumber?.text = "Order Number: ${menuViewModel.orderNumber.value}"

    }

    fun placeOrder() {
        menuViewModel.insertToOrderCustomerList()
        menuViewModel.insertCustomer()
        findNavController().navigate(R.id.action_checkoutFragment_to_orderStatusFragment)
        binding?.inputName?.text = null
        binding?.phoneNumberEdttxt?.text = null
        binding?.request?.text = null
        binding?.pickupText?.text = null
        binding?.inputAddress?.text = null
        binding?.inputZip?.text = null
        menuViewModel.orderNumber.value = null
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