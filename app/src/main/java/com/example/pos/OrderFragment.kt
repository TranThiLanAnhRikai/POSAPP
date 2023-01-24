package com.example.pos
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.MenuItemsAdapter
import com.example.pos.adapter.OrderItemsAdapter
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentOrderBinding
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.const.ItemType

//Fragment for staff to make orders//

class OrderFragment : Fragment() {
    private var binding: FragmentOrderBinding? = null
    private lateinit var menuViewModel: MenuViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOrderBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao()
        val repository = MenuItemRepository(dao)
        val factory = MenuViewModelFactory(repository, requireContext())
        menuViewModel = ViewModelProvider(this, factory)[MenuViewModel::class.java]
        recyclerView = binding?.orderItems!!
        menuViewModel.getMenuItems(ItemType.FOOD.typeName).observe(viewLifecycleOwner, Observer{items ->
            adapter = OrderItemsAdapter(requireContext(), items)
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
                        val itemsIds = selectedItems.map { it.id.toString() }
                        adapter = OrderItemsAdapter(requireContext(), selectedItems)
                        recyclerView?.adapter = adapter
                    })
            }

        }

    }






    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}