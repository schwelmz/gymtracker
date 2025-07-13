package com.example.gymtracker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // <-- ADD THIS IMPORT
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.data.TodayHealthStats
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.HomeUiState
import com.example.gymtracker.viewmodel.HomeViewModel
import java.lang.Float.max

// ... (HomeScreen composable remains unchanged)

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    foodViewModel: FoodViewModel,
    onGrantPermissionsClick: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val todaysFoodEntries by foodViewModel.todayFood.collectAsState(initial = emptyList())
    val totalCaloriesIntake = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.calories } }
    val totalProtein = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.protein } }
    val totalCarbs = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.carbs } }
    val totalFat = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.fat } }

    val context = LocalContext.current

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
                text = "Today's Activity",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
            )
        }
        item {
            when (val state = uiState) {
                is HomeUiState.Idle -> CircularProgressIndicator()
                is HomeUiState.HealthConnectNotInstalled -> {
                    PermissionCard(
                        title = "Health Connect Not Installed",
                        description = "To view your health stats, please install the Health Connect app.",
                        buttonText = "Install",
                        onButtonClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
                                setPackage("com.android.vending")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
                is HomeUiState.PermissionsNotGranted -> {
                    PermissionCard(
                        title = "Permissions Required",
                        description = "This app needs permission to read your health data. Tap below to grant access.",
                        buttonText = "Grant Permissions",
                        onButtonClick = onGrantPermissionsClick
                    )
                }
                is HomeUiState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CalorieBudgetGraph(
                            intake = totalCaloriesIntake,
                            burned = state.stats.caloriesBurned
                        )
                        MacroSummaryCard(
                            protein = totalProtein,
                            carbs = totalCarbs,
                            fat = totalFat
                        )
                        HealthStatsGrid(stats = state.stats)
                    }
                }
            }
        }
    }
}


// --- CalorieBudgetGraph with GREEN color logic ---
@Composable
fun CalorieBudgetGraph(intake: Int, burned: Double) {
    val burnedInt = burned.toInt()
    val leftover = burnedInt - intake
    val progress = (intake.toFloat() / max(1f, burnedInt.toFloat())).coerceIn(0f, 1f)

    val progressBarColor = if (leftover >= 0) {
        Color.Green // Use a distinct green color
    } else {
        Color.Red
    }

    // Also set the text color for the 'leftover' stat
    val leftoverTextColor = if (leftover >= 0) {
        Color.Green
    } else {
        Color.Red
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Calorie Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(MaterialTheme.shapes.small),
                color = progressBarColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalorieStat(label = "Intake", value = "$intake")
                CalorieStat(label = "Burned", value = "$burnedInt")
                // Pass the dynamic color to the 'Leftover' stat
                CalorieStat(label = "Leftover", value = "$leftover", valueColor = leftoverTextColor as Color)
            }
        }
    }
}

/**
 * A small, reusable composable to display a single calorie stat.
 * Now with an optional color parameter.
 */
@Composable
private fun CalorieStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalContentColor.current // Default to the current content color
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
            color = valueColor // Use the provided color
        )
    }
}


// ... The rest of your file (HealthStatsGrid, Previews, etc.) is unchanged ...

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

@Composable
fun HealthStatsGrid(stats: TodayHealthStats) {
    ActivityStatCard(
        steps = stats.steps.toString(),
        distance = "%.2f km".format(stats.distanceMeters / 1000)
    )
}

@Composable
fun ActivityStatCard(steps: String, distance: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.directionwalk_icon),
                contentDescription = "Activity Stats",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Steps", style = MaterialTheme.typography.labelLarge)
                    Text(text = steps, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Distance", style = MaterialTheme.typography.labelLarge)
                    Text(text = distance, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MacroSummaryCard(protein: Int, carbs: Int, fat: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MacroStat(label = "Protein", value = protein)
            MacroStat(label = "Carbs", value = carbs)
            MacroStat(label = "Fat", value = fat)
        }
    }
}

@Composable
private fun MacroStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = "${value}g", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

// Previews
@Preview(showBackground = true)
@Composable
fun CalorieBudgetGraphPreview_UnderBudget() {
    AppTheme {
        CalorieBudgetGraph(intake = 1250, burned = 2100.0)
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieBudgetGraphPreview_OverBudget() {
    AppTheme {
        CalorieBudgetGraph(intake = 2500, burned = 2100.0)
    }
}

@Preview(showBackground = true)
@Composable
fun HealthStatsGridPreview() {
    AppTheme {
        HealthStatsGrid(
            stats = TodayHealthStats(
                steps = 10456,
                distanceMeters = 8364.8,
                caloriesBurned = 418.0
            )
        )
    }
}