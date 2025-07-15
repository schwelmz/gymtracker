package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.*
import com.example.gymtracker.data.model.FoodTemplate
import com.example.gymtracker.data.model.IngredientDetails
import com.example.gymtracker.data.model.Recipe
import com.example.gymtracker.data.model.RecipeIngredient
import com.example.gymtracker.data.model.RecipeWithDetails
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()
    private val foodTemplateDao = AppDatabase.getDatabase(application).foodTemplateDao()

    val allFoodTemplates: Flow<List<FoodTemplate>> = foodTemplateDao.getAll()

    // --- THIS IS THE NEW, CORRECTED LOGIC ---
    val allRecipes: Flow<List<RecipeWithDetails>> = recipeDao.getRecipesWithIngredients()
        .combine(allFoodTemplates) { recipes, templates ->
            // Create a quick lookup map for templates by their ID
            val templateMap = templates.associateBy { it.id }

            recipes.map { recipeWithIngredients ->
                val ingredientDetails = recipeWithIngredients.ingredients.mapNotNull { ingredient ->
                    templateMap[ingredient.templateId]?.let { template ->
                        IngredientDetails(foodTemplate = template, grams = ingredient.grams)
                    }
                }
                RecipeWithDetails(
                    recipe = recipeWithIngredients.recipe,
                    ingredients = ingredientDetails
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addOrUpdateRecipe(
        name: String,
        instructions: String?,
        ingredients: Map<FoodTemplate, Int>, // Map of Template to Grams
        existingRecipe: RecipeWithDetails?
    ) {
        viewModelScope.launch {
            val recipe = existingRecipe?.recipe?.copy(
                name = name,
                instructions = instructions
            ) ?: Recipe(name = name, instructions = instructions)

            val recipeId = recipeDao.insertRecipe(recipe).toInt()

            // If updating, clear old ingredients first
            if (existingRecipe != null) {
                recipeDao.deleteIngredientsByRecipeId(existingRecipe.recipe.id)
            }

            val recipeIngredients = ingredients.map { (template, grams) ->
                RecipeIngredient(recipeId = recipeId, templateId = template.id, grams = grams)
            }
            recipeDao.insertIngredients(recipeIngredients)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.deleteRecipe(recipe)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return RecipeViewModel(application) as T
            }
        }
    }
}