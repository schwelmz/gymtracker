package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "exercises",
    // Ensure that no two exercises can have the same name.
    indices = [Index(value = ["name"], unique = true)]
)
data class Exercise(
    @PrimaryKey val name: String, // Unique ID for the database
    val description: String,
    val isCustom: Boolean = false
)