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
import com.example.pos_admin.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentCheckoutBinding


/**
 * A simple [Fragment] subclass.
 * Use the [CheckoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckoutFragment : Fragment() {

    private var binding: FragmentCheckoutBinding? = null
    private val menuViewModel: MenuViewModel by activityViewModels {
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao()
            )
        )
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: CheckoutItemsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        recyclerView = binding?.cartItems!!
        return fragmentBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.checkoutFragment = this
        binding?.menuViewModel = menuViewModel
        val spinner = binding?.spinnerInner
        val options = resources.getStringArray(R.array.payment_options)
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, options)
        spinner?.adapter = adapter
        binding?.switchDelivery?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding?.apply {
                    inputAddress.visibility = View.VISIBLE
                    inputCity.visibility = View.VISIBLE
                    inputZip.visibility = View.VISIBLE
                    inputPickupTime.visibility = View.GONE
                    request.visibility = View.GONE
                }
            } else {
                binding?.apply {
                    inputAddress.visibility = View.GONE
                    inputCity.visibility = View.GONE
                    inputZip.visibility = View.GONE
                    inputPickupTime.visibility = View.VISIBLE
                    request.visibility = View.VISIBLE
                }
            }

        }
        binding?.tvSubtotalAmount?.text = "$" + menuViewModel.total.toString()
        binding?.tvTotalAmount?.text = "$" + (menuViewModel.total + 5).toString()
        menuViewModel.selectedItems.observe(viewLifecycleOwner, Observer { selectedItems ->
            itemsAdapter = CheckoutItemsAdapter(requireContext(), selectedItems)
            recyclerView.adapter = itemsAdapter
        })
    }

    fun toOrderStatus() {
        menuViewModel.insertToOrderList()
        findNavController().navigate(R.id.action_checkoutFragment_to_orderStatusFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}