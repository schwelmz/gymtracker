package com.example.gymtracker.data.model
enum class CalorieMode {
    DEFICIT,
    SURPLUS
}

// In a new file, e.g., data/UserGoals.kt
data class UserGoals(
    val calorieGoal: Int = 2000,
    val proteinGoal: Int = 150,
    val carbGoal: Int = 250,
    val fatGoal: Int = 60,
    val stepsGoal: Int = 10000,
    val calorieMode: CalorieMode = CalorieMode.DEFICIT
)