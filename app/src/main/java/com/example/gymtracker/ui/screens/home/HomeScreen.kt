package com.example.gymtracker.ui.screens.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.data.model.CalorieMode
import com.example.gymtracker.data.model.DiaryEntry
import com.example.gymtracker.data.model.TodayHealthStats
import com.example.gymtracker.data.model.WeightEntry
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.GoalsViewModel
import com.example.gymtracker.viewmodel.HomeUiState
import com.example.gymtracker.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.lang.Float.max
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    foodViewModel: FoodViewModel,
    goalsViewModel: GoalsViewModel,
    onGrantPermissionsClick: () -> Unit,
    onNavigateToWeightHistory: () -> Unit
) {
    val healthState by homeViewModel.uiState.collectAsState()
    val goals by goalsViewModel.uiState.collectAsState()
    val weightEntries by homeViewModel.weightEntries.collectAsState(initial = emptyList())

    val todaysDiaryEntries by foodViewModel.todayDiaryEntries.collectAsState(initial = emptyList())
    val (totalCaloriesIntake, totalProtein, totalCarbs, totalFat) = remember(todaysDiaryEntries) {
        var cals = 0; var prot = 0; var carb = 0; var fat = 0
        todaysDiaryEntries.forEach { entry ->
            when (entry) {
                is DiaryEntry.Food -> {
                    cals += entry.details.calories
                    prot += entry.details.protein
                    carb += entry.details.carbs
                    fat += entry.details.fat
                }
                is DiaryEntry.Recipe -> {
                    cals += entry.log.totalCalories
                    prot += entry.log.totalProtein
                    carb += entry.log.totalCarbs
                    fat += entry.log.totalFat
                }
            }
        }
        listOf(cals, prot, carb, fat)
    }

    val context = LocalContext.current

    var showGoalDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogCurrentValue by remember { mutableStateOf("") }
    var onDialogSave by remember { mutableStateOf<(Int) -> Unit>({}) }
    val dismissedCard by homeViewModel.dismissedDisabledCard.collectAsState(initial = false)
    val showDisabledCard = healthState is HomeUiState.PermissionsDeclined && !dismissedCard
    val scope = rememberCoroutineScope()

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
            when (val state = healthState) {
                is HomeUiState.Idle -> CircularProgressIndicator()
                is HomeUiState.HealthConnectNotInstalled -> {
                    PermissionCard(
                        title = "Health Connect Not Installed",
                        description = "To view your health stats, please install the Health Connect app.",
                        grantButtonText = "Install",
                        onGrantClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
                                setPackage("com.android.vending")
                            }
                            context.startActivity(intent)
                        },
                        onDeclineClick = { /* No decline option for installation */ },
                        showDeclineButton = false
                    )
                }
                is HomeUiState.PermissionsNotGranted -> {
                    PermissionCard(
                        title = "Permissions Required",
                        description = "This app needs permission to read your health data. Tap below to grant access.",
                        grantButtonText = "Grant Permissions",
                        onGrantClick = onGrantPermissionsClick,
                        onDeclineClick = { homeViewModel.userDeclinedPermissions() }
                    )
                }
                // --- NEW: Handle the case where permissions were declined ---
                is HomeUiState.PermissionsDeclined -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CalorieBudgetGraph(
                            intake = totalCaloriesIntake,
                            burned = 0, // Burned calories are unknown
                            goal = goals.calorieGoal,
                            calorieMode = goals.calorieMode,
                            showBurned = false, // Hide the burned stat
                            onGoalClick = {
                                launchGoalDialog("Set Calorie Goal", goals.calorieGoal) {
                                    goalsViewModel.updateUserGoal(calories = it)
                                }
                            },
                            onModeChange = { newMode ->
                                goalsViewModel.updateUserGoal(calorieMode = newMode)
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
                                    goalsViewModel.updateUserGoal(protein = it)
                                }
                            },
                            onCarbGoalClick = {
                                launchGoalDialog("Set Carb Goal (g)", goals.carbGoal) {
                                    goalsViewModel.updateUserGoal(carbs = it)
                                }
                            },
                            onFatGoalClick = {
                                launchGoalDialog("Set Fat Goal (g)", goals.fatGoal) {
                                    goalsViewModel.updateUserGoal(fat = it)
                                }
                            }
                        )
                        // Show the new disabled card instead of the grid
                        if (showDisabledCard) {
                            HealthStatsDisabledCard(onDismiss = {
                                scope.launch {
                                    homeViewModel.userPreferencesRepository.setDismissedDisabledCard(true)
                                }
                            })
                        }

                        WeightTrackerCard(
                            weightEntries = weightEntries,
                            onNavigateToWeightHistory = onNavigateToWeightHistory
                        )
                    }
                }
                is HomeUiState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CalorieBudgetGraph(
                            intake = totalCaloriesIntake,
                            burned = state.stats.caloriesBurned.toInt(),
                            goal = goals.calorieGoal,
                            calorieMode = goals.calorieMode,
                            onGoalClick = {
                                launchGoalDialog("Set Calorie Goal", goals.calorieGoal) {
                                    goalsViewModel.updateUserGoal(calories = it)
                                }
                            },
                            onModeChange = { newMode ->
                                goalsViewModel.updateUserGoal(calorieMode = newMode)
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
                                    goalsViewModel.updateUserGoal(protein = it)
                                }
                            },
                            onCarbGoalClick = {
                                launchGoalDialog("Set Carb Goal (g)", goals.carbGoal) {
                                    goalsViewModel.updateUserGoal(carbs = it)
                                }
                            },
                            onFatGoalClick = {
                                launchGoalDialog("Set Fat Goal (g)", goals.fatGoal) {
                                    goalsViewModel.updateUserGoal(fat = it)
                                }
                            }
                        )
                        HealthStatsGrid(
                            stats = state.stats,
                            stepsGoal = goals.stepsGoal,
                            onStepsGoalClick = {
                                launchGoalDialog("Set Daily Step Goal", goals.stepsGoal) {
                                    goalsViewModel.updateUserGoal(steps = it)
                                }
                            }
                        )
                        WeightTrackerCard(
                            weightEntries = weightEntries,
                            onNavigateToWeightHistory = onNavigateToWeightHistory
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun WeightTrackerCard(
    weightEntries: List<WeightEntry>,
    onNavigateToWeightHistory: () -> Unit
) {
    val latestEntry = weightEntries.firstOrNull()
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weight Tracker", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            if (latestEntry != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Latest Weight", style = MaterialTheme.typography.labelLarge)
                        Text("%.1f kg".format(latestEntry.weight), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(latestEntry.date.format(formatter), style = MaterialTheme.typography.bodySmall)
                    }
                    OutlinedButton(onClick = onNavigateToWeightHistory, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                        Text("View History")
                    }
                }
            } else {
                Text("No weight entries yet.", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onNavigateToWeightHistory,colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                    Text("Add First Weight")
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
                    textValue.toIntOrNull()?.let { onSave(it) }
                },
                enabled = textValue.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss,colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
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
    onModeChange: (CalorieMode) -> Unit,
    showBurned: Boolean = true // <-- NEW PARAMETER
) {
    val leftover = goal - intake
    val progress = (intake.toFloat() / max(1f, goal.toFloat())).coerceIn(0f, 1f)

    val isGoalAchieved = when (calorieMode) {
        CalorieMode.DEFICIT -> intake <= goal
        CalorieMode.SURPLUS -> intake >= goal
    }

    val progressBarColor = if (isGoalAchieved) Color(0xFF8BC34A) else MaterialTheme.colorScheme.error
    val leftoverTextColor = if (isGoalAchieved) LocalContentColor.current else MaterialTheme.colorScheme.error

    var menuExpanded by remember { mutableStateOf(false) }
    val goalLabel = when (calorieMode) {
        CalorieMode.DEFICIT -> "Limit"
        CalorieMode.SURPLUS -> "Goal"
    }
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

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(MaterialTheme.shapes.small),
                color = progressBarColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CalorieStat(label = "Intake", value = "$intake")
                Box(modifier = Modifier
                    .clickable(onClick = onGoalClick)
                    .padding(4.dp)) {
                    CalorieStat(label = goalLabel, value = "$goal")
                }
                // Conditionally show the "Burned" stat
                if (showBurned) {
                    CalorieStat(label = "Burned", value = "$burned")
                }
                CalorieStat(label = "Leftover", value = "$leftover", valueColor = leftoverTextColor)
            }
        }
    }
}
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
                            progress = { progress }, // BUGFIX: Corrected syntax
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )
                        Text(text = "${stats.steps} / $stepsGoal", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(text = "Distance", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "%.2f km".format(stats.distanceMeters / 1000), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MacroStat(label = "Protein", value = protein, goal = proteinGoal, onClick = onProteinGoalClick)
            MacroStat(label = "Carbs", value = carbs, goal = carbGoal, onClick = onCarbGoalClick)
            MacroStat(label = "Fat", value = fat, goal = fatGoal, onClick = onFatGoalClick)
        }
    }
}

@Composable
private fun MacroStat(label: String, value: Int, goal: Int, onClick: () -> Unit) {
    val progress = (value.toFloat() / max(1f, goal.toFloat())).coerceIn(0f, 1f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        LinearProgressIndicator(
            progress = { progress }, // BUGFIX: Corrected syntax
            modifier = Modifier
                .width(80.dp)
                .padding(vertical = 4.dp),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
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
    grantButtonText: String,
    onGrantClick: () -> Unit,
    onDeclineClick: () -> Unit,
    showDeclineButton: Boolean = true // Make decline optional
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showDeclineButton) {
                    OutlinedButton(onClick = onDeclineClick, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text("Decline")
                    }
                }
                OutlinedButton(onClick = onGrantClick, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                    Text(grantButtonText)
                }
            }
        }
    }
}
@Composable
fun HealthStatsDisabledCard(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Health Stats Disabled", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "You have declined health permissions. To enable activity tracking, please grant access in your phone's settings. You can change this at any time in the FreeGym Settings.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // The new "I understand" button
                OutlinedButton(onClick = onDismiss) {
                    Text("I understand")
                }
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Go to Settings")
                }
            }
        }
    }
}
