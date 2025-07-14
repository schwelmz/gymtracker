package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The unified template for any food item. Stores nutritional info per 100g.
 * This can be from a scanned barcode, user-defined, or predefined.
 * The barcode is unique to ensure we don't have duplicate scanned items.
 */
@Entity(
    tableName = "food_templates",
    // Ensure barcodes are unique, allowing nulls for custom/predefined foods.
    indices = [Index(value = ["barcode"], unique = true)]
)
data class FoodTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // Barcode can be null for foods that don't have one (e.g., "Apple").
    val barcode: String? = null,
    val name: String,
    val imageUrl: String? = null,
    val caloriesPer100g: Int,
    val proteinPer100g: Int,
    val carbsPer100g: Int,
    val fatPer100g: Int
)