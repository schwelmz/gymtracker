package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
){
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding,
                        end = 16.dp
                    ),
                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
            ) {
                Text(
                    text = "Workout Plans",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        item {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        top = headlineTopPadding,
//                        bottom = headlineBottomPadding,
//                        end = 16.dp
//                    ),
//                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
//            )
//            {
//                Text("work in progress...")
//            }
            WorkoutPlanScreen(
                onExercisePicker = onExercisePicker,
                onLogWorkoutForPlan = onLogWorkoutForPlan
            )
        }
    }
}