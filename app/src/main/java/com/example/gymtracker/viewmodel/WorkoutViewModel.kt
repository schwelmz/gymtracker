package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.constraintlayout.helper.widget.Flow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.ExerciseSet
import com.example.gymtracker.data.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    // Live data for all sessions for the home screen
    val allSessions = workoutDao.getAllSessions()

    // State for the current logging session
    private val _currentSets = MutableStateFlow<List<ExerciseSet>>(emptyList())
    val currentSets = _currentSets.asStateFlow()

    fun addSet(reps: Int, weight: Double) {
        val newSet = ExerciseSet(reps, weight)
        _currentSets.value = _currentSets.value + newSet
    }

    fun saveWorkoutSession(exerciseName: String) {
        if (_currentSets.value.isNotEmpty()) {
            viewModelScope.launch {
                val newSession = WorkoutSession(
                    exerciseName = exerciseName,
                    sets = _currentSets.value,
                    date = Date()
                )
                workoutDao.insertSession(newSession)
                // Reset for the next workout
                _currentSets.value = emptyList()
            }
        }
    }
    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            workoutDao.deleteSession(session)
        }
    }
    fun getSessionsForChart(exerciseName: String) = workoutDao.getSessionsForExercise(exerciseName)
}