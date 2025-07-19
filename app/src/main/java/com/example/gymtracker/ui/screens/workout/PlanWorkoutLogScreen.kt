package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.components.ExerciseSetInput
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import com.example.gymtracker.viewmodel.WorkoutViewModel
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
    val currentExerciseName = exercises.getOrElse(currentExerciseIndex) { "" }

    val currentSets by workoutViewModel.currentSets.collectAsState()
    val lastReps by workoutViewModel.lastReps.collectAsState()
    val lastWeight by workoutViewModel.lastWeight.collectAsState()

    // Reset sets for current exercise when switching
    LaunchedEffect(currentExerciseName) {
        if (currentExerciseName.isNotBlank()) {
            workoutViewModel.resetLastSession()
            workoutViewModel.loadLastSession(currentExerciseName)
            workoutViewModel.resetCurrentSets()
        }
    }

    val imeBottom = WindowInsets.ime
    val navBars = WindowInsets.navigationBars
    val combinedInsets = imeBottom.exclude(navBars)
    val bottomPadding = with(LocalDensity.current) { combinedInsets.getBottom(this).toDp() }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentExerciseName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            val isLastExercise = currentExerciseIndex == exercises.lastIndex
            FloatingActionButton(
                onClick = {
                    if (currentSets.isNotEmpty()) {
                        workoutViewModel.saveWorkoutSession(currentExerciseName, planId)
                    }
                    if (isLastExercise) {
                        onNavigateUp()
                    } else {
                        currentExerciseIndex++
                    }
                },
                containerColor = if (isLastExercise) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = if (isLastExercise) Icons.Default.Done else Icons.Default.ArrowForward,
                    contentDescription = if (isLastExercise) "Finish Workout" else "Next Exercise"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headlineTopPadding, bottom = headlineBottomPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentExerciseName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Display the ExerciseSetInput or a loading state
            if (lastReps != null && lastWeight != null) {
                ExerciseSetInput(
                    sets = currentSets,
                    onAddSet = { reps, weight -> workoutViewModel.addSet(reps, weight) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    initialReps = lastReps,
                    initialWeight = lastWeight
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
