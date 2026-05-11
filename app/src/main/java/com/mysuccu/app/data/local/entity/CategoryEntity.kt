package com.mysuccu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String, // 分类名称 (如：景天科、冰玉)

    @ColumnInfo(name = "parent_id")
    val parentId: Int? = null // 指向上级分类的ID，实现无限嵌套
)