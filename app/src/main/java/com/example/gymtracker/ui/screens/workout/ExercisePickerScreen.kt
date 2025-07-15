package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerScreen(
    planId: Int,
    onNavigateUp: () -> Unit,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val allExercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
    val planWithExercises by workoutPlanViewModel.getPlanWithExercises(planId).collectAsState(initial = null)

    if (planWithExercises == null) {
        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
        return
    }

    val selectedExercises = remember(planWithExercises) {
        mutableStateListOf<String>().apply {
            addAll(planWithExercises!!.exercises.map { it.name })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Exercises") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                workoutPlanViewModel.updatePlanExercises(planId, selectedExercises)
                onNavigateUp()
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allExercises, key = { it.name }) { exercise ->
                val isSelected = exercise.name in selectedExercises
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) {
                                selectedExercises.remove(exercise.name)
                            } else {
                                selectedExercises.add(exercise.name)
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                        if (!exercise.description.isNullOrBlank()) {
                            Text(
                                text = exercise.description ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
