package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.*
import com.example.gymtracker.data.dao.FoodLogWithDetails
import com.example.gymtracker.data.model.DiaryEntry
import com.example.gymtracker.data.model.FoodLog
import com.example.gymtracker.data.model.FoodTemplate
import com.example.gymtracker.data.model.Product
import com.example.gymtracker.data.model.RecipeLog
import com.example.gymtracker.data.model.RecipeWithDetails
import com.example.gymtracker.data.repository.PredefinedFoodRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val templateDao = AppDatabase.getDatabase(application).foodTemplateDao()
    private val logDao = AppDatabase.getDatabase(application).foodLogDao()
    private val foodLogDao = AppDatabase.getDatabase(application).foodLogDao()
    private val gson = Gson()
    private val recipeLogDao = AppDatabase.getDatabase(application).recipeLogDao()
    val todayDiaryEntries: Flow<List<DiaryEntry>>
        get() {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0)
            val startOfDay = today.timeInMillis
            today.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = today.timeInMillis

            val foodLogs = logDao.getLogsForDayWithDetails(startOfDay, endOfDay)
            val recipeLogs = recipeLogDao.getRecipeLogsForDay(startOfDay, endOfDay)

            return combine(foodLogs, recipeLogs) { foods, recipes ->
                (foods.map { DiaryEntry.Food(it) } + recipes.map { DiaryEntry.Recipe(it) })
                    .sortedByDescending { it.timestamp }
            }
        }
    val allDiaryHistory: Flow<List<DiaryEntry>>
        get() {
            val foodLogs = logDao.getAllLogsWithDetails()
            val recipeLogs = recipeLogDao.getAllRecipeLogs()

            return combine(foodLogs, recipeLogs) { foods, recipes ->
                (foods.map { DiaryEntry.Food(it) } + recipes.map { DiaryEntry.Recipe(it) })
                    .sortedByDescending { it.timestamp }
            }
        }

    val allFoodTemplates: Flow<List<FoodTemplate>>

    init {
        // Pre-populate the database on first launch with unified templates
        viewModelScope.launch {
            if (templateDao.count() == 0) {
                val predefinedFoods = PredefinedFoodRepository.getPredefinedFoods()
                templateDao.insertAll(predefinedFoods)
            }
        }
        //allFoodHistory = logDao.getAllLogsWithDetails()
        allFoodTemplates = templateDao.getAll()
    }
    fun logRecipe(recipe: RecipeWithDetails) {
        viewModelScope.launch {
            val totalCalories = recipe.ingredients.sumOf { (it.foodTemplate.caloriesPer100g * it.grams) / 100 }
            val totalProtein = recipe.ingredients.sumOf { (it.foodTemplate.proteinPer100g * it.grams) / 100 }
            val totalCarbs = recipe.ingredients.sumOf { (it.foodTemplate.carbsPer100g * it.grams) / 100 }
            val totalFat = recipe.ingredients.sumOf { (it.foodTemplate.fatPer100g * it.grams) / 100 }

            // Create a JSON string of the ingredients for the snapshot
            val ingredientsJson = gson.toJson(recipe.ingredients)

            val recipeLog = RecipeLog(
                name = recipe.recipe.name,
                instructions = recipe.recipe.instructions,
                imageUrl = recipe.recipe.imageUrl,
                timestamp = System.currentTimeMillis(),
                totalCalories = totalCalories,
                totalProtein = totalProtein,
                totalCarbs = totalCarbs,
                totalFat = totalFat,
                ingredientsJson = ingredientsJson
            )
            recipeLogDao.insert(recipeLog)
        }
    }
    // --- REFACTORED LOGIC ---

    /**
     * Handles adding food from the barcode scanner.
     * It checks if a template for this food already exists. If not, it creates one.
     * Then, it creates a log entry for the specified weight.
     */
    fun addScannedFood(product: Product, grams: Int) {
        viewModelScope.launch {
            val barcode = product.code ?: return@launch
            var template = templateDao.getByBarcode(barcode)

            // If template doesn't exist, create it from the scanned product
            if (template == null) {
                val newTemplate = FoodTemplate(
                    barcode = barcode,
                    name = findBestName(product),
                    imageUrl = product.imageUrl,
                    caloriesPer100g = product.nutriments?.energyKcalPer100g?.toInt() ?: 0,
                    proteinPer100g = product.nutriments?.proteinsPer100g?.toInt() ?: 0,
                    carbsPer100g = product.nutriments?.carbohydratesPer100g?.toInt() ?: 0,
                    fatPer100g = product.nutriments?.fatPer100g?.toInt() ?: 0
                )
                // Insert and get the ID of the newly created template
                val newId = templateDao.insert(newTemplate).toInt()
                template = newTemplate.copy(id = newId)
            }

            // Create a log entry with the specified weight
            logFood(template, grams)
        }
    }
    fun deleteRecipeLog(logId: Int) {
        viewModelScope.launch {
            recipeLogDao.delete(logId)
        }
    }


    fun updateLogTimestamp(logId: Int, newTimestamp: Long) {
        viewModelScope.launch {
            logDao.updateTimestamp(logId, newTimestamp)
        }
    }
    suspend fun getOrCreateTemplateFromProduct(product: Product): FoodTemplate {
        val barcode = product.code ?: return FoodTemplate(
            name = "Unknown", caloriesPer100g = 0,
            id = 0,
            barcode = null,
            imageUrl = null,
            proteinPer100g = 0,
            carbsPer100g = 0,
            fatPer100g = 0
        )
        var template = templateDao.getByBarcode(barcode)
        if (template == null) {
            val newTemplate = FoodTemplate(
                barcode = barcode,
                name = findBestName(product),
                imageUrl = product.imageUrl,
                caloriesPer100g = product.nutriments?.energyKcalPer100g?.toInt() ?: 0,
                proteinPer100g = product.nutriments?.proteinsPer100g?.toInt() ?: 0,
                carbsPer100g = product.nutriments?.carbohydratesPer100g?.toInt() ?: 0,
                fatPer100g = product.nutriments?.fatPer100g?.toInt() ?: 0
            )
            val id = templateDao.insert(newTemplate).toInt()
            template = newTemplate.copy(id = id)
        }
        return template
    }
    fun addScannedFoodWithCustomName(product: Product, grams: Int, customName: String) {
        viewModelScope.launch {
            val barcode = product.code ?: return@launch
            var template = templateDao.getByBarcode(barcode)

            if (template == null) {
                val newTemplate = FoodTemplate(
                    barcode = barcode,
                    name = customName,
                    imageUrl = product.imageUrl,
                    caloriesPer100g = product.nutriments?.energyKcalPer100g?.toInt() ?: 0,
                    proteinPer100g = product.nutriments?.proteinsPer100g?.toInt() ?: 0,
                    carbsPer100g = product.nutriments?.carbohydratesPer100g?.toInt() ?: 0,
                    fatPer100g = product.nutriments?.fatPer100g?.toInt() ?: 0
                )
                val newId = templateDao.insert(newTemplate).toInt()
                template = newTemplate.copy(id = newId)
            }

            logFood(template, grams)
        }
    }
    fun updateLogGrams(logId: Int, newGrams: Int) {
        viewModelScope.launch {
            logDao.updateGrams(logId, newGrams)
        }
    }
    fun updateFoodLog(
        logId: Int,
        grams: Int,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ) {
        viewModelScope.launch {
            foodLogDao.updateFoodLogFull(logId, grams, calories, protein, carbs, fat)
        }
    }
    /**
     * Logs an entry for a predefined or custom food template.
     */
    fun logFood(template: FoodTemplate, grams: Int) {
        viewModelScope.launch {
            val log = FoodLog(
            templateId = template.id,
            grams = grams,
            timestamp = System.currentTimeMillis(),
            id = 0,
            calories = (template.caloriesPer100g * grams) / 100,
            protein = (template.proteinPer100g * grams) / 100,
            carbs = (template.carbsPer100g * grams) / 100,
            fat = (template.fatPer100g * grams) / 100
            )
            logDao.insert(log)
        }
    }

    /**
     * Saves a new user-defined food template to the database.
     */
    fun saveCustomFoodTemplate(name: String, calories: Int, protein: Int, carbs: Int, fat: Int) {
        viewModelScope.launch {
            val newTemplate = FoodTemplate(
                name = name,
                caloriesPer100g = calories,
                proteinPer100g = protein,
                carbsPer100g = carbs,
                fatPer100g = fat
            )
            templateDao.insert(newTemplate)
        }
    }

    suspend fun deleteFoodTemplate(template: FoodTemplate) {
        templateDao.delete(template)
    }

    suspend fun deleteFoodLog(logId: Int) {
        logDao.delete(logId)
    }

    private fun findBestName(product: Product): String {
        if (!product.name.isNullOrBlank()) {
            return product.name.split("–", "-").firstOrNull()?.trim() ?: product.name
        }
        if (!product.genericName.isNullOrBlank()) {
            return product.genericName
        }
        return "Unknown Product"
    }
    // In com/example/gymtracker/viewmodel/FoodViewModel.kt

    suspend fun saveScannedFood(
        product: Product,
        barcode: String,
        grams: Int,
        name: String,
        isForRecipe: Boolean,
        scannerResultViewModel: ScannerResultViewModel
    ) {
        // Use the 'barcode' parameter to check for an existing template.
        // FIX: Changed foodTemplateDao to templateDao
        val existingTemplate = templateDao.getByBarcode(barcode)

        val finalTemplate = if (existingTemplate != null) {
            // If a template exists, update its name if the user provided a new one.
            if (existingTemplate.name != name) {
                val updated = existingTemplate.copy(name = name)
                // FIX: Changed foodTemplateDao to templateDao
                templateDao.update(updated)
                updated
            } else {
                existingTemplate
            }
        } else {
            // If no template exists, create a new one with all the correct details.
            val newTemplate = FoodTemplate(
                barcode = barcode,
                name = name,
                imageUrl = product.imageUrl,
                caloriesPer100g = product.nutriments?.energyKcalPer100g?.toInt() ?: 0,
                proteinPer100g = product.nutriments?.proteinsPer100g?.toInt() ?: 0,
                carbsPer100g = product.nutriments?.carbohydratesPer100g?.toInt() ?: 0,
                fatPer100g = product.nutriments?.fatPer100g?.toInt() ?: 0
            )
            // FIX: Changed foodTemplateDao to templateDao
            val newId = templateDao.insert(newTemplate)
            newTemplate.copy(id = newId.toInt())
        }

        // Proceed to log the food or add it to the recipe.
        if (isForRecipe) {
            scannerResultViewModel.setScannedIngredient(finalTemplate, grams)
        } else {
            logFood(finalTemplate, grams)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
                    val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    return FoodViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}