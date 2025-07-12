package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.Food

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val foodDao = AppDatabase.getDatabase(application).foodDao()

    val allFood = foodDao.getAllFood()

    suspend fun insertFood(food: Food) {
        foodDao.insertFood(food)
    }

    suspend fun deleteFood(food: Food) {
        foodDao.deleteFood(food)
    }

    suspend fun insertAll(foods: List<Food>) {
        foodDao.insertAll(foods)
    }
}