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
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat:Float,
    // Store a JSON snapshot of the ingredients.
    val ingredientsJson: String
)