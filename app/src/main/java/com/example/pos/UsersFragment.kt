package com.example.pos_admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.adapter.UsersAdapter
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentUsersBinding
import com.example.pos_admin.model.UsersViewModel
import com.example.pos_admin.model.UsersViewModelFactory

/*Fragment to show users inside recyclerview*/

class UsersFragment : Fragment() {

    private lateinit var usersViewModel: UsersViewModel
    private var binding: FragmentUsersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentUsersBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        //Get usersViewModel
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(dao)
        val factory = UsersViewModelFactory(repository)
        usersViewModel = ViewModelProvider(this, factory)[UsersViewModel::class.java]
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.usersFragment = this
        binding?.usersViewModel = usersViewModel
        val recyclerView = binding?.users
        /*Show all users*/
        usersViewModel.getAllUsers().observe(viewLifecycleOwner, Observer { users ->
            val adapter = UsersAdapter(requireContext(), users)
            recyclerView?.adapter = adapter
        })
    }

    fun nextFragment() {
        findNavController().navigate(R.id.action_usersFragment_to_addUsersFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}

