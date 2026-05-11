package com.mysuccu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mysuccu.app.data.local.entity.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLog(log: LogEntity): Long

    @Query("SELECT * FROM log_table WHERE plant_id = :plantId ORDER BY log_date DESC")
    fun getLogsForPlant(plantId: Int): Flow<List<LogEntity>>
}