package com.example.gymtracker.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.ExerciseRepository
import com.example.gymtracker.data.WorkoutSession
import com.example.gymtracker.ui.components.WorkoutCalendar
import com.example.gymtracker.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Composable
fun WorkoutScreen(
    sessions: List<WorkoutSession>,
    exercises: List<Exercise>,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToWorkoutCalendarDay: (LocalDate) -> Unit,
    workoutDates: Set<LocalDate>,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit,
    onModifySession: (WorkoutSession) -> Unit
) {
    Column (modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            item {
                Text(
                    text = "Recent Workouts", // Changed title to be more specific
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
                )
            }
            // Group sessions by date and display them chronologically
            val sessionDates = sessions.map { it.date }.distinct().sortedByDescending { it.time }
            items(items = sessionDates.take(2)) { sessionDate ->
                Text(
                    text = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    ).format(sessionDate),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom=16.dp)
                )

                LazyRow (modifier = Modifier.padding(bottom=16.dp)) {
                    val filteredSessions = sessions
                        .filter { it.date == sessionDate }
                    items(filteredSessions) { session ->
                        val totalSets = session.sets.size
                        val totalReps = session.sets.sumOf { it.reps }
                        val details = "$totalSets sets, Total Reps: $totalReps"

                        WorkoutSessionCard(
                            session = session,
                            details = details,
                            onClick = { onSessionClicked(session.exerciseName) },
                            onDelete = { onDeleteSession(session) },
                            onModify = { onModifySession(session) }
                        )
                    }
                }
            }
            if (sessions.isEmpty()) {
                item {
                    Text(
                        "No workouts recorded yet. Tap the '+' button to start!",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            item {
                Text(
                    text = "Workout Calendar", // Changed title to be more specific
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )
            }

            item {
                WorkoutCalendar(
                    workoutDates = workoutDates,
                    modifier = Modifier.padding(top = 16.dp),
                    onDayClicked = { date ->
                        onNavigateToWorkoutCalendarDay(date)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun WorkoutSessionCard(
    session: WorkoutSession,
    details: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onModify: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Workout Options") },
            text = { Text("What would you like to do with this workout?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    onModify()
                }) {
                    Text("Modify Date")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDialog = true
                    }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.exerciseName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutScreenPreview() { // Renamed from HomeScreenPreview
    val fakeDates = setOf(LocalDate.of(2025, 7, 13), LocalDate.of(2025, 7, 8), LocalDate.of(2025, 7, 12), LocalDate.of(2025, 7, 23))
    val fakeSessions = listOf(
        WorkoutSession(1, "Bench Press", emptyList(), Date(125, 7, 13)),
        WorkoutSession(4, "Bench Press", emptyList(), Date(125, 7, 8)),
        WorkoutSession(4, "Bench Press", emptyList(), Date(125, 7, 12)),
        WorkoutSession(1, "Deadlift", emptyList(), Date(125, 7, 13)),
        WorkoutSession(1, "Squat", emptyList(), Date(125, 7, 13)),
        WorkoutSession(4, "Overhead Press", emptyList(), Date(125, 7, 8)),
        WorkoutSession(4, "Squat", emptyList(), Date(125, 7, 12)),
        WorkoutSession(2, "Squat", emptyList(), Date(125, 7, 12)),
        WorkoutSession(2, "Squat", emptyList(), Date(125, 7, 12)),
    )
    AppTheme {
        // Call the new WorkoutScreen in the preview
        WorkoutScreen(
            sessions = fakeSessions,
            exercises = ExerciseRepository.getAvailableExercises(),
            workoutDates = fakeDates,
            onNavigateToAddWorkout = {},
            onNavigateToWorkoutCalendarDay = {},
            onSessionClicked = {},
            onDeleteSession = {},
            onModifySession = {}
        )
    }
}