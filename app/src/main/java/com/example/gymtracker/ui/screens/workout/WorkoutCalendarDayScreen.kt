package com.example.gymtracker.ui.screens.workout

import WorkoutSessionCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.WorkoutSession
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutCalendarDayScreen(
    day: String?,
    sessions: List<WorkoutSession>,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit,
    onModifySession: (WorkoutSession) -> Unit
) {
    // Using derivedStateOf for efficient recomposition when day or sessions change
    val selectedDate by remember(day) {
        derivedStateOf { day?.let { LocalDate.parse(it) } }
    }

    // Filter sessions based on the selected date efficiently
    val daySessions by remember(selectedDate, sessions) {
        derivedStateOf {
            selectedDate?.let {
                sessions.filter { session ->
                    session.date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() == it
                } ?: emptyList()
            } ?: emptyList()
        }
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        item {
            val formattedDate = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM dd")) ?: "Unknown Date"
            Text(
                text = "Workouts on $formattedDate",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
            )
        }

        // Show sessions if available, or a message if no sessions exist for the selected day
        if (daySessions.isNotEmpty()) {
            items(daySessions) { session ->
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
        } else {
            item {
                Text(
                    text = "No workouts recorded for this day.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun WorkoutCalendarDayScreenPreview() {
    val fakeSessions = listOf(
        WorkoutSession(1, "Bench Press", emptyList(), java.util.Date()),
        WorkoutSession(2, "Deadlift", emptyList(), java.util.Date()),
        WorkoutSession(3, "Squat", emptyList(), java.util.Date())
    )
    WorkoutCalendarDayScreen(
        day = LocalDate.now().toString(),
        sessions = fakeSessions,
        onSessionClicked = {},
        onDeleteSession = {},
        onModifySession = {}
    )
}
