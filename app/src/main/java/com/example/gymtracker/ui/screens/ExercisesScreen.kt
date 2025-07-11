package com.example.gymtracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.ExerciseRepository

val cardHeight = 95.dp
@Composable
fun ExercisesScreen(
    //viewModel: ExerciseViewModel = viewModel(),
    exercises: List<Exercise>,
    onAddExerciseClicked: () -> Unit = {},
    onExerciseClicked: (String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit = {}
) {
    // Collect the list of exercises as a state. The UI will recompose when it changes.
    //val exercises by viewModel.allExercises.collectAsState(initial = emptyList())

    Column (modifier = Modifier.fillMaxSize()) {
        LazyColumn (modifier = Modifier.padding(16.dp)) {
            item {
                Text(
                    text = "All Exercises",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(top=50.dp,bottom=50.dp)
                )
            }
            item {
                Card(
                    onClick = onAddExerciseClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight) // Ensures the card has enough height for centering
                        .padding(vertical = 8.dp) // Outer padding for the card in the list
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()    // Box fills the card
                            .padding(16.dp),  // Inner padding within the card, content centered inside this
                        contentAlignment = Alignment.Center // THIS IS KEY FOR HORIZONTAL & VERTICAL CENTERING
                    ) {
                        Text(
                            text = "Add custom exercise", // Your desired text
                            style = MaterialTheme.typography.titleMedium
                            // textAlign = TextAlign.Center is not strictly needed here for horizontal
                            // centering because the Box handles it, but won't hurt.
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
}

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
            if (!exercise.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = exercise.imageUri, // Coil handles the loading
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(100.dp)
                        .height(cardHeight)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(100.dp)
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

@Preview(showBackground = true)
@Composable
fun ExerciseScreenPreview() {
    AppTheme {
        ExercisesScreen(
            ExerciseRepository.getAvailableExercises(),
            onExerciseClicked = {},
            onDeleteExercise = {}
        )
    }
}