package com.example.gymtracker.data.model

data class WorkoutPlanWithCompletionStatus(
    val plan: WorkoutPlan,
    val exercises: List<Exercise>,
    val currentWeekCompletedCount: Int,
    val isGoalMetThisWeek: Boolean
)