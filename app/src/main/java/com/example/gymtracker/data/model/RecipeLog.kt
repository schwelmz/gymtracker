package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_logs")
data class RecipeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val instructions: String?,
    val imageUrl: String?,
    val timestamp: Long,
    // Store the calculated totals at the time of logging.
    val totalCalories: Int,
    val totalProtein: Int,
    val totalCarbs: Int,
    val totalFat: Int,
    // Store a JSON snapshot of the ingredients.
    val ingredientsJson: String
)