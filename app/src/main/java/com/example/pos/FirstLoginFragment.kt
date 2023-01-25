package com.example.pos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
        /*if (loginViewModel.inputFirstCode.value == null) {
            Log.d(TAG, "login ${loginViewModel.inputFirstCode.value}")
            Toast.makeText(requireContext(), "Please fill in your login code.", Toast.LENGTH_SHORT)
                .show()
        } else {*/
/*            loginViewModel.getUser().observe(viewLifecycleOwner, Observer { person ->
                loginViewModel.user.value = person

                if (!loginViewModel.isFirstLoginCodeValid()) {
                    loginViewModel.inputFirstCode.value = ""
                    Toast.makeText(
                        requireContext(),
                        "Your login code is invalid. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    loginViewModel.userSecondLoginCode.value = person.secondCode
                    Log.d(TAG,"2code ${loginViewModel.userSecondLoginCode.value}" )
                    val destination = loginViewModel.nextFragment()
                    Log.d(TAG, "1user ${loginViewModel.user.value}")
                    loginViewModel.inputFirstCode.value = ""
                    if (destination == Destination.NON_STAFF) {

                        findNavController().navigate(R.id.action_firstLoginFragment_to_secondLoginFragment)
                    } else {*/

                        findNavController().navigate((R.id.action_firstLoginFragment_to_orderFragment))
                   /* }
                }
            })

        }*/
    }
}


