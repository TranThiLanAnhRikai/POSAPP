package com.example.pos_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pos.const.Destination
import com.example.pos_admin.R
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.repository.UserRepository
import com.example.pos_admin.databinding.FragmentFirstLoginBinding
import com.example.pos_admin.model.LoginViewModel
import com.example.pos_admin.model.LoginViewModelFactory

/** Firs Fragment for user to fill in code
- Bind with LoginViewModel
- Display messages if user doesn't fill in any code or the code user filled in doesn't match any user
- if user is staff, navigate to order fragment
- if user is admin, navigate to second login fragment where user has to fill in another code
 */
class FirstLoginFragment : Fragment() {

    private var binding: FragmentFirstLoginBinding? = null
    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(
            UserRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).userDao()
            )
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.firstLoginFragment = this
        binding?.loginViewModel = loginViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun nextScreen() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        if (loginViewModel.inputFirstCode.value == null) {
            builder.setMessage("Please fill in your login code.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (!loginViewModel.isFirstLoginCodeValid()) {
            builder.setMessage("Login code is invalid. Please try again.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            binding?.loginEditText?.text = null
        }
        else {
            loginViewModel.getUser().observe(viewLifecycleOwner, Observer { person ->
                loginViewModel.user.value = person
                loginViewModel.userSecondLoginCode.value = person.secondCode
                val destination = loginViewModel.nextFragment()
                binding?.loginEditText?.text = null
                if (destination == Destination.NON_STAFF) {
                    findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
                } else {
                    findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
                }

        })
        }
        /*findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)*/
    }
}