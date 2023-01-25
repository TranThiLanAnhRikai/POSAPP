package com.example.pos

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.CartItemsAdapter
import com.example.pos.data.entity.MenuItem
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentCartBinding

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment() {
    private var binding: FragmentCartBinding? = null
    private val menuViewModel: MenuViewModel by activityViewModels {
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao()
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
        /*recyclerView = binding?.orderItems!!
            menuViewModel.getMenuItems(ItemType.FOOD.typeName).observe(viewLifecycleOwner, Observer{ items ->
                adapter = OrderItemsAdapter(requireContext(), items, this)
                recyclerView.adapter = adapter
            })
*/
        Log.d(TAG, "list ${menuViewModel.selectedItems}")
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.cartFragment = this
        binding?.menuViewModel = menuViewModel
        val menuItemDao = PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao()
        val menuItemRepository = MenuItemRepository(menuItemDao)
        adapter = CartItemsAdapter(requireContext(), menuViewModel.selectedItems)
        recyclerView.adapter = adapter
    /*val btnsContainer = binding?.btnsContainer
            btnsContainer?.forEach { it ->
                it.setOnClickListener {
                    menuViewModel.getMenuItems(it.tag.toString())
                        .observe(viewLifecycleOwner, Observer { selectedItems ->
                            val itemsIds = selectedItems.map { it.id.toString() }
                            adapter = OrderItemsAdapter(requireContext(), selectedItems, this)
                            recyclerView?.adapter = adapter
                        })
                }*/

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }





}


