//养护日志表
package com.mysuccu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_table")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 唯一主键

    @ColumnInfo(name = "plant_id")
    val plantId: Int, // 关联植物表的ID
    @ColumnInfo(name = "action_type")
    val actionType: String, // 养护/操作类型 (如: 浇水, 施肥)
    @ColumnInfo(name = "log_date")
    val logDate: Long, // 操作时间/养护时间戳
    val note: String?, // 养护备注
    @ColumnInfo(name = "local_img_path")
    val localImgPath: String?, // 本地压缩图片路径
    @ColumnInfo(name = "cloud_img_url")
    val cloudImgUrl: String? // 云端 R2 存储图片 URL
)