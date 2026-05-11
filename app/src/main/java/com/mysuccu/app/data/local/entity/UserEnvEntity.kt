//用户微气候环境表
package com.mysuccu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_env_table")
data class UserEnvEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "city_code")
    val cityCode: String, // 所在城市代码 (用于获取天气预报)
    @ColumnInfo(name = "env_type")
    val envType: String, // 养护场景类型 (如: 露养、全封闭阳台)
    @ColumnInfo(name = "alert_rules")
    val alertRules: String? // 智能提醒规则配置 (可选)
)