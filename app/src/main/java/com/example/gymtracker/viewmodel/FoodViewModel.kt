package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.CustomFood
import com.example.gymtracker.data.Food
import com.example.gymtracker.data.PredefinedFoodRepository // Import the repository
import com.example.gymtracker.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val foodDao = AppDatabase.getDatabase(application).foodDao()
    private val customFoodDao = AppDatabase.getDatabase(application).customFoodDao()
    val todayFood: Flow<List<Food>>
    val allFoodHistory: Flow<List<Food>> = foodDao.getAllFood()
    val allCustomFoods: Flow<List<CustomFood>> = customFoodDao.getAll()

    init {
        // --- ADDED CODE: Pre-populates the database on first launch ---
        viewModelScope.launch {
            // Check if the custom_food table is empty
            if (customFoodDao.count() == 0) {
                // If it's empty, get the predefined list and insert it
                val predefinedFoods = PredefinedFoodRepository.getPredefinedFoods()
                customFoodDao.insertAll(predefinedFoods)
            }
        }
        // --- END OF ADDED CODE ---

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val startOfDay = today.timeInMillis

        today.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = today.timeInMillis

        todayFood = foodDao.getFoodForDay(startOfDay, endOfDay)
    }

    fun addFood(product: Product, grams: Int) {
        viewModelScope.launch {
            val nutriments = product.nutriments
            if (nutriments != null) {
                val caloriesPer100g = nutriments.energyKcalPer100g ?: 0.0
                val carbsPer100g = nutriments.carbohydratesPer100g ?: 0.0
                val proteinPer100g = nutriments.proteinsPer100g ?: 0.0
                val fatPer100g = nutriments.fatPer100g ?: 0.0

                val food = Food(
                    barcode = product.code ?: "",
                    name = product.name ?: "Unknown",
                    calories = (caloriesPer100g * grams / 100.0).toInt(),
                    carbs = (carbsPer100g * grams / 100.0).toInt(),
                    protein = (proteinPer100g * grams / 100.0).toInt(),
                    fat = (fatPer100g * grams / 100.0).toInt(),
                    grams = grams,
                    timestamp = System.currentTimeMillis(),
                    imageUri = product.imageUrl
                )
                foodDao.insertFood(food)
            }
        }
    }

    /**
     * Creates a new Food log entry from a CustomFood template and a given weight.
     */
    fun addCustomFoodEntry(customFood: CustomFood, grams: Int) {
        viewModelScope.launch {
            val food = Food(
                barcode = "custom_${customFood.id}", // Create a unique identifier
                name = customFood.name,
                calories = (customFood.caloriesPer100g * grams / 100.0).toInt(),
                carbs = (customFood.carbsPer100g * grams / 100.0).toInt(),
                protein = (customFood.proteinPer100g * grams / 100.0).toInt(),
                fat = (customFood.fatPer100g * grams / 100.0).toInt(),
                grams = grams,
                timestamp = System.currentTimeMillis()
                // No image for custom foods, so imageUri remains null
            )
            foodDao.insertFood(food)
        }
    }

    /**
     * Saves a new CustomFood template to the database.
     */
    fun saveCustomFoodTemplate(name: String, calories: Int, protein: Int, carbs: Int, fat: Int) {
        viewModelScope.launch {
            val newCustomFood = CustomFood(
                name = name,
                caloriesPer100g = calories,
                proteinPer100g = protein,
                carbsPer100g = carbs,
                fatPer100g = fat
            )
            customFoodDao.insert(newCustomFood)
        }
    }

    suspend fun deleteCustomFoodTemplate(customFood: CustomFood){
        customFoodDao.delete(customFood)
    }

    suspend fun deleteFood(food: Food) {
        foodDao.deleteFood(food)
    }
    // --- ADD THIS COMPANION OBJECT ---
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
                    // Get the Application object from extras
                    val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    // Create and return an instance of FoodViewModel
                    return FoodViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}