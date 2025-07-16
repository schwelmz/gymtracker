package com.example.gymtracker.ui.screens.workout

import WorkoutSessionCard
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel

@Composable
fun WorkoutCalendarDayScreen(
    day: String?,
    sessions: List<WorkoutSession>,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit,
    onModifySession: (WorkoutSession) -> Unit,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory)
) {
    val selectedDate by remember(day) {
        derivedStateOf {
            day?.let { LocalDate.parse(it) }
        }
    }

    val daySessions by remember(selectedDate, sessions) {
        derivedStateOf {
            sessions.filter { session ->
                session.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate
            }
        }
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        item {
            val formattedDate = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM dd")) ?: "Unknown Date"
            Text(text = "Workouts on $formattedDate",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
            )
        }
        if (daySessions.isNotEmpty()) {
            items(items = daySessions) { session ->
                val totalSets = session.sets.size
                val totalReps = session.sets.sumOf { it.reps }
                val details = "$totalSets sets, Total Reps: $totalReps"

                WorkoutSessionCard(
                    session = session,
                    details = details,
                    onClick = { onSessionClicked(session.exerciseName) },
                    onDelete = { 
                        onDeleteSession(session)
                        workoutPlanViewModel.refresh()
                    },
                    onModify = { 
                        onModifySession(session)
                        workoutPlanViewModel.refresh()
                    }
                )
            }
        } else {
            item {
                Text(
                    "No workouts recorded for this day.",
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
        WorkoutSession(1, "Bench Press", emptyList(), Date()),
        WorkoutSession(1, "Deadlift", emptyList(), Date()),
        WorkoutSession(1, "Squat", emptyList(), Date())
)
    WorkoutCalendarDayScreen(
        day = LocalDate.now().toString(),
        sessions = fakeSessions,
        onSessionClicked = {},
        onDeleteSession = {},
        onModifySession = {}
    )

}