package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel

@Composable
fun PlanWorkoutLogScreen(
    planId: Int,
    onExerciseSelected: (String) -> Unit,
    viewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory)
) {
    val planWithExercises by viewModel.getPlanWithExercises(planId).collectAsState(initial = null)
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = planWithExercises?.plan?.name ?: "Loading...",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(planWithExercises?.exercises ?: emptyList()) { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onExerciseSelected(exercise.name) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Full-height Image on Left
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp), // Match image width to card height
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            exercise.imageUri != null -> {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(exercise.imageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Exercise Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            exercise.imageResId != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(exercise.imageResId),
                                    contentDescription = "Exercise Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                Text("No Image")
                            }
                        }
                    }

                    // Text info
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 12.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (exercise.description.isNotBlank()) {
                            Text(
                                text = exercise.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

