package com.example.pos_admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.Room
import com.example.pos_admin.data.PosAdminRoomDatabase

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    lateinit var database: PosAdminRoomDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val sharedPreferences =
                    this.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
                sharedPreferences?.edit()?.clear()?.apply()
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun removeLogoutButtonFromMenu(menu: Menu) {
        menu.removeItem(R.id.logout)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()

    }
}
