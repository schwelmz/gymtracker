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
import com.example.gymtracker.data.repository.UserPreferencesRepository
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
    object PermissionsDeclined : HomeUiState
}

// NOTE: The separate `HomeState` data class is no longer needed,
// as the goals are managed by GoalsViewModel.

open class HomeViewModel(
    application: Application,
    private val healthDataRepository: HealthDataRepository,
    private val weightViewModel: WeightViewModel,
    val userPreferencesRepository: UserPreferencesRepository

) : AndroidViewModel(application) {
    val weightEntries: Flow<List<WeightEntry>> = weightViewModel.allWeightEntries
    // This state flow now only deals with health data.
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    open val uiState = _uiState.asStateFlow()

    private val healthConnectManager = HealthConnectManager(application)
    val permissions = healthDataRepository.permissions
    val dismissedDisabledCard: Flow<Boolean> = userPreferencesRepository.dismissedDisabledCardFlow



    init {
        checkAvailabilityAndPermissions()
    }

    // `updateUserGoal` function is completely removed.

    fun checkAvailabilityAndPermissions() {
        viewModelScope.launch {
            userPreferencesRepository.healthPermissionsDeclinedFlow.collect { declined ->
                if (declined) {
                    _uiState.value = HomeUiState.PermissionsDeclined
                    return@collect
                }

                if (healthDataRepository.hasAllPermissions()) {
                    loadHealthData()
                } else {
                    _uiState.value = HomeUiState.PermissionsNotGranted
                }
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
    fun userDeclinedPermissions() {
        viewModelScope.launch {
            userPreferencesRepository.setHealthPermissionsDeclined(true)
            _uiState.value = HomeUiState.PermissionsDeclined
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val healthDataRepository = HealthDataRepository(application)
                val userPreferences = UserPreferencesRepository(application)
                return HomeViewModel(
                    application,
                    healthDataRepository,
                    WeightViewModel(application),
                    userPreferences
                ) as T
            }
        }

    }
}