package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipeId", "templateId"], // Composite primary key
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE // If recipe is deleted, its ingredients are deleted
        ),
        ForeignKey(
            entity = FoodTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE // If food template is deleted, this ingredient link is removed
        )
    ]
)
data class RecipeIngredient(
    val recipeId: Int,
    val templateId: Int,
    val grams: Int
)