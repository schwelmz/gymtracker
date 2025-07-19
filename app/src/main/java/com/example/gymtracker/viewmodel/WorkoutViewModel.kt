package com.example.gymtracker.viewmodel

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.model.ExerciseSet
import com.example.gymtracker.data.model.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Date
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    private val _workoutSessionEvents = MutableSharedFlow<Unit>()
    val workoutSessionEvents: SharedFlow<Unit> = _workoutSessionEvents

    // Live data for all sessions for the home screen
    val allSessions = workoutDao.getAllSessions()

    // State for the current logging session
    private val _currentSets = MutableStateFlow<List<ExerciseSet>>(emptyList())
    val currentSets = _currentSets.asStateFlow()

    private val _lastReps = MutableStateFlow<Int?>(null)
    val lastReps = _lastReps.asStateFlow()

    private val _lastWeight = MutableStateFlow<Double?>(null)
    val lastWeight = _lastWeight.asStateFlow()

    // Load the last session for an exercise asynchronously using the IO dispatcher
    fun loadLastSession(exerciseName: String) {
        viewModelScope.launch {
            // Run database queries on background thread
            withContext(Dispatchers.IO) {
                workoutDao.getLastSessionForExercise(exerciseName).collect { session ->
                    if (session != null && session.sets.isNotEmpty()) {
                        _lastReps.value = session.sets.last().reps
                        _lastWeight.value = session.sets.last().weight
                    } else {
                        _lastReps.value = 10 // Default value
                        _lastWeight.value = 20.0 // Default value
                    }
                }
            }
        }
    }

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

    // Using stateIn() to observe session by ID, running on background thread when necessary
    fun getSessionById(sessionId: Int): StateFlow<WorkoutSession> {
        return workoutDao.getSessionById(sessionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = WorkoutSession(id = -1, exerciseName = "", sets = emptyList(), date = Date())
            )
    }

    // Adding a new set and updating the last reps and weight values
    fun addSet(reps: Int, weight: Double) {
        val newSet = ExerciseSet(reps, weight)
        _currentSets.value = _currentSets.value + newSet
        _lastReps.value = reps
        _lastWeight.value = weight
    }

    // Saving workout session in the background using IO dispatcher
    fun saveWorkoutSession(exerciseName: String, planId: Int? = null) {
        if (_currentSets.value.isNotEmpty()) {
            viewModelScope.launch {
                // Run database insert on background thread (IO)
                withContext(Dispatchers.IO) {
                    val newSession = WorkoutSession(
                        exerciseName = exerciseName,
                        sets = _currentSets.value,
                        date = Date().normalized(),
                        planId = planId
                    )
                    workoutDao.insertSession(newSession)
                    _workoutSessionEvents.emit(Unit)
                }
                // Reset for the next workout after DB operations are done
                _currentSets.value = emptyList()
            }
        }
    }

    // Deleting a workout session asynchronously in the background
    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            // Use IO dispatcher to handle the delete operation off the main thread
            withContext(Dispatchers.IO) {
                workoutDao.deleteSession(session)
                _workoutSessionEvents.emit(Unit)
            }
        }
    }

    // Retrieve sessions for chart view in background (use IO dispatcher)
    fun getSessionsForChart(exerciseName: String) = workoutDao.getSessionsForExercise(exerciseName)

    // Update a session in the background
    fun updateSession(session: WorkoutSession) {
        viewModelScope.launch {
            // Execute the database update in the background
            withContext(Dispatchers.IO) {
                workoutDao.updateSession(session)
                _workoutSessionEvents.emit(Unit)
            }
        }
    }

    // Reset the current sets for the next workout
    fun resetCurrentSets() {
        _currentSets.value = emptyList()
    }

    // Reset last session data for reps and weight
    fun resetLastSession() {
        _lastReps.value = null
        _lastWeight.value = null
    }
}

// Normalize the date to midnight (removes time part for comparison purposes)
fun Date.normalized(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
