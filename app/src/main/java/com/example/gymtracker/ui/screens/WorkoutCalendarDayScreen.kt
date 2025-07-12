package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gymtracker.data.WorkoutSession

@Composable
fun WorkoutCalendarDayScreen(
    day: String?,
    sessions: List<WorkoutSession>,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(text = "Workouts on $day")
        }
//        val daySessions = sessions.filter { it.date == day }
//        items(items = daySessions) { session ->
//            val totalSets = session.sets.size
//            val totalReps = session.sets.sumOf { it.reps }
//            val details = "$totalSets sets, Total Reps: $totalReps"
//
//            WorkoutSessionCard(
//                session = session,
//                details = details,
//                onClick = { onSessionClicked(session.exerciseName) },
//                onDelete = { onDeleteSession(session) }
//            )
//        }
//        if (sessions.isEmpty()) {
//            item {
//                Text(
//                    "No workouts recorded yet. Tap the '+' button to start!",
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//        }
    }
}