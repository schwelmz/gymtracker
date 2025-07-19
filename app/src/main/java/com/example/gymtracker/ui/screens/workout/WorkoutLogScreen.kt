package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.ExerciseSet
import com.example.gymtracker.ui.components.ExerciseSetInput
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding

@Composable
fun WorkoutLogScreen(
    exerciseName: String,
    sets: List<ExerciseSet>,
    onAddSet: (reps: Int, weight: Double) -> Unit,
    onWorkoutSaved: () -> Unit,
    initialReps: Int?,
    initialWeight: Double?
) {
    // Optimized padding for handling IME and Navigation bar insets
    val imeBottom = WindowInsets.ime
    val navBars = WindowInsets.navigationBars
    val bottomPadding = with(LocalDensity.current) {
        (imeBottom.exclude(navBars).getBottom(this)).toDp()
    }

    // Define layout using Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header for exercise name
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = headlineTopPadding, bottom = headlineBottomPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Exercise set input component with dynamic layout
        ExerciseSetInput(
            sets = sets,
            onAddSet = onAddSet,
            modifier = Modifier.weight(1f),
            initialReps = initialReps,
            initialWeight = initialWeight
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save workout button, enabled only when sets are added
        Button(
            onClick = onWorkoutSaved,
            enabled = sets.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish & Save Workout")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
