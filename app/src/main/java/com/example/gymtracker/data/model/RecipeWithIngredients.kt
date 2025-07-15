package com.example.gymtracker.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A simple data class fetched directly by Room.
 * It holds one Recipe and its list of corresponding join table entries.
 * This is an intermediate object used by the ViewModel.
 */
data class RecipeWithIngredients(
    @Embedded
    val recipe: Recipe,

    @Relation(
        parentColumn = "id",       // Primary key from the Recipe table
        entityColumn = "recipeId"  // Foreign key from the RecipeIngredient table
    )
    val ingredients: List<RecipeIngredient>
)


/**
 * A UI-ready data class that the ViewModel will construct.
 * This is the class your UI composables will actually use.
 */
data class RecipeWithDetails(
    val recipe: Recipe,
    val ingredients: List<IngredientDetails>
)

data class IngredientDetails(
    val foodTemplate: FoodTemplate,
    val grams: Int
)