package com.example.gymtracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.model.CalorieMode
import com.example.gymtracker.data.model.UserGoals
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import com.example.gymtracker.viewmodel.GoalsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsSetGoalsScreen() {
    val viewModel: GoalsViewModel = viewModel(factory = GoalsViewModel.Factory)
    val userGoals by viewModel.userGoals.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var calorieGoal by remember { mutableStateOf("") }
    var proteinGoal by remember { mutableStateOf("") }
    var carbGoal by remember { mutableStateOf("") }
    var fatGoal by remember { mutableStateOf("") }
    var stepsGoal by remember { mutableStateOf("") }
    var calorieMode by remember { mutableStateOf(CalorieMode.DEFICIT) }

    LaunchedEffect(userGoals) {
        calorieGoal = userGoals.calorieGoal.toString()
        proteinGoal = userGoals.proteinGoal.toString()
        carbGoal = userGoals.carbGoal.toString()
        fatGoal = userGoals.fatGoal.toString()
        stepsGoal = userGoals.stepsGoal.toString()
        calorieMode = userGoals.calorieMode
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = headlineTopPadding,
                            bottom = headlineBottomPadding,
                            start = 16.dp,
                            end = 16.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Goal Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                GoalInputRow(
                    label = "Calorie Goal (kcal)",
                    value = calorieGoal,
                    onValueChange = { calorieGoal = it }
                )
            }

            item {
                CalorieModeSelector(
                    selectedMode = calorieMode,
                    onModeSelected = { calorieMode = it }
                )
            }

            item {
                GoalInputRow(
                    label = "Protein Goal (g)",
                    value = proteinGoal,
                    onValueChange = { proteinGoal = it }
                )
            }

            item {
                GoalInputRow(
                    label = "Carbohydrate Goal (g)",
                    value = carbGoal,
                    onValueChange = { carbGoal = it }
                )
            }

            item {
                GoalInputRow(
                    label = "Fat Goal (g)",
                    value = fatGoal,
                    onValueChange = { fatGoal = it }
                )
            }

            item {
                GoalInputRow(
                    label = "Steps Goal",
                    value = stepsGoal,
                    onValueChange = { stepsGoal = it }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val updatedGoals = UserGoals(
                            calorieGoal = calorieGoal.toIntOrNull() ?: userGoals.calorieGoal,
                            proteinGoal = proteinGoal.toIntOrNull() ?: userGoals.proteinGoal,
                            carbGoal = carbGoal.toIntOrNull() ?: userGoals.carbGoal,
                            fatGoal = fatGoal.toIntOrNull() ?: userGoals.fatGoal,
                            stepsGoal = stepsGoal.toIntOrNull() ?: userGoals.stepsGoal,
                            calorieMode = calorieMode
                        )
                        viewModel.saveUserGoals(updatedGoals)
                        scope.launch {
                            snackbarHostState.showSnackbar("Goals saved successfully!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Save Goals")
                }
            }
        }
    }
}

@Composable
fun GoalInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(120.dp)
        )
    }
}

@Composable
fun CalorieModeSelector(
    selectedMode: CalorieMode,
    onModeSelected: (CalorieMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Calorie Mode", style = MaterialTheme.typography.bodyLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalorieMode.values().forEach { mode ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedMode == mode,
                        onClick = { onModeSelected(mode) }
                    )
                    Text(
                        text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
