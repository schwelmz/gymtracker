package com.example.gymtracker.viewmodel

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.model.ExerciseSet
import com.example.gymtracker.data.model.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import kotlin.collections.toSet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    private val _workoutSessionEvents = MutableSharedFlow<Unit>()
    val workoutSessionEvents: SharedFlow<Unit> = _workoutSessionEvents

    // Live data for all sessions for the home screen
    val allSessions = workoutDao.getAllSessions()

    // State for the current logging session
    private val _currentSets = MutableStateFlow<List<ExerciseSet>>(emptyList())
    val currentSets = _currentSets.asStateFlow()

    // NEW STATE FOR THE CALENDAR
    val workoutDates: StateFlow<Set<LocalDate>> = workoutDao.getAllWorkoutDates()
        .map { dates ->
            // Convert the List<java.util.Date> to a Set<java.time.LocalDate>
            dates.map { date ->
                Instant.ofEpochMilli(date.time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.toSet()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun getSessionById(sessionId: Int): StateFlow<WorkoutSession> {
        return workoutDao.getSessionById(sessionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = WorkoutSession(id = -1, exerciseName = "", sets = emptyList(), date = Date())
            )
    }

    fun addSet(reps: Int, weight: Double) {
        val newSet = ExerciseSet(reps, weight)
        _currentSets.value = _currentSets.value + newSet
    }

    fun saveWorkoutSession(exerciseName: String, planId: Int? = null) {
        if (_currentSets.value.isNotEmpty()) {
            viewModelScope.launch {
                val newSession = WorkoutSession(
                    exerciseName = exerciseName,
                    sets = _currentSets.value,
                    date = Date().normalized(),
                    planId = planId
                )
                workoutDao.insertSession(newSession)
                _workoutSessionEvents.emit(Unit)
                // Reset for the next workout
                _currentSets.value = emptyList()
            }
        }
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            workoutDao.deleteSession(session)
            _workoutSessionEvents.emit(Unit)
        }
    }
    fun getSessionsForChart(exerciseName: String) = workoutDao.getSessionsForExercise(exerciseName)

    fun updateSession(session: WorkoutSession) {
        viewModelScope.launch {
            workoutDao.updateSession(session)
            _workoutSessionEvents.emit(Unit)
        }
    }

    fun resetCurrentSets() {
        _currentSets.value = emptyList()
    }
}

fun Date.normalized(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
