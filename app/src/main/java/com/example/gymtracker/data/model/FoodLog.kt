package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a single instance of a food being logged by the user.
 * It contains the amount and a timestamp, and references a FoodTemplate.
 */
@Entity(
    tableName = "food_logs",
    foreignKeys = [ForeignKey(
        entity = FoodTemplate::class,
        parentColumns = ["id"],
        childColumns = ["templateId"],
        onDelete = ForeignKey.Companion.CASCADE
    )]
)
data class FoodLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val templateId: Int,
    val grams: Float,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val timestamp: Long
)

