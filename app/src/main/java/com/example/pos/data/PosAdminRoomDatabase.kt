package com.example.pos_admin.data

import android.content.Context
import androidx.room.*
import com.example.pos.data.dao.CartItemDao
import com.example.pos.data.dao.CustomerDao
import com.example.pos_admin.Converters
import com.example.pos.data.dao.MenuItemDao
import com.example.pos.data.dao.OrderDao
import com.example.pos.data.entity.CartItem
import com.example.pos.data.entity.Customer
import com.example.pos_admin.data.dao.NotificationDao
import com.example.pos_admin.data.dao.ShiftDao
import com.example.pos_admin.data.dao.UserDao
import com.example.pos_admin.data.entity.*

@Database(entities = [User::class, Shift::class, MenuItem::class,  Notification::class, Order::class, CartItem::class, Customer::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PosAdminRoomDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun shiftDao(): ShiftDao
    abstract fun notificationDao(): NotificationDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun orderDao(): OrderDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: PosAdminRoomDatabase? = null
        fun getDatabase(context: Context): PosAdminRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PosAdminRoomDatabase::class.java,
                    "pos_admin_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}