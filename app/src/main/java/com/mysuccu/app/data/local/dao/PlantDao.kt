package com.mysuccu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mysuccu.app.data.local.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Update
    suspend fun updatePlant(plant: PlantEntity): Int

    @Query("SELECT * FROM plant_table WHERE is_dead = 0 ORDER BY updated_at DESC")
    fun getAlivePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plant_table WHERE is_dead = 1 ORDER BY updated_at DESC")
    fun getDeadPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plant_table WHERE id = :plantId")
    suspend fun getPlantById(plantId: String): PlantEntity?

    // 引擎专属：云端同步方法区

    @Query("SELECT * FROM plant_table WHERE is_synced = 0")
    suspend fun getUnsyncedPlants(): List<PlantEntity>

    // 修复核心：加上 : Int 返回值，解决 KSP unexpected jvm signature V 报错
    @Query("UPDATE plant_table SET is_synced = 1 WHERE id = :plantId")
    suspend fun markAsSynced(plantId: String): Int

    // 修复核心：加上 : Int 返回值
    @Query("UPDATE plant_table SET is_synced = 1 WHERE id IN (:plantIds)")
    suspend fun markListAsSynced(plantIds: List<String>): Int
}