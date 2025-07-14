package com.example.gymtracker.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.WorkoutPlan
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel

@Composable
fun WorkoutPlanScreen(
    viewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory),
    onExercisePicker: (WorkoutPlan) -> Unit,
    onLogWorkoutForPlan: (WorkoutPlan) -> Unit
) {
    val plans by viewModel.allPlans.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var deleteDialogPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var newPlanName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Workout Plans", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        plans.forEach { planWithExercises ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .combinedClickable(
                        onClick = { onLogWorkoutForPlan(planWithExercises.plan) }, // <- Card click
                        onLongClick = { deleteDialogPlan = planWithExercises.plan }
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
                    Button(onClick = { onExercisePicker(planWithExercises.plan) }) {
                        Text("Edit Exercises")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Create New Plan")
        }

        // Create plan dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPlanName.isNotBlank()) {
                            viewModel.createPlan(newPlanName)
                            newPlanName = ""
                            showDialog = false
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

        // Confirm delete dialog
        deleteDialogPlan?.let { plan ->
            AlertDialog(
                onDismissRequest = { deleteDialogPlan = null },
                title = { Text("Delete Plan") },
                text = { Text("Are you sure you want to delete '${plan.name}'?") },
                confirmButton = {
                    TextButton(onClick = {
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
