package com.example.pos_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos.adapter.MenuItemsAdapter
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos.helper.CommonAdminHeaderHelper
import com.example.pos_admin.databinding.FragmentMenuBinding
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.databinding.AdminCommonHeaderBinding

/** メニューのものをRecyclerViewで表示する
 */
class MenuFragment : Fragment() {
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
    private var binding: FragmentMenuBinding? = null
    private lateinit var headerHelper: CommonAdminHeaderHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMenuBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val headerBinding = AdminCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonAdminHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.menuFragment = this
        binding?.menuViewModel = menuViewModel
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        val recyclerView = binding?.menuItems
        menuViewModel.getAllMenuItems().observe(viewLifecycleOwner) { items ->
            val adapter = MenuItemsAdapter(requireContext(), items)
            recyclerView?.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // メニューに新しい物を追加する画面にナビゲートする
    fun toAddMenu() {
        findNavController().navigate(R.id.action_menuFragment_to_addMenuFragment)
    }
}