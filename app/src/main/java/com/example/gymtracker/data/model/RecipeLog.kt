package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_logs")
class RecipeLog (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val instructions: String,
    val imageUrl: String? = null,
    val timestamp: Long
    )