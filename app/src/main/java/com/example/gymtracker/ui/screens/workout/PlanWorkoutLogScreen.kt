package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.components.ExerciseSetInput
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import com.example.gymtracker.viewmodel.WorkoutViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    // Collect sets for the current exercise from the ViewModel
    val currentSets by workoutViewModel.currentSets.collectAsState()
    val lastReps by workoutViewModel.lastReps.collectAsState()
    val lastWeight by workoutViewModel.lastWeight.collectAsState()

    // Reset current sets in ViewModel when switching exercises
    LaunchedEffect(currentExerciseName) {
        if (currentExerciseName.isNotBlank()) {
            workoutViewModel.resetLastSession() // Reset first
            workoutViewModel.loadLastSession(currentExerciseName)
            workoutViewModel.resetCurrentSets()
        }
    }

    val imeBottom = WindowInsets.ime
    val navBars = WindowInsets.navigationBars
    val combinedInsets = imeBottom.exclude(navBars)
    val bottomPadding = with(LocalDensity.current) { combinedInsets.getBottom(this).toDp() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
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
                            scope.launch {
                                workoutViewModel.saveWorkoutSession(currentExerciseName, planId)
                            }
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
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding
                    ),
                contentAlignment = Alignment.Center // Aligns content to the end (right)
            ) {
                Text(
                    text = currentExerciseName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            if (lastReps != null && lastWeight != null) {
                ExerciseSetInput(
                    sets = currentSets,
                    onAddSet = { reps, weight ->
                        workoutViewModel.addSet(reps, weight)
                    },
                    modifier = Modifier
                        .fillMaxWidth() // Use fillMaxWidth instead of fillMaxSize for ExerciseSetInput
                        .padding(horizontal = 16.dp), // Apply specific padding here
                    initialReps = lastReps,
                    initialWeight = lastWeight
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
