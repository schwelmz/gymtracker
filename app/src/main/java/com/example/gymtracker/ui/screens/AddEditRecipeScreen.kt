package com.example.gymtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.FoodTemplate
import com.example.gymtracker.data.IngredientDetails
import com.example.gymtracker.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: Int,
    recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory),
    onNavigateUp: () -> Unit
) {
    val isEditing = recipeId != -1
    val title = if (isEditing) "Edit Recipe" else "Add Recipe"

    // State for all food templates, needed for the add ingredient dialog
    val allFoodTemplates by recipeViewModel.allFoodTemplates.collectAsState(initial = emptyList())
    val allRecipes by recipeViewModel.allRecipes.collectAsState(initial = emptyList())

    // State for the UI inputs
    var name by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf<Map<FoodTemplate, Int>>(emptyMap()) }
    var showAddIngredientDialog by remember { mutableStateOf(false) }

    // This effect runs when the screen is first displayed or if recipeId changes.
    // It loads the existing recipe data when in "edit" mode.
    LaunchedEffect(allRecipes) {
        if (isEditing) {
            val existingRecipe = allRecipes.find { it.recipe.id == recipeId }
            if (existingRecipe != null) {
                name = existingRecipe.recipe.name
                instructions = existingRecipe.recipe.instructions ?: ""
                ingredients = existingRecipe.ingredients.associate { it.foodTemplate to it.grams }
            }
        }
    }

    // The save button is enabled only if the recipe has a name and at least one ingredient.
    val isSaveEnabled by remember(name, ingredients) {
        derivedStateOf { name.isNotBlank() && ingredients.isNotEmpty() }
    }

    if (showAddIngredientDialog) {
        AddIngredientDialog(
            allFoodTemplates = allFoodTemplates,
            onDismiss = { showAddIngredientDialog = false },
            onIngredientSelected = { foodTemplate, grams ->
                ingredients = ingredients + (foodTemplate to grams)
                showAddIngredientDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val existingRecipeDetails = if (isEditing) allRecipes.find { it.recipe.id == recipeId } else null
                            recipeViewModel.addOrUpdateRecipe(name, instructions, ingredients, existingRecipeDetails)
                            onNavigateUp()
                        },
                        enabled = isSaveEnabled
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recipe Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            item {
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions (Optional)") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ingredients", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { showAddIngredientDialog = true }) {
                        Text("Add")
                    }
                }
            }

            if (ingredients.isEmpty()){
                item {
                    Text(
                        text = "No ingredients added yet.",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            items(ingredients.toList(), key = { (template, _) -> template.id }) { (template, grams) ->
                IngredientListItem(
                    template = template,
                    grams = grams,
                    onRemove = { ingredients = ingredients - template }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun IngredientListItem(template: FoodTemplate, grams: Int, onRemove: () -> Unit) {
    val calories = (template.caloriesPer100g * grams) / 100
    ListItem(
        headlineContent = { Text(template.name, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text("$grams g") },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$calories kcal", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove ingredient")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientDialog(
    allFoodTemplates: List<FoodTemplate>,
    onDismiss: () -> Unit,
    onIngredientSelected: (FoodTemplate, Int) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<FoodTemplate?>(null) }
    var grams by remember { mutableStateOf("") }

    val filteredTemplates = remember(searchText, allFoodTemplates) {
        if (searchText.length >= 3) {
            allFoodTemplates.filter {
                it.name.contains(searchText, ignoreCase = true)
            }.take(4)
        } else {
            emptyList()
        }
    }

    val isConfirmEnabled = selectedTemplate != null && grams.isNotBlank() && grams.toIntOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Ingredient") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            expanded = true
                        },
                        label = { Text("Search food") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filteredTemplates.forEach { template ->
                            DropdownMenuItem(
                                text = { Text(template.name) },
                                onClick = {
                                    selectedTemplate = template
                                    searchText = template.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = grams,
                    onValueChange = { grams = it.filter { char -> char.isDigit() } },
                    label = { Text("Weight (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onIngredientSelected(selectedTemplate!!, grams.toInt())
                },
                enabled = isConfirmEnabled
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}