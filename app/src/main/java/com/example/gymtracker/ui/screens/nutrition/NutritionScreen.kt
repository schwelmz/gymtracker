package com.example.gymtracker.ui.screens.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.DiaryEntry
import com.example.gymtracker.ui.components.DateTimePickerDialog
import com.example.gymtracker.ui.components.EditFoodLogDialog
import com.example.gymtracker.ui.components.FoodCard
import com.example.gymtracker.ui.components.RecipeLogCard
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: FoodViewModel,
    onNavigateToCustomFood: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val todaysDiaryEntries by viewModel.todayDiaryEntries.collectAsState(initial = emptyList())

    var showOptionsDialog by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showGramsEditor by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    val entry = selectedEntry
    if (showOptionsDialog && entry != null) {
        when (entry) {
            is DiaryEntry.Food -> {
                FoodOptionsDialog(
                    foodName = entry.details.name,
                    onDismiss = { showOptionsDialog = false; selectedEntry = null },
                    onEditTimeClick = {
                        showOptionsDialog = false
                        showDateTimePicker = true
                    },
                    onEditStatsClick = {
                        showOptionsDialog = false
                        showGramsEditor = true
                    },
                    onDeleteClick = {
                        scope.launch { viewModel.deleteFoodLog(entry.details.logId) }
                        showOptionsDialog = false
                        selectedEntry = null
                    }
                )
            }
            is DiaryEntry.Recipe -> {
                DeleteConfirmDialog(
                    itemName = entry.log.name,
                    onDismiss = { showOptionsDialog = false; selectedEntry = null },
                    onConfirm = {
                        viewModel.deleteRecipeLog(entry.log.id)
                        showOptionsDialog = false
                        selectedEntry = null
                    }
                )
            }
        }
    }

    if (showDateTimePicker && entry is DiaryEntry.Food) {
        DateTimePickerDialog(
            initialTimestamp = entry.details.timestamp,
            onDismiss = { showDateTimePicker = false; selectedEntry = null },
            onDateTimeSelected = { newTimestamp ->
                viewModel.updateLogTimestamp(entry.details.logId, newTimestamp)
                showDateTimePicker = false
                selectedEntry = null
            }
        )
    }

    if (showGramsEditor && entry is DiaryEntry.Food) {
        EditFoodLogDialog(
            initialGrams = entry.details.grams,
            initialCalories = entry.details.calories,
            initialProtein = entry.details.protein,
            initialCarbs = entry.details.carbs,
            initialFat = entry.details.fat,
            onDismiss = { showGramsEditor = false; selectedEntry = null },
            onSave = { newGrams, newCalories, newProtein, newCarbs, newFat ->
                scope.launch {
                    viewModel.updateFoodLog(
                        logId = entry.details.logId,
                        grams = newGrams,
                        calories = newCalories,
                        protein = newProtein,
                        carbs = newCarbs,
                        fat = newFat
                    )
                }
                showGramsEditor = false
                selectedEntry = null
            }
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val (totalCalories, totalProtein, totalCarbs, totalFat) = remember(todaysDiaryEntries) {
                var cals = 0; var prot = 0; var carb = 0; var fat = 0
                todaysDiaryEntries.forEach { diaryEntry ->
                    when (diaryEntry) {
                        is DiaryEntry.Food -> {
                            cals += diaryEntry.details.calories
                            prot += diaryEntry.details.protein
                            carb += diaryEntry.details.carbs
                            fat += diaryEntry.details.fat
                        }
                        is DiaryEntry.Recipe -> {
                            cals += diaryEntry.log.totalCalories
                            prot += diaryEntry.log.totalProtein
                            carb += diaryEntry.log.totalCarbs
                            fat += diaryEntry.log.totalFat
                        }
                    }
                }
                listOf(cals, prot, carb, fat)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = headlineTopPadding,
                                bottom = headlineBottomPadding,
                            ),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Today's Summary",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
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
                            HorizontalDivider()
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
                    Button(
                        onClick = { onNavigateToCustomFood() },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    ) {
                        Text("Add Food")
                    }
                }

                item {
                    Text(
                        text = "Today's Entries",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }

                items(todaysDiaryEntries, key = { it.id }) { diaryEntry ->
                    when (diaryEntry) {
                        is DiaryEntry.Food -> {
                            FoodCard(
                                foodLog = diaryEntry.details,
                                onLongPress = {
                                    selectedEntry = diaryEntry
                                    showOptionsDialog = true
                                }
                            )
                        }
                        is DiaryEntry.Recipe -> {
                            RecipeLogCard(
                                recipeLog = diaryEntry.log,
                                onLongPress = {
                                    selectedEntry = diaryEntry
                                    showOptionsDialog = true
                                }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(96.dp)) }
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

@Composable
private fun FoodOptionsDialog(
    foodName: String,
    onDismiss: () -> Unit,
    onEditStatsClick: () -> Unit,
    onEditTimeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(foodName) },
        text = { Text("What would you like to do?") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onEditStatsClick) { Text("Edit Stats") }
                TextButton(onClick = onEditTimeClick) { Text("Edit Time") }
                TextButton(onClick = onDeleteClick) { Text("Delete") }
            }
        }
    )
}

@Composable
private fun DeleteConfirmDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Entry") },
        text = { Text("Are you sure you want to delete \"$itemName\" from your diary?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}