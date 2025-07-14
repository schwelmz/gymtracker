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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.WorkoutSession
import com.example.gymtracker.ui.components.WorkoutCalendar
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.ui.utils.NavigationType
import com.example.gymtracker.ui.utils.rememberNavigationType
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

val headlineTopPadding = 64.dp
val headlineBottomPadding = 32.dp

@Composable
fun RecentWorkoutsView(
    sessions: List<WorkoutSession>,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit,
    onModifySession: (WorkoutSession) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn (
    ){
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding,
                        end = 16.dp
                    ),
                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
            ) {
                Text(
                    text = "Recent Workouts",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        if (sessions.isEmpty()) {
            item {
                Text(
                    "No workouts recorded yet."
                )
            }
        } else {
            val sessionDates = sessions.map { it.date }.distinct().sortedByDescending { it.time }
            items(items = sessionDates) { sessionDate ->
                Text(
                    text = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    ).format(sessionDate),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val filteredSessions = sessions.filter { it.date == sessionDate }
                    items(filteredSessions.take(10)) { session ->
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
        }
    }
}

@Composable
fun WorkoutCalendarView(
    workoutDates: Set<LocalDate>,
    onNavigateToWorkoutCalendarDay: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding,
                        end = 16.dp
                    ),
                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
            ) {
                Text(
                    text = "Calendar",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        item {
            Card (
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.height(100.dp).fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Text(
                    "You have a ... day Streak!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )

            }
        }
        item {
            WorkoutCalendar(
                workoutDates = workoutDates,
                onDayClicked = { date -> onNavigateToWorkoutCalendarDay(date) }
            )
        }
    }
}

@Composable
fun AllExercisesView(
    exercises: List<Exercise>,
    onAddExerciseClicked: () -> Unit,
    onExerciseClicked: (String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding,
                        end = 16.dp
                    ),
                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
            ) {
                Text(
                    text = "All Exercises",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        item {
            Card(
                onClick = onAddExerciseClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add custom exercise",
                        style = MaterialTheme.typography.titleMedium
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
            } else if (exercise.imageResId != null) {
                Image(
                    painter = painterResource(id = exercise.imageResId),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            } else {
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
