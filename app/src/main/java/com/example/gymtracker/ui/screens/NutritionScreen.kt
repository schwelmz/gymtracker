package com.example.gymtracker.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.FoodLogWithDetails
import com.example.gymtracker.ui.components.DateTimePickerDialog
import com.example.gymtracker.ui.components.FoodCard // Assuming FoodCard is in components
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: FoodViewModel,
    // Note: The signature is now simpler because the actions are handled internally
    onNavigateToDiary: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToCustomFood: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // 1. USE THE NEW UNIFIED DATA MODEL
    val todaysFoodLogs by viewModel.todayFoodLogs.collectAsState(initial = emptyList())

    // --- STATE MANAGEMENT FOR EDIT/DELETE ACTIONS ---
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var selectedLog by remember { mutableStateOf<FoodLogWithDetails?>(null) }

    // --- DIALOGS ---
    if (showOptionsDialog && selectedLog != null) {
        OptionsDialog(
            foodName = selectedLog!!.name,
            onDismiss = { showOptionsDialog = false },
            onEditClick = {
                showOptionsDialog = false
                showDateTimePicker = true
            },
            onDeleteClick = {
                scope.launch { viewModel.deleteFoodLog(selectedLog!!.logId) }
                showOptionsDialog = false
            }
        )
    }

    if (showDateTimePicker && selectedLog != null) {
        DateTimePickerDialog(
            initialTimestamp = selectedLog!!.timestamp,
            onDismiss = { showDateTimePicker = false },
            onDateTimeSelected = { newTimestamp ->
                viewModel.updateLogTimestamp(selectedLog!!.logId, newTimestamp)
                showDateTimePicker = false
            }
        )
    }


    Scaffold { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            val totalCalories = remember(todaysFoodLogs) { todaysFoodLogs.sumOf { it.calories } }
            val totalProtein = remember(todaysFoodLogs) { todaysFoodLogs.sumOf { it.protein } }
            val totalCarbs = remember(todaysFoodLogs) { todaysFoodLogs.sumOf { it.carbs } }
            val totalFat = remember(todaysFoodLogs) { todaysFoodLogs.sumOf { it.fat } }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Today's Summary",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = "Today's Total Calories",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "$totalCalories kcal",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            HorizontalDivider(
                                thickness = DividerDefaults.Thickness,
                                color = DividerDefaults.color
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                MacroStat(label = "Protein", value = totalProtein)
                                MacroStat(label = "Carbs", value = totalCarbs)
                                MacroStat(label = "Fat", value = totalFat)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToCustomFood() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Food")
                        }
                        Button(
                            onClick = { onNavigateToDiary() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Diary")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Entries",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                // 2. UPDATE LAZYCOLUMN ITEMS
                items(todaysFoodLogs, key = { it.logId }) { foodLog ->
                    FoodCard(
                        foodLog = foodLog,
                        onLongPress = {
                            selectedLog = foodLog
                            showOptionsDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    }
}

/**
 * A small, reusable composable to display a single macronutrient stat.
 */
@Composable
private fun MacroStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = "${value}g", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

/**
 * A dialog that gives the user the choice to Edit or Delete.
 * This can be moved to a shared components file if needed.
 */
@Composable
private fun OptionsDialog(
    foodName: String,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(foodName) },
        text = { Text("What would you like to do?") },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        confirmButton = {
            Row {
                TextButton(onClick = onEditClick) { Text("Edit Time") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDeleteClick) { Text("Delete") }
            }
        }
    )
}