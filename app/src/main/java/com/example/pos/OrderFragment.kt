package com.example.pos
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.OrderItemsAdapter
import com.example.pos.data.entity.Item
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.MenuItemRepository
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.databinding.FragmentOrderBinding

//Fragment for staff to make orders//

class OrderFragment : Fragment(), OrderItemsAdapter.OnClickListener {
    private var binding: FragmentOrderBinding? = null
    private val menuViewModel: MenuViewModel by activityViewModels {
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao()
            )
        )
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemsAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOrderBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        recyclerView = binding?.orderItems!!
        menuViewModel.getMenuItems(ItemType.FOOD.typeName).observe(viewLifecycleOwner, Observer{items ->
            adapter = OrderItemsAdapter(requireContext(), items, this)
            recyclerView.adapter = adapter
        })


        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderFragment = this
        binding?.menuViewModel = menuViewModel
        val btnsContainer = binding?.btnsContainer
        btnsContainer?.forEach { it ->
            it.setOnClickListener {
                menuViewModel.getMenuItems(it.tag.toString())
                    .observe(viewLifecycleOwner, Observer { selectedItems ->
                        adapter = OrderItemsAdapter(requireContext(), selectedItems, this)
                        recyclerView?.adapter = adapter
                    })
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
         Log.d(TAG, "list in order frag ${menuViewModel.selectedItems.value}")
        /*menuViewModel.calculateTotal()*/
        findNavController().navigate(R.id.action_orderFragment_to_cartFragment)
    }


}