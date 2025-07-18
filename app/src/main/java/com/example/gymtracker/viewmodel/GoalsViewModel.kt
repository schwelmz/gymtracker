package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.model.UserGoals
import com.example.gymtracker.data.repository.UserGoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(
    application: Application,
    private val userGoalsRepository: UserGoalsRepository
    ) : AndroidViewModel(application) {


    val userGoals = userGoalsRepository.userGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserGoals())

    fun saveUserGoals(goals: UserGoals) {
        viewModelScope.launch {
            userGoalsRepository.saveUserGoals(goals)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val userGoalsRepository = UserGoalsRepository(application)
                return GoalsViewModel(
                    application,
                    userGoalsRepository
                ) as T
            }
        }
    }
}
