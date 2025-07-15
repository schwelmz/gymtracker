package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.HealthConnectManager
import com.example.gymtracker.data.model.HealthDataRepository
import com.example.gymtracker.data.model.TodayHealthStats
import com.example.gymtracker.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the different states the HomeScreen UI can be in
sealed interface HomeUiState {
    object Idle : HomeUiState
    object HealthConnectNotInstalled : HomeUiState
    object PermissionsNotGranted : HomeUiState
    data class Success(val stats: TodayHealthStats) : HomeUiState
}

// NOTE: The separate `HomeState` data class is no longer needed,
// as the goals are managed by GoalsViewModel.

open class HomeViewModel(
    application: Application,
    private val healthDataRepository: HealthDataRepository,
    private val weightViewModel: WeightViewModel
    // `userGoalsRepository` is removed from the constructor
) : AndroidViewModel(application) {
    val weightEntries: Flow<List<WeightEntry>> = weightViewModel.allWeightEntries
    // This state flow now only deals with health data.
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    open val uiState = _uiState.asStateFlow()

    private val healthConnectManager = HealthConnectManager(application)
    val permissions = healthDataRepository.permissions

    init {
        checkAvailabilityAndPermissions()
    }

    // `updateUserGoal` function is completely removed.

    fun checkAvailabilityAndPermissions() {
        if (healthConnectManager.healthConnectAvailability != HealthConnectClient.SDK_AVAILABLE) {
            _uiState.update { HomeUiState.HealthConnectNotInstalled }
            return
        }
        viewModelScope.launch {
            if (healthDataRepository.hasAllPermissions()) {
                loadHealthData()
            } else {
                _uiState.update { HomeUiState.PermissionsNotGranted }
            }
        }
    }

    private fun loadHealthData() {
        viewModelScope.launch {
            val stats = healthDataRepository.readTodayHealthStats()
            val newHealthState = if (stats != null) {
                HomeUiState.Success(stats)
            } else {
                HomeUiState.PermissionsNotGranted
            }
            _uiState.update { newHealthState }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    val healthDataRepository = HealthDataRepository(application)
                    // We no longer create or pass UserGoalsRepository here.
                    return HomeViewModel(
                        application, healthDataRepository,
                        weightViewModel = WeightViewModel(application)
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}