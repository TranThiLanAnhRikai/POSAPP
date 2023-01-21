package com.example.pos_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.adapter.MenuItemsAdapter
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentMenuBinding
import com.example.pos_admin.model.MenuViewModel
import com.example.pos_admin.model.MenuViewModelFactory

class MenuFragment : Fragment() {
    private lateinit var menuViewModel: MenuViewModel
   private var binding: FragmentMenuBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMenuBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao()
        val repository = MenuItemRepository(dao)
        val factory = MenuViewModelFactory(repository)
        menuViewModel = ViewModelProvider(this, factory)[MenuViewModel::class.java]
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.menuFragment = this
        binding?.menuViewModel = menuViewModel
        val recyclerView = binding?.menuItems
        menuViewModel.getAllMenuItems().observe(viewLifecycleOwner, Observer { items ->
            val adapter = MenuItemsAdapter(requireContext(), items)
            recyclerView?.adapter = adapter
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun goToAddMenuFragment() {
        findNavController().navigate(R.id.action_menuFragment_to_addMenuFragment)
    }
}