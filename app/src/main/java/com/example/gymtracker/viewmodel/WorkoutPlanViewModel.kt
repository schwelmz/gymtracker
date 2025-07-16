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
import com.example.gymtracker.data.model.WorkoutPlanStatus

import com.example.gymtracker.data.model.WorkoutSession
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class WorkoutPlanViewModel(application: Application, private val workoutDao: WorkoutDao) : AndroidViewModel(application) {
    private val planDao: WorkoutPlanDao =
        AppDatabase.getDatabase(application).workoutPlanDao()

    val allPlans: Flow<List<WorkoutPlanWithExercises>> = planDao.getAllPlansWithExercises()

    fun createPlan(name: String, goal: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            planDao.insertPlan(WorkoutPlan(name = name, goal = goal, creationDate = Date()))
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

    private fun getSessionsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutSession>> {
        val start = java.util.Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val end = java.util.Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
        return workoutDao.getSessionsInDateRange(start, end)
    }


    val plannedWorkoutsThisWeek: Flow<List<WorkoutPlanStatus>> =
        combine(
            allPlans,
            getSessionsForDateRange(
                LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            )
        ) { plans, sessionsThisWeek ->
            mapPlansToStatus(plans, sessionsThisWeek)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val incompleteWorkoutsThisWeek: Flow<List<WorkoutPlanStatus>> =
        plannedWorkoutsThisWeek.map { plans ->
            plans.filter { !it.isGoalMetThisWeek && (it.plan.goal ?: 1) > 0 }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val globalWeeklyStreak: StateFlow<Int> = combine(
        allPlans,
        workoutDao.getAllSessions()
    ) { plans, allSessions ->
        val plansWithGoals = plans.filter { (it.plan.goal ?: 0) > 0 }
        if (plansWithGoals.isEmpty()) {
            return@combine 0
        }

        // Check current week
        val currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sessionsThisWeek = allSessions.filter {
            val sessionDate = it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            !sessionDate.isBefore(currentWeekStart)
        }
        val areAllGoalsMetThisWeek = areAllGoalsMetForWeek(plansWithGoals, sessionsThisWeek)

        // Check past weeks
        var consecutiveWeeks = 0
        var weekToCheck = LocalDate.now().minusWeeks(1)
        val oldestSessionDate = allSessions.minOfOrNull { it.date }?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()


        while (true) {
            val weekStart = weekToCheck.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekEnd = weekToCheck.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            if (oldestSessionDate != null && weekEnd.isBefore(oldestSessionDate)) {
                break
            }

            val plansToConsider = plansWithGoals.filter {
                it.plan.creationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(weekEnd)
            }

            if (plansToConsider.isEmpty()) {
                consecutiveWeeks++
                weekToCheck = weekToCheck.minusWeeks(1)
                continue
            }

            val sessionsForWeek = allSessions.filter {
                val sessionDate = it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                !sessionDate.isBefore(weekStart) && !sessionDate.isAfter(weekEnd)
            }

            if (sessionsForWeek.isEmpty() && plansToConsider.any { (it.plan.goal ?: 0) > 0 }) {
                break // Stop if there are no sessions and goals exist
            }

            if (areAllGoalsMetForWeek(plansToConsider, sessionsForWeek)) {
                consecutiveWeeks++
                weekToCheck = weekToCheck.minusWeeks(1)
            } else {
                break
            }
        }

        var streak = consecutiveWeeks
        if (areAllGoalsMetThisWeek) {
            streak++
        }
        streak

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun areAllGoalsMetForWeek(plans: List<WorkoutPlanWithExercises>, sessions: List<WorkoutSession>): Boolean {
        if (plans.isEmpty()) return true

        val sessionsByDate = sessions.groupBy {
            it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }

        return plans.all { planWithExercises ->
            val planExercises = planWithExercises.exercises.map { it.name }.toSet()
            val goal = planWithExercises.plan.goal ?: 1

            if (planExercises.isEmpty()) {
                goal == 0
            } else {
                val completedCount = sessionsByDate.count { (_, sessionsOnDate) ->
                    val loggedExercisesOnDate = sessionsOnDate.map { it.exerciseName }.toSet()
                    loggedExercisesOnDate.containsAll(planExercises)
                }
                completedCount >= goal
            }
        }
    }

    private fun mapPlansToStatus(plans: List<WorkoutPlanWithExercises>, sessions: List<WorkoutSession>): List<WorkoutPlanStatus> {
        return plans.map { planWithExercises ->
            val planExercises = planWithExercises.exercises.map { it.name }.toSet()
            if (planExercises.isEmpty()) {
                WorkoutPlanStatus(
                    plan = planWithExercises.plan,
                    exercises = planWithExercises.exercises,
                    currentWeekCompletedCount = 0,
                    isGoalMetThisWeek = (planWithExercises.plan.goal ?: 0) == 0
                )
            } else {
                val sessionsByDate = sessions.groupBy {
                    it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                }
                val completedCount = sessionsByDate.count { (_, sessionsOnDate) ->
                    val loggedExercisesOnDate = sessionsOnDate.map { it.exerciseName }.toSet()
                    loggedExercisesOnDate.containsAll(planExercises)
                }
                val isGoalMet = (planWithExercises.plan.goal ?: 1).let { goal ->
                    completedCount >= goal
                }
                WorkoutPlanStatus(
                    plan = planWithExercises.plan,
                    exercises = planWithExercises.exercises,
                    currentWeekCompletedCount = completedCount,
                    isGoalMetThisWeek = isGoalMet
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val workoutDao = AppDatabase.getDatabase(application).workoutDao()
                return WorkoutPlanViewModel(application, workoutDao) as T
            }
        }
    }
}
