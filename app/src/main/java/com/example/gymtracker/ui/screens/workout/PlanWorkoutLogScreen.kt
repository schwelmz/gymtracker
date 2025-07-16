package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.model.ExerciseSet
import com.example.gymtracker.viewmodel.WorkoutViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Done

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanWorkoutLogScreen(
    exerciseNames: String?,
    planId: Int?,
    onNavigateUp: () -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val exercises = remember(exerciseNames) {
        exerciseNames?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    var currentExerciseIndex by remember { mutableStateOf(0) }
    val currentExerciseName = remember(currentExerciseIndex, exercises) {
        exercises.getOrNull(currentExerciseIndex) ?: ""
    }

    // State for the current logging session for the current exercise
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // Collect sets for the current exercise from the ViewModel
    val currentSets by workoutViewModel.currentSets.collectAsState()

    // Reset current sets in ViewModel when switching exercises
    LaunchedEffect(currentExerciseName) {
        workoutViewModel.resetCurrentSets()
    }

    val imeBottom = WindowInsets.ime
    val navBars = WindowInsets.navigationBars
    val combinedInsets = imeBottom.exclude(navBars)
    val bottomPadding = with(LocalDensity.current) { combinedInsets.getBottom(this).toDp() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Workout: ${currentExerciseName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentExerciseIndex == exercises.lastIndex) {
                FloatingActionButton(
                    onClick = {
                        // Save the last exercise's sets
                        if (currentSets.isNotEmpty()) {
                            workoutViewModel.saveWorkoutSession(currentExerciseName, planId)
                        }
                        if (planId != null) {
                            workoutViewModel.logWorkoutPlanCompletion(planId)
                        }
                        onNavigateUp()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Finish Workout")
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        // Save current exercise's sets and move to next
                        if (currentSets.isNotEmpty()) {
                            workoutViewModel.saveWorkoutSession(currentExerciseName, planId)
                        }
                        currentExerciseIndex++
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Exercise")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        workoutViewModel.addSet(repCount, weightValue)
                        reps = ""
                        weight = ""
                    }
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
                if (currentSets.isNotEmpty()) {
                    item {
                        Text(
                            text = "Logged Sets",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                itemsIndexed(currentSets) { index, set ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = "Set ${index + 1}: ${set.reps} reps @ ${set.weight} kg")
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
