package com.example.gymtracker.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
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
    onModifySession: (WorkoutSession) -> Unit,
    onAddExerciseClicked: () -> Unit,
    onExerciseClicked: (String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit
) {
    Column (modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            item {
                Text(
                    text = "Recent Workouts", // Changed title to be more specific
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary,
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
                    color = MaterialTheme.colorScheme.secondary,
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
                Text(
                    text = "All Exercises",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(top=20.dp,bottom=20.dp)
                )
            }
            item {
                Card(
                    onClick = onAddExerciseClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight) // Ensures the card has enough height for centering
                        .padding(vertical = 8.dp) // Outer padding for the card in the list
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()    // Box fills the card
                            .padding(16.dp),  // Inner padding within the card, content centered inside this
                        contentAlignment = Alignment.Center // THIS IS KEY FOR HORIZONTAL & VERTICAL CENTERING
                    ) {
                        Text(
                            text = "Add custom exercise", // Your desired text
                            style = MaterialTheme.typography.titleMedium
                            // textAlign = TextAlign.Center is not strictly needed here for horizontal
                            // centering because the Box handles it, but won't hurt.
                        )
                    }
                }
            }

            items(items = exercises) { exercise ->
                ExerciseCard(
                    exercise,
                    onClick = { onExerciseClicked(exercise.name) },
                    onDelete = { onDeleteExercise(exercise) }
                )
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
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.exerciseName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

val cardHeight = 95.dp
@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Exercise") },
            text = { Text("Are you sure you want to delete this exercise?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(cardHeight)
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
        Row {
            val imageWidth = 95.dp
            if (!exercise.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = exercise.imageUri, // Coil handles the loading
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            }
            else if (exercise.imageResId != null) {
                Image(
                    painter = painterResource(id = exercise.imageResId),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
            onModifySession = {},
            onAddExerciseClicked = {},
            onExerciseClicked = {},
            onDeleteExercise = {}
        )
    }
}