package com.mysuccu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plant_table")
data class PlantEntity(
    // 🚀 核心改造 1：升级为 UUID 字符串，防止云端多设备数据冲突
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "folder_id")
    val folderId: String? = null,

    val name: String, // 多肉名称

    val price: Double = 0.0, // 购入肉价

    @ColumnInfo(name = "status_tag")
    val statusTag: String? = null, // 状态标签 (如: 健康, 徒长)

    @ColumnInfo(name = "arrive_date")
    val arriveDate: Long, // 到家时间戳

    @ColumnInfo(name = "is_dead")
    val isDead: Boolean = false, // 存活状态 (false=活着, true=死亡/出坑进公墓)

    @ColumnInfo(name = "dead_date")
    val deadDate: Long? = null, // 阵亡时间戳

    // --- 拓展字典 ---
    val intro: String? = null, // 个性介绍

    @ColumnInfo(name = "pot_price")
    val potPrice: Double = 0.0, // 盆器费用

    @ColumnInfo(name = "soil_price")
    val soilPrice: Double = 0.0, // 土壤费用

    @ColumnInfo(name = "repot_date")
    val repotDate: Long? = null, // 上盆日期

    @ColumnInfo(name = "image_urls")
    val imageUrls: String? = null, // 图片组地址 (JSON 格式存储多张图)

    // 🚀 核心改造 2：云端同步专属标志位
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false, // 是否已同步到云端

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis() // 最后修改时间，解决冲突的依据
)