package com.example.pos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.CartItemsAdapter
import com.example.pos.data.entity.Item
import com.example.pos.data.repository.OrderRepository
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentCartBinding


/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment(), CartItemsAdapter.OnClickListener {
    private var binding: FragmentCartBinding? = null
    private val menuViewModel: MenuViewModel by activityViewModels {
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).cartItemDao()
            )
        )
    }
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CartItemsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentCartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        recyclerView = binding?.orderReview!!
        return fragmentBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.cartFragment = this
        binding?.menuViewModel = menuViewModel
        menuViewModel.selectedItems.observe(viewLifecycleOwner, Observer { selectedItems ->
            adapter = CartItemsAdapter(requireContext(), selectedItems, this)
            recyclerView.adapter = adapter
            val items = selectedItems.values.toList()
            menuViewModel.total = 0.0
            items.forEach{ item ->
                menuViewModel.total += item.subTotal
            }
            binding?.total?.text = "TOTAL: $" + "%.2f".format(menuViewModel.total)

        })

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
        findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
    }

}


