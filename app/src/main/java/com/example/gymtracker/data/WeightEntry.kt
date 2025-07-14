package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a single weight entry recorded by the user.
 * The date is the primary key to ensure only one entry per day.
 */
@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey
    val date: LocalDate,
    val weight: Float, // Use Float for precision (e.g., 80.5 kg)
    val imageUri: String? // <-- ADD THIS LINE
)