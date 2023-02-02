package com.example.pos.helper

import android.content.Context
import android.content.Intent
import com.example.pos_admin.MainActivity
import com.example.pos_admin.databinding.StaffCommonHeaderBinding

class CommonHeaderHelper(private val binding: StaffCommonHeaderBinding, private val context: Context?) {

    fun bindHeader() {
        val prefs = context?.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val username = prefs?.getString("username", "")
        binding.staffName.text = "Staff in charge: $username"
        binding.logoutBtn.setOnClickListener {
            val sharedPreferences =
                context?.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()
            val intent = Intent(context, MainActivity::class.java)
            context?.startActivity(intent)
        }
    }
}
