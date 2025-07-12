package com.example.gymtracker.viewmodel

import android.app.Application
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.HealthConnectManager
import com.example.gymtracker.data.HealthDataRepository
import com.example.gymtracker.data.TodayHealthStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents the different states the HomeScreen UI can be in
sealed interface HomeUiState {
    object Idle : HomeUiState
    object HealthConnectNotInstalled : HomeUiState
    object PermissionsNotGranted : HomeUiState
    data class Success(val stats: TodayHealthStats) : HomeUiState
}

open class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    open val uiState = _uiState.asStateFlow()

    private val healthConnectManager = HealthConnectManager(application)
    private val healthDataRepository = HealthDataRepository(application)

    val permissions = healthDataRepository.permissions

    fun checkAvailabilityAndPermissions() {
        if (healthConnectManager.healthConnectAvailability != HealthConnectClient.SDK_AVAILABLE) {
            _uiState.value = HomeUiState.HealthConnectNotInstalled
            return
        }

        viewModelScope.launch {
            if (healthDataRepository.hasAllPermissions()) {
                loadHealthData()
            } else {
                _uiState.value = HomeUiState.PermissionsNotGranted
            }
        }
    }

    private fun loadHealthData() {
        viewModelScope.launch {
            val stats = healthDataRepository.readTodayHealthStats()
            if (stats != null) {
                _uiState.value = HomeUiState.Success(stats)
            } else {
                // This could happen if permissions are revoked after the initial check
                _uiState.value = HomeUiState.PermissionsNotGranted
            }
        }
    }

    // Factory for creating the ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
            ): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return HomeViewModel(
                        application = Application() // This is a placeholder, a proper DI framework would be better
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}