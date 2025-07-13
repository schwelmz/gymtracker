package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.Food
import com.example.gymtracker.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val foodDao = AppDatabase.getDatabase(application).foodDao()

    val todayFood: Flow<List<Food>>
    val allFoodHistory: Flow<List<Food>> = foodDao.getAllFood()

    init {
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

    suspend fun deleteFood(food: Food) {
        foodDao.deleteFood(food)
    }
}