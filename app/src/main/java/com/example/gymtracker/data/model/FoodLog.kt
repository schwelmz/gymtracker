package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.gymtracker.data.model.FoodTemplate

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
    val grams: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val timestamp: Long
)

