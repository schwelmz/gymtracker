package com.example.gymtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.ExerciseSet

@Composable
fun ExerciseSetInput(
    sets: List<ExerciseSet>,
    onAddSet: (reps: Int, weight: Double) -> Unit,
    modifier: Modifier = Modifier,
    initialReps: Int?,
    initialWeight: Double?
) {
    var reps by remember(initialReps) { mutableStateOf(initialReps?.toString() ?: "10") }
    var weight by remember(initialWeight) { mutableStateOf(initialWeight?.toString() ?: "20.0") }

    val repsList = (1..100).map { it.toString() }
    val weightList = (0..500).map { (it * 0.5).toString() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reps")
                CustomWheelPicker(
                    items = repsList,
                    initialValue = reps,
                    onItemSelected = { reps = it }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text("Weight (kg)")
                CustomWheelPicker(
                    items = weightList,
                    initialValue = weight,
                    onItemSelected = { weight = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val repCount = reps.toIntOrNull() ?: 0
                val weightValue = weight.toDoubleOrNull() ?: 0.0
                onAddSet(repCount, weightValue)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Set")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
    }
}