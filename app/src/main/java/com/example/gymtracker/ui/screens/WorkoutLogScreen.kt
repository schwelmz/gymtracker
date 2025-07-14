package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.example.gymtracker.data.ExerciseSet
import com.example.gymtracker.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun WorkoutLogScreen(
    exerciseName: String,
    sets: List<ExerciseSet>,
    onAddSet: (reps: Int, weight: Double) -> Unit,
    onWorkoutSaved: () -> Unit,
) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val imeBottom = WindowInsets.ime
    val navBars = WindowInsets.navigationBars
    val combinedInsets = imeBottom.exclude(navBars) // removes nav bar padding from IME height
    val bottomPadding = with(LocalDensity.current) { combinedInsets.getBottom(this).toDp() }

    // Calculate the bottom padding based on keyboard (IME) height
    val imeHeightDp = with(LocalDensity.current) {
        WindowInsets.ime.getBottom(this).toDp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = exerciseName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (sets.isNotEmpty()) {
                item {
                    Text(
                        text = "Logged Sets",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            itemsIndexed(sets) { index, set ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "Set ${index + 1}: ${set.reps} reps @ ${set.weight} kg")
                    HorizontalDivider()
                }
            }
        }

        Text(
            text = "Add New Set",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
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

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val repCount = reps.toIntOrNull()
                val weightValue = weight.toDoubleOrNull()
                if (repCount != null && weightValue != null) {
                    onAddSet(repCount, weightValue)
                    reps = ""
                    weight = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Set")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onWorkoutSaved,
            enabled = sets.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish & Save Workout")
        }
    }
}
