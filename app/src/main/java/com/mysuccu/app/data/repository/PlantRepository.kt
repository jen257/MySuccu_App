package com.mysuccu.app.data.repository

import com.mysuccu.app.data.local.dao.PlantDao
import com.mysuccu.app.data.local.entity.PlantEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// 1. Data Mapper: Supabase 数据传输对象 (DTO)
// 完美对应你的 Entity 字段，映射为 JSON 格式

@Serializable
data class PlantDto(
    val id: String,
    val user_id: String, // 绑定到 Supabase 的用户身份
    val folder_id: String?, // 🚀 核心修改：移除旧的 category_id，启用无极分类的 folder_id
    val name: String,
    val price: Double,
    val status_tag: String?,
    val arrive_date: Long,
    val is_dead: Boolean,
    val dead_date: Long?,
    val intro: String?,
    val pot_price: Double,
    val soil_price: Double,
    val repot_date: Long?,
    val image_urls: String?,
    val updated_at: Long
)

// 扩展方法：本地对象 翻译为 -> 云端对象
fun PlantEntity.toDto(userId: String) = PlantDto(
    id = this.id,
    user_id = userId,
    folder_id = this.folderId, // 🚀 核心映射：完美打通底层数据结构
    name = this.name,
    price = this.price,
    status_tag = this.statusTag,
    arrive_date = this.arriveDate,
    is_dead = this.isDead,
    dead_date = this.deadDate,
    intro = this.intro,
    pot_price = this.potPrice,
    soil_price = this.soilPrice,
    repot_date = this.repotDate,
    image_urls = this.imageUrls,
    updated_at = this.updatedAt
)

// 2. Repository：UI 层唯一的数据交互入口

class PlantRepository(
    private val plantDao: PlantDao,
    private val supabaseClient: SupabaseClient
) {
    // 独立的后台工作协程，确保不卡顿主 UI
    private val syncScope = CoroutineScope(Dispatchers.IO)

    // 🟢 读操作：绝对的极速体验，永远直接查本地
    fun getAlivePlantsStream(): Flow<List<PlantEntity>> = plantDao.getAlivePlants()
    fun getDeadPlantsStream(): Flow<List<PlantEntity>> = plantDao.getDeadPlants()

    // 🟢 写操作：本地秒存 + 后台静默推云
    suspend fun savePlant(plant: PlantEntity, currentUserId: String?) {
        // 1. 保存到本地，默认 isSynced = false
        val plantToSave = plant.copy(isSynced = false, updatedAt = System.currentTimeMillis())
        plantDao.insertPlant(plantToSave)

        // 2. 如果用户已登录，启动后台无感同步
        if (!currentUserId.isNullOrEmpty()) {
            syncScope.launch {
                pushSinglePlantToCloud(plantToSave, currentUserId)
            }
        }
    }

    // 🟢 更新操作：同理，修改本地 + 后台推云
    suspend fun updatePlant(plant: PlantEntity, currentUserId: String?) {
        val plantToUpdate = plant.copy(isSynced = false, updatedAt = System.currentTimeMillis())
        plantDao.updatePlant(plantToUpdate)

        if (!currentUserId.isNullOrEmpty()) {
            syncScope.launch {
                pushSinglePlantToCloud(plantToUpdate, currentUserId)
            }
        }
    }

    // 私有核心：静默网络推流引擎

    private suspend fun pushSinglePlantToCloud(plant: PlantEntity, userId: String) {
        try {
            val dto = plant.toDto(userId)
            // 调用 Supabase API: upsert (无则插入，有则更新)
            supabaseClient.postgrest["plants"].upsert(dto)

            // 云端接收成功！把本地标志位改为 1
            plantDao.markAsSynced(plant.id)
        } catch (e: Exception) {
            // 弱网断开？毫无影响，被吞掉的错误等待下次批量同步
            e.printStackTrace()
        }
    }

    // 补救机制：在 App 启动时或网络恢复时调用
    suspend fun syncAllPendingData(currentUserId: String) {
        try {
            // 找出所有之前因为断网没发出去的数据
            val pendingPlants = plantDao.getUnsyncedPlants()
            if (pendingPlants.isEmpty()) return

            val dtoList = pendingPlants.map { it.toDto(currentUserId) }

            // 批量打给 Supabase
            supabaseClient.postgrest["plants"].upsert(dtoList)

            // 批量抹去本地红点
            val pendingIds = pendingPlants.map { it.id }
            plantDao.markListAsSynced(pendingIds)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}