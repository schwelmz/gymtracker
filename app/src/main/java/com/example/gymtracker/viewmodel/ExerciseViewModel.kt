package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseDao = AppDatabase.Companion.getDatabase(application).exerciseDao()
    private val workoutDao = AppDatabase.Companion.getDatabase(application).workoutDao()
    // This Flow will automatically emit a new list whenever the database changes.
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()

    init {
        // We launch a coroutine to pre-populate the database with default exercises.
        // This will only insert them if they don't already exist, thanks to our
        // OnConflictStrategy.IGNORE in the DAO.
        viewModelScope.launch(Dispatchers.IO) {
            val defaultExercises = ExerciseRepository.getAvailableExercises()
            exerciseDao.insertAll(defaultExercises)
        }
    }

    fun addCustomExercise(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Create a new exercise marked as custom and insert it.
            val newExercise = Exercise(name = name, description = description, isCustom = true)
            exerciseDao.insertExercise(newExercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.deleteExercise(exercise)
            //delete all sessions with this exercise name
            workoutDao.deleteSessionsByExerciseName(exercise.name)
        }
    }
}