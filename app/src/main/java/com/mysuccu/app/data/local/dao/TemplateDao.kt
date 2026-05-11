package com.mysuccu.app.data.local.dao
import androidx.room.*
import com.mysuccu.app.data.local.entity.TemplateEntity

@Dao
interface TemplateDao {
    @Insert
    fun insert(template: TemplateEntity): Long
}