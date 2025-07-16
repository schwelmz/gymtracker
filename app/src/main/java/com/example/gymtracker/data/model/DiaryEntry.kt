package com.example.gymtracker.data.model

import com.example.gymtracker.data.dao.FoodLogWithDetails

// A sealed interface to represent any item that can appear in the food diary.
// It ensures that all entries have a common way to access their ID and timestamp for sorting.
sealed class DiaryEntry {
    abstract val id: Long
    abstract val timestamp: Long

    data class Food(val details: FoodLogWithDetails) : DiaryEntry() {
        // Use a combination of a prefix and the real ID to ensure uniqueness across types.
        override val id: Long = "food_".hashCode() + details.logId.toLong()
        override val timestamp: Long = details.timestamp
    }

    data class Recipe(val log: RecipeLog) : DiaryEntry() {
        override val id: Long = "recipe_".hashCode() + log.id.toLong()
        override val timestamp: Long = log.timestamp
    }
}