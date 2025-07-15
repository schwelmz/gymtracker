package com.example.gymtracker.viewmodel

import UserGoalsRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.model.CalorieMode
import com.example.gymtracker.data.model.UserGoals
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GoalsViewModel(
    application: Application,
    private val userGoalsRepository: UserGoalsRepository
) : AndroidViewModel(application) {

    // Internal state that the ViewModel can write to.
    private val _uiState = MutableStateFlow(UserGoals())
    // Public, read-only state for the UI to observe.
    val uiState: StateFlow<UserGoals> = _uiState.asStateFlow()

    init {
        // Load the initial goals from storage once when the ViewModel is created.
        viewModelScope.launch {
            _uiState.value = userGoalsRepository.userGoals.first()
        }
    }

    fun updateUserGoal(
        calories: Int? = null,
        protein: Int? = null,
        carbs: Int? = null,
        fat: Int? = null,
        steps: Int? = null,
        calorieMode: CalorieMode? = null
    ) {
        // 1. Update the UI state IMMEDIATELY with the new value.
        // This makes the UI instantly responsive and fixes the "double-click" bug.
        _uiState.update { currentGoals ->
            currentGoals.copy(
                calorieGoal = calories ?: currentGoals.calorieGoal,
                proteinGoal = protein ?: currentGoals.proteinGoal,
                carbGoal = carbs ?: currentGoals.carbGoal,
                fatGoal = fat ?: currentGoals.fatGoal,
                stepsGoal = steps ?: currentGoals.stepsGoal,
                calorieMode = calorieMode ?: currentGoals.calorieMode
            )
        }
        // 2. Launch a background task to save the new state to the repository.
        viewModelScope.launch {
            userGoalsRepository.saveUserGoals(_uiState.value)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
                    val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    val userGoalsRepository = UserGoalsRepository(application)
                    return GoalsViewModel(application, userGoalsRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}