package com.example.gymtracker.ui.screens.workout

import ExerciseCard
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cardHeight
import com.example.gymtracker.data.model.Exercise
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding

@Composable
fun AllExercisesView(
    exercises: List<Exercise>,
    onAddExerciseClicked: () -> Unit,
    onExerciseClicked: (String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
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