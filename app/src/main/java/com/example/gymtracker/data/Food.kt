package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val barcode: String,
    val name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val grams: Int, // To store the weight of the food
    val timestamp: Long, // To know when the food was logged
    val imageUri: String? = null,
)