package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.data.ExerciseSet

@Composable
fun WorkoutLogScreen(
    exerciseName: String,
    sets: List<ExerciseSet>,
    onAddSet: (reps: Int, weight: Double) -> Unit,
    onWorkoutSaved: () -> Unit,
) {
    // Local UI state for the input fields. These are temporary.
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = exerciseName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // This LazyColumn displays the sets that have been logged for the current session.
        // The `weight(1f)` modifier makes it take up all available vertical space,
        // pushing the input fields to the bottom.
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                if (sets.isNotEmpty()) {
                    Text(
                        text = "Logged Sets",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            itemsIndexed(sets) { index, set ->
                Text(
                    text = "Set ${index + 1}: ${set.reps} reps @ ${set.weight} kg",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input section for a new set
        Text(
            text = "Add New Set",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Reps") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val repCount = reps.toIntOrNull()
                val weightValue = weight.toDoubleOrNull()
                // Validate input before adding the set
                if (repCount != null && weightValue != null) {
                    onAddSet(repCount, weightValue)
                    // Clear fields for the next entry
                    reps = ""
                    weight = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Set")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // The final save button
        Button(
            onClick = {
                onWorkoutSaved()
            },
            // Disable the button if no sets have been added
            enabled = sets.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish & Save Workout")
        }
    }
}


// Preview for when a few sets have already been logged
@Preview(showBackground = true)
@Composable
fun WorkoutLogScreenWithDataPreview() {
    val fakeSets = listOf(
        ExerciseSet(reps = 10, weight = 100.0),
        ExerciseSet(reps = 8, weight = 105.0),
        ExerciseSet(reps = 6, weight = 110.0)
    )
    AppTheme {
        WorkoutLogScreen(
            exerciseName = "Squat",
            sets = fakeSets, // Provide the fake data
            onAddSet = { _, _ -> },
            onWorkoutSaved = {}
        )
    }
}