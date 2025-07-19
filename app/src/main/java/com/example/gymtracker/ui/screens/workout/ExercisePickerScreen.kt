package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerScreen(
    planId: Int,
    onNavigateUp: () -> Unit,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory),
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val allExercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
    val planWithExercises by workoutPlanViewModel.getPlanWithExercises(planId).collectAsState(initial = null)

    if (planWithExercises == null) {
        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
        return
    }

    val selectedExercises = remember { mutableStateListOf<String>().apply { addAll(planWithExercises!!.exercises.map { it.name }) } }
    var searchQuery by remember { mutableStateOf("") }
    var planGoal by remember { mutableStateOf(planWithExercises?.plan?.goal ?: 1) }

    // Filter exercises based on the search query
    val filteredExercises = remember(searchQuery, allExercises) {
        allExercises.filter { it.name.contains(searchQuery, ignoreCase = true) }
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
                workoutPlanViewModel.updatePlanGoal(planId, planGoal)
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

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Exercises") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("Weekly Goal (workouts)", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    (1..10).forEach { goal ->
                        FilterChip(
                            selected = planGoal == goal,
                            onClick = { planGoal = goal },
                            label = { Text(goal.toString()) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            items(filteredExercises, key = { it.name }) { exercise ->
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val imageModel: Any? = when {
                            exercise.imageResId != null -> exercise.imageResId
                            !exercise.imageUri.isNullOrBlank() -> exercise.imageUri
                            else -> null
                        }

                        imageModel?.let {
                            AsyncImage(
                                model = imageModel,
                                contentDescription = "Exercise Image",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1
                            )
                            if (!exercise.description.isNullOrBlank()) {
                                Text(
                                    text = exercise.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun resolveExerciseImage(imageUri: String?): Any? {
    return when (imageUri) {
        "Barbell Row" -> R.drawable.barbellrow
        "Bench Press" -> R.drawable.benchpress
        "deadlift" -> R.drawable.deadlift
        "overhead press" -> R.drawable.overheadpress
        "squat" -> R.drawable.squat
        else -> null
    }
}
