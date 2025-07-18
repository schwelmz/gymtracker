package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.model.FoodTemplate
import com.example.gymtracker.data.model.Recipe
import com.example.gymtracker.data.model.RecipeIngredient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.forEach

class AddEditRecipeViewModel(
    application: Application,
    private val recipeId: Int
) : ViewModel() {

    private val recipeDao = AppDatabase.getDatabase(application).recipeDao()
    private val foodTemplateDao = AppDatabase.getDatabase(application).foodTemplateDao()

    var recipeName by mutableStateOf("")
    var recipeInstructions by mutableStateOf("")
    var recipeImageUri by mutableStateOf<String?>(null)
    var recipeIngredients by mutableStateOf<Map<FoodTemplate, Float>>(emptyMap())
        private set

    init {
        if (recipeId != -1) {
            viewModelScope.launch {
                val recipeWithIngredients = recipeDao.getRecipeWithIngredients(recipeId).first()
                if (recipeWithIngredients != null) {
                    recipeName = recipeWithIngredients.recipe.name
                    recipeInstructions = recipeWithIngredients.recipe.instructions ?: ""
                    recipeImageUri = recipeWithIngredients.recipe.imageUrl

                    val ingredientsMap = mutableMapOf<FoodTemplate, Float>()
                    recipeWithIngredients.ingredients.forEach { ingredient ->
                        val template = foodTemplateDao.getById(ingredient.templateId)
                        if (template != null) {
                            ingredientsMap[template] = ingredient.grams.toFloat()
                        }
                    }
                    recipeIngredients = ingredientsMap
                }
            }
        }
    }

    fun addIngredient(template: FoodTemplate, grams: Float) {
        recipeIngredients = recipeIngredients + (template to grams)
    }

    fun removeIngredient(template: FoodTemplate) {
        recipeIngredients = recipeIngredients - template
    }

    fun saveRecipe() {
        viewModelScope.launch {
            val recipe = Recipe(id = if (recipeId == -1) 0 else recipeId, name = recipeName, instructions = recipeInstructions, imageUrl = recipeImageUri)
            val newRecipeId = recipeDao.insertRecipe(recipe).toInt()

            // Clear old ingredients before inserting new ones to handle updates correctly
            if (recipeId != -1) {
                recipeDao.deleteIngredientsByRecipeId(recipeId)
            }

            val ingredientsToInsert = recipeIngredients.map { (template, grams) ->
                RecipeIngredient(recipeId = newRecipeId, templateId = template.id, grams = grams)
            }
            recipeDao.insertIngredients(ingredientsToInsert)
        }
    }

    companion object {
        fun Factory(recipeId: Int): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return AddEditRecipeViewModel(application, recipeId) as T
            }
        }
    }
}
