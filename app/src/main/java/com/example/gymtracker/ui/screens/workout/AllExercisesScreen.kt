package com.example.gymtracker.ui.screens.workout

import ExerciseCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.Exercise
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import cardHeight

@Composable
fun AllExercisesView(
    exercises: List<Exercise>,
    onAddExerciseClicked: () -> Unit,
    onExerciseClicked: (String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Item
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headlineTopPadding, bottom = headlineBottomPadding, end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "All Exercises",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Add custom exercise button
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

        // Exercises list
        items(items = exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onClick = { onExerciseClicked(exercise.name) },
                onDelete = { onDeleteExercise(exercise) }
            )
        }
    }
}
