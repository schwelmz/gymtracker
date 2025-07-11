package com.example.gymtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.ExerciseRepository

@Composable
fun AddWorkoutScreen(
    onExerciseSelected: (String) -> Unit,
    exercises: List<Exercise>
) {

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Choose an Exercise", style = MaterialTheme.typography.titleLarge)
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(exercises) { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onExerciseSelected(exercise.name) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = exercise.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddWorkoutScreenPreview() {
    AppTheme {
        AddWorkoutScreen(
            onExerciseSelected = {},
            exercises = ExerciseRepository.getAvailableExercises())
    }
}