package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.WorkoutPlan
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding

@Composable
fun WorkoutPlansView(
    onExercisePicker: (WorkoutPlan) -> Unit,
    onLogWorkoutForPlan: (List<String>, Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = headlineTopPadding,
                    bottom = headlineBottomPadding,
                    end = 16.dp
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Workout Plans",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Displaying the WorkoutPlanScreen directly, assuming it displays plans from the ViewModel
        WorkoutPlanScreen(
            onExercisePicker = onExercisePicker,
            onLogWorkoutForPlan = onLogWorkoutForPlan
        )
    }
}
