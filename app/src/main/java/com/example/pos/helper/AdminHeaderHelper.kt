package com.example.pos.helper

import android.content.Context
import android.content.Intent
import com.example.pos_admin.MainActivity
import com.example.pos_admin.databinding.AdminCommonHeaderBinding


class CommonAdminHeaderHelper(private val binding: AdminCommonHeaderBinding, private val context: Context?) {

    fun bindHeader() {
        binding.logoutBtn.setOnClickListener {
            val sharedPreferences =
                context?.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()
            val intent = Intent(context, MainActivity::class.java)
            context?.startActivity(intent)
        }
    }
}