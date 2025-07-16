package com.example.gymtracker.data.model

data class WorkoutPlanStatus(
    val plan: WorkoutPlan,
    val exercises: List<Exercise>,
    val currentWeekCompletedCount: Int,
    val isGoalMetThisWeek: Boolean
)