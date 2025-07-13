package com.example.gymtracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.gymtracker.data.Food
import com.example.gymtracker.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: FoodViewModel,
    onDeleteFoodEntry: (Food) -> Unit,
    onNavigateToDiary: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToCustomFood: () -> Unit
) {
    Scaffold(
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val todaysFoodEntries by viewModel.todayFood.collectAsState(initial = emptyList())

            val totalCalories = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.calories } }
            val totalProtein = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.protein } }
            val totalCarbs = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.carbs } }
            val totalFat = remember(todaysFoodEntries) { todaysFoodEntries.sumOf { it.fat } }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
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
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                MacroStat(label = "Protein", value = totalProtein)
                                MacroStat(label = "Carbs", value = totalCarbs)
                                MacroStat(label = "Fat", value = totalFat)
                            }
                        }
                    }
                }

                // --- BUTTONS SECTION (MOVED) ---
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

                // Title for the food list
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

                items(todaysFoodEntries) { food ->
                    FoodCard(food = food, onDelete = { onDeleteFoodEntry(food) })
                }

                // Spacer at the end so list items don't hide behind the FAB
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


@Composable
fun FoodCard(
    food: Food,
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
                Button(onClick = { showDialog = false }) {
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
            if (!food.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = food.imageUri,
                    contentDescription = food.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = food.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                        .padding(16.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = food.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Weight: ${food.grams}g", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Calories: ${food.calories}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}