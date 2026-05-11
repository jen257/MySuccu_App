package com.mysuccu.app

import android.app.Application
import com.mysuccu.app.data.local.AppDatabase

class MySuccuApplication : Application() {
    // 使用 lazy 委托，确保数据库只在第一次真正被使用时才去创建，极大地优化了 App 冷启动速度！
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        // 这里以后还可以用来初始化第三方服务，比如 Supabase云端 或 Coil图片加载器
    }
}