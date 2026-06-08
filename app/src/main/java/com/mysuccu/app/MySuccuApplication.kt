package com.mysuccu.app

import android.app.Application
import com.mysuccu.app.data.local.AppDatabase
import com.mysuccu.app.data.repository.PlantRepository
import com.mysuccu.app.data.remote.SupabaseModule // 确保 import 正确

class MySuccuApplication : Application() {

    // 1. 数据库单例
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // 2. 核心仓库单例：UI 层从此直接调用这个 repository
    val plantRepository: PlantRepository by lazy {
        PlantRepository(
            plantDao = database.plantDao(),
            supabaseClient = SupabaseModule.client
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}