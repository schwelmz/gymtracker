package com.example.gymtracker.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.WorkoutSession
import com.example.gymtracker.ui.theme.GymTrackerTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType


@Composable
fun HomeScreen(
    sessions: List<WorkoutSession>,
    onNavigateToAddWorkout: () -> Unit,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Recent Workouts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(sessions) { session ->
            val totalSets = session.sets.size
            val totalReps = session.sets.sumOf { it.reps }
            val details = "$totalSets sets, Total Reps: $totalReps"

            WorkoutSessionCard(
                session = session,
                details = details,
                onClick = { onSessionClicked(session.exerciseName) },
                onDelete = { onDeleteSession(session) }
            )
        }

        if (sessions.isEmpty()) {
            item {
                Text("No workouts recorded yet. Tap the '+' button to start!")
            }
        }
    }
}

@Composable
fun WorkoutSessionCard(
    session: WorkoutSession,
    details: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Workout") },
            text = { Text("Are you sure you want to delete this workout?") },
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
            .padding(bottom = 8.dp)
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
            Text(text = dateFormat.format(session.date), style = MaterialTheme.typography.bodySmall)
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
fun HomeScreenPreview() {
    val fakeSessions = listOf(
        WorkoutSession(1, "Benchpress", emptyList(), Date()),
        WorkoutSession(2, "Squat", emptyList(), Date()),
        WorkoutSession(3, "Deadlift", emptyList(), Date())
    )
    GymTrackerTheme {
        HomeScreen(
            sessions = fakeSessions,
            onNavigateToAddWorkout = {},
            onSessionClicked = {},
            onDeleteSession = {}
        )
    }
}
