package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.WorkoutPlan
import com.example.gymtracker.data.WorkoutPlanDao
import com.example.gymtracker.data.WorkoutPlanExerciseCrossRef
import com.example.gymtracker.data.WorkoutPlanWithExercises
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val planDao: WorkoutPlanDao =
        AppDatabase.getDatabase(application).workoutPlanDao()

    val allPlans: Flow<List<WorkoutPlanWithExercises>> = planDao.getAllPlansWithExercises()

    fun createPlan(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            planDao.insertPlan(WorkoutPlan(name = name))
        }
    }

    fun deletePlan(plan: WorkoutPlan) {
        viewModelScope.launch(Dispatchers.IO) {
            planDao.deletePlan(plan)
        }
    }

    fun updatePlanExercises(planId: Int, exerciseNames: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPlan = planDao.getPlanWithExercisesNow(planId)
            currentPlan?.exercises?.forEach {
                planDao.removeExerciseFromPlan(planId, it.name)
            }
            exerciseNames.forEach {
                planDao.insertExerciseToPlan(WorkoutPlanExerciseCrossRef(planId, it))
            }
        }
    }

    fun getPlanWithExercises(planId: Int): Flow<WorkoutPlanWithExercises> {
        return planDao.getPlanWithExercises(planId)
    }

    fun addExerciseToPlan(planId: Int, exerciseName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            planDao.insertExerciseToPlan(WorkoutPlanExerciseCrossRef(planId, exerciseName))
        }
    }

    fun removeExerciseFromPlan(planId: Int, exerciseName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            planDao.removeExerciseFromPlan(planId, exerciseName)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return WorkoutPlanViewModel(application) as T
            }
        }
    }
}
