package com.example.gymtracker.viewmodel

import UserGoalsRepository
import android.app.Application
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.CalorieMode
import com.example.gymtracker.data.HealthConnectManager
import com.example.gymtracker.data.HealthDataRepository
import com.example.gymtracker.data.TodayHealthStats
import com.example.gymtracker.data.UserGoals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the different states the HomeScreen UI can be in
sealed interface HomeUiState {
    object Idle : HomeUiState
    object HealthConnectNotInstalled : HomeUiState
    object PermissionsNotGranted : HomeUiState
    data class Success(val stats: TodayHealthStats) : HomeUiState
}

// New state class to hold both UI state and user goals
data class HomeState(
    val healthUiState: HomeUiState = HomeUiState.Idle,
    val userGoals: UserGoals = UserGoals() // Start with default goals
)

open class HomeViewModel(
    application: Application,
    private val healthDataRepository: HealthDataRepository,
    private val userGoalsRepository: UserGoalsRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeState())
    open val uiState = _uiState.asStateFlow()

    private val healthConnectManager = HealthConnectManager(application)
    val permissions = healthDataRepository.permissions

    init {
        // --- THIS BLOCK IS CORRECTED ---
        // Load the initial state from the repository ONCE at startup.
        // This removes the continuous collector that was causing the race condition.
        viewModelScope.launch {
            val initialGoals = userGoalsRepository.userGoals.first()
            _uiState.update { it.copy(userGoals = initialGoals) }
        }
        checkAvailabilityAndPermissions()
    }

    fun updateUserGoal(
        calories: Int? = null,
        protein: Int? = null,
        carbs: Int? = null,
        fat: Int? = null,
        steps: Int? = null,
        calorieMode: CalorieMode? = null
    ) {
        // --- THIS FUNCTION IS ALSO MADE MORE ROBUST ---
        // 1. Update the UI state IMMEDIATELY using the safe 'update' function.
        //    This provides instant feedback to the user and fixes the bug.
        _uiState.update { currentState ->
            currentState.copy(
                userGoals = currentState.userGoals.copy(
                    calorieGoal = calories ?: currentState.userGoals.calorieGoal,
                    proteinGoal = protein ?: currentState.userGoals.proteinGoal,
                    carbGoal = carbs ?: currentState.userGoals.carbGoal,
                    fatGoal = fat ?: currentState.userGoals.fatGoal,
                    stepsGoal = steps ?: currentState.userGoals.stepsGoal,
                    calorieMode = calorieMode ?: currentState.userGoals.calorieMode
                )
            )
        }

        // 2. Launch a background coroutine to save the new state to the repository for persistence.
        viewModelScope.launch {
            // We read the state again inside the coroutine to ensure we save the most recent update.
            userGoalsRepository.saveUserGoals(_uiState.value.userGoals)
        }
    }


    fun checkAvailabilityAndPermissions() {
        if (healthConnectManager.healthConnectAvailability != HealthConnectClient.SDK_AVAILABLE) {
            _uiState.update { it.copy(healthUiState = HomeUiState.HealthConnectNotInstalled) }
            return
        }

        viewModelScope.launch {
            val hasPermissions = healthDataRepository.hasAllPermissions()
            if (hasPermissions) {
                loadHealthData()
            } else {
                _uiState.update { it.copy(healthUiState = HomeUiState.PermissionsNotGranted) }
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
            _uiState.update { it.copy(healthUiState = newHealthState) }
        }
    }
    // ... Factory companion object remains the same ...
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    val healthDataRepository = HealthDataRepository(application)
                    val userGoalsRepository = UserGoalsRepository(application)
                    return HomeViewModel(
                        application = application,
                        healthDataRepository = healthDataRepository,
                        userGoalsRepository = userGoalsRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}