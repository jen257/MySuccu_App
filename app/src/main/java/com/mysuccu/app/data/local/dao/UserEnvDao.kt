package com.mysuccu.app.data.local.dao
import androidx.room.*
import com.mysuccu.app.data.local.entity.UserEnvEntity

@Dao
interface UserEnvDao {
    @Insert
    fun insert(env: UserEnvEntity): Long
}