package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user-defined food template.
 * All nutritional values are stored per 100g to allow for easy calculation.
 */
@Entity(tableName = "custom_food")
data class CustomFood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    // Storing nutrition per 100g is best practice
    val caloriesPer100g: Int,
    val proteinPer100g: Int,
    val carbsPer100g: Int,
    val fatPer100g: Int
)