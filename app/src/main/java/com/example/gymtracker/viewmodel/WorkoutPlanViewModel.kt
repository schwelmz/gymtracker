package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.model.WorkoutPlan
import com.example.gymtracker.data.dao.WorkoutPlanDao
import com.example.gymtracker.data.model.WorkoutPlanExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutPlanWithExercises
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

import com.example.gymtracker.data.dao.WorkoutDao
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import com.example.gymtracker.data.model.WorkoutSession
import com.example.gymtracker.data.model.WorkoutPlanWithCompletionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

class WorkoutPlanViewModel(application: Application, private val workoutDao: WorkoutDao, private val workoutViewModel: WorkoutViewModel) : AndroidViewModel(application) {
    private val planDao: WorkoutPlanDao =
        AppDatabase.getDatabase(application).workoutPlanDao()

    init {
        viewModelScope.launch {
            workoutViewModel.workoutSessionEvents.collectLatest {
                refresh()
            }
        }
    }

    val allPlans: Flow<List<WorkoutPlanWithExercises>> = planDao.getAllPlansWithExercises()

    fun createPlan(name: String, goal: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            planDao.insertPlan(WorkoutPlan(name = name, goal = goal))
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

    fun updatePlanGoal(planId: Int, goal: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentPlan = planDao.getPlanByIdNow(planId)
            currentPlan?.let { plan ->
                planDao.updatePlan(plan.copy(goal = goal))
            }
        }
    }

    private val refreshTrigger = MutableStateFlow(0)

    fun refresh() {
        refreshTrigger.value++
    }

    val plannedWorkoutsThisWeek: Flow<List<WorkoutPlanWithCompletionStatus>> =
        combine(allPlans, refreshTrigger) { plans, _ -> plans }
        .combine(
            workoutDao.getSessionsInDateRange(
                java.util.Date.from(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                java.util.Date.from(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
            )
        ) { plans, sessionsThisWeek ->
            plans.map { planWithExercises ->
                val planExercises = planWithExercises.exercises.map { it.name }.toSet()
                val planId = planWithExercises.plan.id

                if (planExercises.isEmpty()) {
                    // Handle case where plan has no exercises, can't be completed.
                    WorkoutPlanWithCompletionStatus(
                        plan = planWithExercises.plan,
                        exercises = planWithExercises.exercises,
                        currentWeekCompletedCount = 0,
                        isGoalMetThisWeek = false
                    )
                } else {
                    val sessionsForThisPlan = sessionsThisWeek.filter { it.planId == planId }

                    val sessionsByDate = sessionsForThisPlan.groupBy {
                        Instant.ofEpochMilli(it.date.time)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }

                    val completedCount = sessionsByDate.count { (_, sessionsOnDate) ->
                        val loggedExercisesOnDate = sessionsOnDate.map { it.exerciseName }.toSet()
                        loggedExercisesOnDate.containsAll(planExercises)
                    }

                    val isGoalMet = planWithExercises.plan.goal?.let { goal ->
                        completedCount >= goal
                    } ?: false

                    WorkoutPlanWithCompletionStatus(
                        plan = planWithExercises.plan,
                        exercises = planWithExercises.exercises,
                        currentWeekCompletedCount = completedCount,
                        isGoalMetThisWeek = isGoalMet
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val incompleteWorkoutsThisWeek: Flow<List<WorkoutPlanWithCompletionStatus>> =
        plannedWorkoutsThisWeek.map { plans ->
            plans.filter { !it.isGoalMetThisWeek }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val workoutDao = AppDatabase.getDatabase(application).workoutDao()
                val workoutViewModel = WorkoutViewModel(application) // Create an instance of WorkoutViewModel
                return WorkoutPlanViewModel(application, workoutDao, workoutViewModel) as T
            }
        }
    }
}
