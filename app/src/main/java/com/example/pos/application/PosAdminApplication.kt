package com.example.pos_admin.application

import android.app.Application
import com.example.pos_admin.data.PosAdminRoomDatabase

class PosAdminApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        PosAdminRoomDatabase.getDatabase(this)
    }
}

