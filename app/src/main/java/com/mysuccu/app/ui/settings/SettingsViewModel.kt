package com.mysuccu.app.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysuccu.app.data.remote.SupabaseModule
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// 🚀 定义云端存储的数据结构 (对应你 Supabase 的 feedback 表)
@Serializable
data class FeedbackDto(
    val type: String,
    val content: String,
    val contact: String,
    val is_anonymous: Boolean,
    val created_at: String = "" // 让 Supabase 后台自动填充时间戳
)

data class SettingsUiState(
    val isLoading: Boolean = true,
    val customUserName: String? = null,
    val userAvatarUri: Uri? = null,
    val currentPlantCount: Int = 0,
    val maxPlantCount: Int = 20,
    val isProUser: Boolean = false
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // 后期这里可以从 Supabase 的 users 表读取真实状态
            delay(800)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refreshData() { loadUserData() }

    fun updateUserName(newName: String?) {
        _uiState.update { it.copy(customUserName = newName) }
    }

    fun updateUserAvatar(uri: Uri) {
        _uiState.update { it.copy(userAvatarUri = uri) }
    }

    /**
     * 🚀 联机提交反馈：通过 Supabase 真实存入 PostgreSQL 数据库
     */
    suspend fun submitFeedback(
        type: String,
        content: String,
        contact: String,
        isAnonymous: Boolean
    ): Boolean {
        return try {
            // 组装数据对象
            val feedback = FeedbackDto(
                type = type,
                content = content,
                contact = if (isAnonymous) "Anonymous" else contact,
                is_anonymous = isAnonymous
            )

            // 存入云端 (确保你在 Supabase 后台创建了名为 'feedbacks' 的表)
            SupabaseModule.client.from("feedbacks").insert(feedback)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}