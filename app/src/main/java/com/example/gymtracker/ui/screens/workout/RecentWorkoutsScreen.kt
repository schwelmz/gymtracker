package com.example.gymtracker.ui.screens.workout

import WorkoutSessionCard
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.WorkoutSession
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import java.text.SimpleDateFormat
import java.util.Locale

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