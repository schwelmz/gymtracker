package com.example.gymtracker.data.dao

import androidx.room.*
import com.example.gymtracker.data.model.Recipe
import com.example.gymtracker.data.model.RecipeIngredient
import com.example.gymtracker.data.model.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<RecipeIngredient>)

    /**
     * This query now fetches the simple relationship that Room can easily handle.
     * The ViewModel will be responsible for fetching the FoodTemplate details.
     */
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredients(recipeId: Int): Flow<RecipeWithIngredients?>

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Int)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}