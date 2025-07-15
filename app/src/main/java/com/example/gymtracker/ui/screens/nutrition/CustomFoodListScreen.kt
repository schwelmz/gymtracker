package com.example.gymtracker.ui.screens.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.model.FoodTemplate // <-- IMPORT THE CORRECT CLASS
import com.example.gymtracker.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFoodListScreen(
    viewModel: FoodViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToAddCustomFood: () -> Unit
) {
    // --- 1. STATE MANAGEMENT (CORRECTED) ---
    // Use the new `allFoodTemplates` Flow from the ViewModel
    val allFoodTemplates by viewModel.allFoodTemplates.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    val filteredFoods = remember(searchQuery, allFoodTemplates) {
        if (searchQuery.isBlank()) {
            allFoodTemplates
        } else {
            allFoodTemplates.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var showLogDialog by remember { mutableStateOf(false) }
    // Use the new FoodTemplate class for the selected food state
    var selectedFood by remember { mutableStateOf<FoodTemplate?>(null) }
    var grams by remember { mutableStateOf("") }

    if (showLogDialog && selectedFood != null) {
        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Log ${selectedFood?.name}") },
            text = {
                OutlinedTextField(
                    value = grams,
                    onValueChange = { grams = it.filter { char -> char.isDigit() } },
                    label = { Text("Enter weight (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val gramsInt = grams.toIntOrNull()
                        if (gramsInt != null) {
                            // Call the new, correct function in the ViewModel
                            viewModel.logFood(selectedFood!!, gramsInt)
                            showLogDialog = false
                            onNavigateUp() // Go back after logging the food
                        }
                    },
                    enabled = grams.isNotBlank()
                ) {
                    Text("Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Foods") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                //modifier = Modifier.offset(y = (-50).dp)
            ) {
                FloatingActionButton(
                    onClick = onNavigateToAddCustomFood,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add new custom food")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Foods or add your Food '+'") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // List of foods
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // --- 3. DISPLAY FILTERED LIST (CORRECTED) ---
                // Iterate over the list of FoodTemplates
                items(filteredFoods) { foodTemplate ->
                    ListItem(
                        headlineContent = { Text(foodTemplate.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("${foodTemplate.caloriesPer100g} kcal per 100g") },
                        modifier = Modifier.clickable {
                            selectedFood = foodTemplate // Set the selected FoodTemplate
                            grams = "100"
                            showLogDialog = true
                        }
                    )
                    HorizontalDivider()
                }

                // Spacer at the end so the FAB doesn't hide the last item
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}