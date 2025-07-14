package com.example.gymtracker.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.data.FoodLogWithDetails // <-- IMPORT THE NEW DATA CLASS
import com.example.gymtracker.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: FoodViewModel,
    // 1. UPDATE THE FUNCTION SIGNATURE
    onDeleteFoodEntry: (FoodLogWithDetails) -> Unit,
    onNavigateToDiary: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToCustomFood: () -> Unit
) {
    Scaffold { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            // 2. USE THE NEW STATE FLOW FROM THE VIEWMODEL
            val todaysFoodLogs by viewModel.todayFoodLogs.collectAsState(initial = emptyList())

            // 3. UPDATE THE AGGREGATE CALCULATIONS
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

                // 4. UPDATE THE LAZYCOLUMN ITEMS
                items(todaysFoodLogs) { foodLog ->
                    FoodCard(foodLog = foodLog, onDelete = { onDeleteFoodEntry(foodLog) })
                }

                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
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

// 5. UPDATE THE FOODCARD TO USE THE NEW DATA CLASS
@Composable
fun FoodCard(
    foodLog: FoodLogWithDetails,
    onDelete: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Food Entry") },
            text = { Text("Are you sure you want to delete this food entry?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val cardHeight = 120.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(cardHeight)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDialog = true
                    }
                )
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val imageWidth = 95.dp
            if (!foodLog.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = foodLog.imageUrl,
                    contentDescription = foodLog.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = foodLog.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                        .padding(16.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = foodLog.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Weight: ${foodLog.grams}g", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Calories: ${foodLog.calories}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}