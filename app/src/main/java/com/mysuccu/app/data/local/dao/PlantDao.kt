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
    fun insertPlant(plant: PlantEntity): Long

    @Update
    fun updatePlant(plant: PlantEntity): Int

    @Query("SELECT * FROM plant_table WHERE is_dead = 0")
    fun getAlivePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plant_table WHERE is_dead = 1")
    fun getDeadPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plant_table WHERE id = :plantId")
    fun getPlantById(plantId: Int): PlantEntity?
}