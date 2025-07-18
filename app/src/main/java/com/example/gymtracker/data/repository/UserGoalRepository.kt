package com.example.gymtracker.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gymtracker.data.model.CalorieMode
import com.example.gymtracker.data.model.UserGoals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Top-level extension property to create the DataStore instance
private val Context.userGoalsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_goals")

class UserGoalsRepository(private val context: Context) {

    private object Keys {
        val CALORIE_GOAL = intPreferencesKey("calorie_goal")
        val PROTEIN_GOAL = intPreferencesKey("protein_goal")
        val CARB_GOAL = intPreferencesKey("carb_goal")
        val FAT_GOAL = intPreferencesKey("fat_goal")
        val STEPS_GOAL = intPreferencesKey("steps_goal")
        val CALORIE_MODE = stringPreferencesKey("calorie_mode")
    }

    // This flow will emit the UserGoals whenever they change
    val userGoals: Flow<UserGoals> = context.userGoalsDataStore.data.map { preferences ->
        val calorieMode = CalorieMode.valueOf(
            preferences[Keys.CALORIE_MODE] ?: CalorieMode.DEFICIT.name
        )
        UserGoals(
            calorieGoal = preferences[Keys.CALORIE_GOAL] ?: 2000, // Default values
            proteinGoal = preferences[Keys.PROTEIN_GOAL] ?: 150,
            carbGoal = preferences[Keys.CARB_GOAL] ?: 250,
            fatGoal = preferences[Keys.FAT_GOAL] ?: 60,
            stepsGoal = preferences[Keys.STEPS_GOAL] ?: 10000,
            calorieMode = calorieMode
        )
    }

    suspend fun saveUserGoals(goals: UserGoals) {
        context.userGoalsDataStore.edit { preferences ->
            preferences[Keys.CALORIE_GOAL] = goals.calorieGoal
            preferences[Keys.PROTEIN_GOAL] = goals.proteinGoal
            preferences[Keys.CARB_GOAL] = goals.carbGoal
            preferences[Keys.FAT_GOAL] = goals.fatGoal
            preferences[Keys.STEPS_GOAL] = goals.stepsGoal
            preferences[Keys.CALORIE_MODE] = goals.calorieMode.name
        }
    }
}
