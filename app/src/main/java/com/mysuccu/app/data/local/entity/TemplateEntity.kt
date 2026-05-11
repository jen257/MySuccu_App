//养护模板表
package com.mysuccu.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_table")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "template_name")
    val templateName: String, // 模板名称
    @ColumnInfo(name = "actions_json")
    val actionsJson: String // 预设养护操作组合 (以 JSON 字符串格式存储)
)