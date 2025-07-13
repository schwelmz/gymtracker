package com.example.gymtracker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.data.CalorieMode
import com.example.gymtracker.data.TodayHealthStats
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.HomeUiState
import com.example.gymtracker.viewmodel.HomeViewModel
import java.lang.Float.max

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    foodViewModel: FoodViewModel,
    onGrantPermissionsClick: () -> Unit
) {
    val state by homeViewModel.uiState.collectAsState()
    val healthState = state.healthUiState
    val goals = state.userGoals

    val todaysFoodEntries by foodViewModel.todayFood.collectAsState(initial = emptyList())
    val totalCaloriesIntake = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.calories } }
    val totalProtein = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.protein } }
    val totalCarbs = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.carbs } }
    val totalFat = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.fat } }

    val context = LocalContext.current

    // --- State for the Goal Setting Dialog ---
    var showGoalDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogCurrentValue by remember { mutableStateOf("") }
    var onDialogSave by remember { mutableStateOf<(Int) -> Unit>({}) }

    if (showGoalDialog) {
        GoalSettingDialog(
            title = dialogTitle,
            initialValue = dialogCurrentValue,
            onDismiss = { showGoalDialog = false },
            onSave = { newValue ->
                onDialogSave(newValue)
                showGoalDialog = false
            }
        )
    }

    // Function to launch the dialog
    val launchGoalDialog = { title: String, currentValue: Int, onSaveAction: (Int) -> Unit ->
        dialogTitle = title
        dialogCurrentValue = currentValue.toString()
        onDialogSave = { onSaveAction(it) }
        showGoalDialog = true
    }

    LaunchedEffect(Unit) {
        homeViewModel.checkAvailabilityAndPermissions()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Today's Summary",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
            )
        }
        item {
            when (healthState) {
                is HomeUiState.Idle -> CircularProgressIndicator()
                is HomeUiState.HealthConnectNotInstalled -> { /* ... unchanged ... */ }
                is HomeUiState.PermissionsNotGranted -> { /* ... unchanged ... */ }
                is HomeUiState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CalorieBudgetGraph(
                            intake = totalCaloriesIntake,
                            burned = healthState.stats.caloriesBurned.toInt(),
                            goal = goals.calorieGoal,
                            calorieMode = goals.calorieMode,
                            onGoalClick = {
                                launchGoalDialog("Set Calorie Goal", goals.calorieGoal) {
                                    homeViewModel.updateUserGoal(calories = it)
                                }
                            },
                            onModeChange = { newMode ->
                                homeViewModel.updateUserGoal(calorieMode = newMode)
                            }
                        )
                        MacroSummaryCard(
                            protein = totalProtein,
                            carbs = totalCarbs,
                            fat = totalFat,
                            proteinGoal = goals.proteinGoal,
                            carbGoal = goals.carbGoal,
                            fatGoal = goals.fatGoal,
                            onProteinGoalClick = {
                                launchGoalDialog("Set Protein Goal (g)", goals.proteinGoal) {
                                    homeViewModel.updateUserGoal(protein = it)
                                }
                            },
                            onCarbGoalClick = {
                                launchGoalDialog("Set Carb Goal (g)", goals.carbGoal) {
                                    homeViewModel.updateUserGoal(carbs = it)
                                }
                            },
                            onFatGoalClick = {
                                launchGoalDialog("Set Fat Goal (g)", goals.fatGoal) {
                                    homeViewModel.updateUserGoal(fat = it)
                                }
                            }
                        )
                        HealthStatsGrid(
                            stats = healthState.stats,
                            stepsGoal = goals.stepsGoal,
                            onStepsGoalClick = {
                                launchGoalDialog("Set Daily Step Goal", goals.stepsGoal) {
                                    homeViewModel.updateUserGoal(steps = it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSettingDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it.filter { char -> char.isDigit() } },
                label = { Text("New Goal") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val newValue = textValue.toIntOrNull()
                    if (newValue != null) {
                        onSave(newValue)
                    }
                },
                enabled = textValue.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CalorieBudgetGraph(
    intake: Int,
    burned: Int,
    goal: Int,
    calorieMode: CalorieMode,
    onGoalClick: () -> Unit,
    onModeChange: (CalorieMode) -> Unit
) {
    val leftover = goal - intake
    val progress = (intake.toFloat() / max(1f, goal.toFloat())).coerceIn(0f, 1f)

    val isGoalAchieved = when (calorieMode) {
        CalorieMode.DEFICIT -> intake <= goal
        CalorieMode.SURPLUS -> intake >= goal
    }

    val progressBarColor = if (isGoalAchieved) Color(0xFF4CAF50) else Color.Red
    val leftoverTextColor = if (isGoalAchieved) Color(0xFF4CAF50) else Color.Red

    var menuExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calorie Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Box {
                    Row(
                        modifier = Modifier.clickable { menuExpanded = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = calorieMode.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change calorie mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Deficit") },
                            onClick = {
                                onModeChange(CalorieMode.DEFICIT)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Surplus") },
                            onClick = {
                                onModeChange(CalorieMode.SURPLUS)
                                menuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(MaterialTheme.shapes.small),
                color = progressBarColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- THIS ROW IS THE ONLY PART THAT HAS BEEN CHANGED ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CalorieStat(label = "Intake", value = "$intake")
                // "Goal" is now second
                Box(modifier = Modifier.clickable(onClick = onGoalClick).padding(4.dp)) {
                    CalorieStat(label = "Goal", value = "$goal")
                }
                // "Burned" is now third
                CalorieStat(label = "Burned", value = "$burned")
                CalorieStat(label = "Leftover", value = "$leftover", valueColor = leftoverTextColor)
            }
        }
    }
}
// --- MODIFIED: HealthStatsGrid now handles clicks and adds progress bar ---
@Composable
fun HealthStatsGrid(stats: TodayHealthStats, stepsGoal: Int, onStepsGoalClick: () -> Unit) {
    val progress = (stats.steps.toFloat() / max(1f, stepsGoal.toFloat())).coerceIn(0f, 1f)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.directionwalk_icon),
                    contentDescription = "Activity Stats",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onStepsGoalClick)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Steps", style = MaterialTheme.typography.labelLarge)
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "${stats.steps} / $stepsGoal", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(text = "Distance", style = MaterialTheme.typography.labelLarge)
                        // This spacer helps align the text with the progress bar in the other column
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "%.2f km".format(stats.distanceMeters / 1000), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- MODIFIED: MacroSummaryCard handles clicks ---
@Composable
fun MacroSummaryCard(
    protein: Int, carbs: Int, fat: Int,
    proteinGoal: Int, carbGoal: Int, fatGoal: Int,
    onProteinGoalClick: () -> Unit,
    onCarbGoalClick: () -> Unit,
    onFatGoalClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MacroStat(label = "Protein", value = protein, goal = proteinGoal, onClick = onProteinGoalClick)
            MacroStat(label = "Carbs", value = carbs, goal = carbGoal, onClick = onCarbGoalClick)
            MacroStat(label = "Fat", value = fat, goal = fatGoal, onClick = onFatGoalClick)
        }
    }
}

// --- MODIFIED: MacroStat now includes a progress bar and is clickable ---
@Composable
private fun MacroStat(label: String, value: Int, goal: Int, onClick: () -> Unit) {
    val progress = (value.toFloat() / max(1f, goal.toFloat())).coerceIn(0f, 1f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.width(80.dp).padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "${value}g / ${goal}g", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CalorieStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalContentColor.current
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}

// --- PREVIEWS UPDATED FOR NEW SIGNATURES ---
@Preview(showBackground = true)
@Composable
fun CalorieBudgetGraphPreview_UnderBudget() {
    AppTheme {
        CalorieBudgetGraph(
            intake = 1850,
            burned = 450,
            goal = 2200,
            calorieMode = CalorieMode.DEFICIT, // Provide a mode for the preview
            onGoalClick = {},
            onModeChange = {} // Provide an empty lambda
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieBudgetGraphPreview_OverBudget() {
    AppTheme {
        CalorieBudgetGraph(
            intake = 2500,
            burned = 300,
            goal = 2200,
            calorieMode = CalorieMode.SURPLUS, // Provide a mode for the preview
            onGoalClick = {},
            onModeChange = {} // Provide an empty lambda
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HealthStatsGridPreview() {
    AppTheme {
        HealthStatsGrid(
            stats = TodayHealthStats(
                steps = 8540,
                distanceMeters = 6832.0,
                caloriesBurned = 350.0
            ),
            stepsGoal = 10000,
            onStepsGoalClick = {} // Pass an empty lambda for the preview
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MacroSummaryCardPreview() {
    AppTheme {
        MacroSummaryCard(
            protein = 120,
            carbs = 180,
            fat = 50,
            proteinGoal = 150,
            carbGoal = 250,
            fatGoal = 70,
            onProteinGoalClick = {}, // Pass empty lambdas for the preview
            onCarbGoalClick = {},
            onFatGoalClick = {}
        )
    }
}