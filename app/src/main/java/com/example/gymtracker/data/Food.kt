package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food (
    @PrimaryKey val barcode: String,
    val name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val imageUri: String? = null,
)