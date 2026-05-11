package com.mysuccu.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mysuccu.app.data.local.dao.* // 🚀 确保引入了所有的 DAO
import com.mysuccu.app.data.local.entity.* // 🚀 确保引入了所有的 Entity

@Database(
    entities = [
        CategoryEntity::class,
        PlantEntity::class,
        LogEntity::class,
        TemplateEntity::class,
        UserEnvEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun plantDao(): PlantDao
    abstract fun logDao(): LogDao
    // 🚀 必须加上这两个，否则 KSP 会报错说对应的 Entity 找不到处理者
    abstract fun templateDao(): TemplateDao
    abstract fun userEnvDao(): UserEnvDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mysuccu_database"
                )
                    // 🚀 添加此行：如果数据库结构变了，自动重建而不是崩溃（开发阶段常用）
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}