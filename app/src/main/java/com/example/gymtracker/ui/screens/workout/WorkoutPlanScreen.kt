package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.model.WorkoutPlan
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WorkoutPlanScreen(
    viewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory),
    onExercisePicker: (WorkoutPlan) -> Unit,
    onLogWorkoutForPlan: (List<String>, Int) -> Unit
) {
    val plans by viewModel.allPlans.collectAsState(initial = emptyList()) // State for all workout plans
    var showDialog by remember { mutableStateOf(false) } // State to manage showing the dialog for new plan
    var deleteDialogPlan by remember { mutableStateOf<WorkoutPlan?>(null) } // State for delete confirmation dialog
    var newPlanName by remember { mutableStateOf("") } // State for the name of the new plan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Iterate through all plans and display them
        plans.forEach { planWithExercises ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .combinedClickable(
                        onClick = {
                            onLogWorkoutForPlan(planWithExercises.exercises.map { it.name }, planWithExercises.plan.id)
                        },
                        onLongClick = { deleteDialogPlan = planWithExercises.plan } // Long click to show delete dialog
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(planWithExercises.plan.name, style = MaterialTheme.typography.titleLarge)

                    if (planWithExercises.exercises.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Exercises:")
                        planWithExercises.exercises.forEach { exercise ->
                            Text("• ${exercise.name}")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = { onExercisePicker(planWithExercises.plan) }) {
                            Text("Edit Exercises")
                        }
                        Button(onClick = {
                            onLogWorkoutForPlan(planWithExercises.exercises.map { it.name }, planWithExercises.plan.id)
                        }) {
                            Text("Start Workout")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Button to show new plan creation dialog
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Create New Plan")
        }

        // Dialog to create a new workout plan
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPlanName.isNotBlank()) {
                            // Offload the creation of the new plan to a background thread
                            viewModel.createPlan(newPlanName, null)
                            newPlanName = "" // Clear the input after creation
                            showDialog = false // Close the dialog
                        }
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("New Workout Plan") },
                text = {
                    OutlinedTextField(
                        value = newPlanName,
                        onValueChange = { newPlanName = it },
                        label = { Text("Plan Name") }
                    )
                }
            )
        }

        // Confirm delete plan dialog
        deleteDialogPlan?.let { plan ->
            AlertDialog(
                onDismissRequest = { deleteDialogPlan = null },
                title = { Text("Delete Plan") },
                text = { Text("Are you sure you want to delete '${plan.name}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        // Offload the delete operation to a background thread
                        viewModel.deletePlan(plan)
                        deleteDialogPlan = null
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialogPlan = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
