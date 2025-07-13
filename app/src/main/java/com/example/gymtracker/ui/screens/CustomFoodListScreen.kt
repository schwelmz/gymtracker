package com.example.gymtracker.ui.screens

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
import com.example.gymtracker.data.CustomFood
import com.example.gymtracker.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFoodListScreen(
    viewModel: FoodViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToAddCustomFood: () -> Unit
) {
    // --- STATE MANAGEMENT ---
    val allCustomFoods by viewModel.allCustomFoods.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    val filteredFoods = remember(searchQuery, allCustomFoods) {
        if (searchQuery.isBlank()) {
            allCustomFoods
        } else {
            allCustomFoods.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var showLogDialog by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf<CustomFood?>(null) }
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
                            viewModel.addCustomFoodEntry(selectedFood!!, gramsInt)
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
        // --- THIS IS THE CORRECTED SECTION ---
        floatingActionButton = {
            // Wrap the FAB in a Box and apply an offset.
            // A negative 'y' value moves the button up.
            Box(
                modifier = Modifier.offset(y = (-52).dp) // Adjust this value as needed
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
                items(filteredFoods) { food ->
                    ListItem(
                        headlineContent = { Text(food.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("${food.caloriesPer100g} kcal per 100g") },
                        modifier = Modifier.clickable {
                            selectedFood = food
                            grams = "100" // Default to 100g
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