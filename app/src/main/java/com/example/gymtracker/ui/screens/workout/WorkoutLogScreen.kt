package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    val imeBottom = WindowInsets.ime
    val navBars = WindowInsets.navigationBars
    val combinedInsets = imeBottom.exclude(navBars) // removes nav bar padding from IME height
    val bottomPadding = with(LocalDensity.current) { combinedInsets.getBottom(this).toDp() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                text = exerciseName,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
                // textAlign can be removed if the Box handles the alignment
            )
        }

        ExerciseSetInput(
            sets = sets,
            onAddSet = onAddSet,
            modifier = Modifier.weight(1f),
            initialReps = initialReps,
            initialWeight = initialWeight
        )

        Spacer(modifier = Modifier.height(8.dp))

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

