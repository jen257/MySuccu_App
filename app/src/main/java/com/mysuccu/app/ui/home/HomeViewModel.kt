package com.mysuccu.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mysuccu.app.data.local.entity.PlantEntity
import com.mysuccu.app.data.repository.PlantRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PlantRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    fun savePlant(plant: PlantEntity) {
        viewModelScope.launch {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
            repository.savePlant(plant, userId)
        }
    }
}

// ViewModel 工厂，用于在 Activity/Fragment 中创建 ViewModel
class HomeViewModelFactory(
    private val repository: PlantRepository,
    private val supabaseClient: SupabaseClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, supabaseClient) as T
    }
}