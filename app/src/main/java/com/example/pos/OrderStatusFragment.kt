package com.example.pos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.OrderItemsAdapter
import com.example.pos.data.entity.Item
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentOrderBinding
import com.example.pos_admin.databinding.FragmentOrderStatusBinding


/**
 * A simple [Fragment] subclass.
 * Use the [PaymentStatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderStatusFragment : Fragment() {

    private var binding: FragmentOrderStatusBinding? = null
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
        val fragmentBinding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.orderStatusFragment = this

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }




}