package com.mysuccu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    val name: String, // 文件夹名称，比如："阳台", "景天科", "橙梦露"

    // 🚀 灵魂字段：它的父级文件夹是谁？
    // 如果是 null，说明它是最顶层的文件夹。
    // 如果有值，说明它是某个文件夹的子文件。
    @ColumnInfo(name = "parent_id")
    val parentId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)