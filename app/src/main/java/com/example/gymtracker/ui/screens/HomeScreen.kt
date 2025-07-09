package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(onNavigateToAddWorkout: () -> Unit) {
    // We use a LazyColumn for an efficient, scrollable list.
    // In Phase 3, this list is just a placeholder.
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        // Header
        item {
            Text(
                text = "Recent Workouts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Placeholder for the list.
        // If the list is empty, show a message.
        // For now, we show a static example.
        item {
            WorkoutSessionCard(
                exerciseName = "Bench Press",
                date = Date(),
                details = "3 sets, 8-10 reps"
            )
        }
        item {
            WorkoutSessionCard(
                exerciseName = "Squat",
                date = Date(),
                details = "5 sets, 5 reps"
            )
        }

        // In a real app with a ViewModel, you would check if the list is empty
        // and show a message like this:
        // if (sessions.isEmpty()) {
        //    item {
        //        Text("No workouts recorded yet. Tap the '+' button to start!")
        //    }
        // }
    }
}

@Composable
fun HomeScreen(
    onNavigateToAddWorkout: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val sessions by viewModel.allSessions.collectAsState(initial = emptyList())
    // ...
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        // Header
        item {
            Text(
                text = "Recent Workouts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(sessions) { session ->
            // Calculate details string
            val totalSets = session.sets.size
            val totalReps = session.sets.sumOf { it.reps }
            val details = "$totalSets sets, Total Reps: $totalReps"
            WorkoutSessionCard(
                exerciseName = session.exerciseName,
                date = session.date,
                details = details
            )
        }
        //In a real app with a ViewModel, you would check if the list is empty and show a message like this:
        if (sessions.isEmpty()) {
            item {
                Text("No workouts recorded yet. Tap the '+' button to start!")
            }
        }
    }
}

@Composable
fun WorkoutSessionCard(exerciseName: String, date: Date, details: String) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exerciseName, style = MaterialTheme.typography.titleMedium)
            Text(text = dateFormat.format(date), style = MaterialTheme.typography.bodySmall)
            Text(text = details, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
    }
}