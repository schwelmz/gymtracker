package com.example.gymtracker.ui.screens.nutrition

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gymtracker.data.model.RecipeWithDetails
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.RecipeViewModel
import com.patrykandpatrick.vico.core.extension.sumOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory),
    foodViewModel: FoodViewModel,
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToRecipeDetails: (Int) -> Unit
) {
    val recipes by recipeViewModel.allRecipes.collectAsState(initial = emptyList())
    var recipeToDelete by remember { mutableStateOf<RecipeWithDetails?>(null) }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Your Recipes",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    OutlinedButton(
                        onClick = onNavigateToAddRecipe,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary // ✅ only content, not container
                        )

                    ) {
                        Text("Add New Recipe")
                    }
                }

                if (recipes.isEmpty()) {
                    item {
                        Text(
                            text = "No recipes found. Add your first one!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(recipes, key = { it.recipe.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToRecipeDetails(recipe.recipe.id) },
                            onUseRecipe = { usedRecipe ->
                                foodViewModel.logRecipe(usedRecipe)
                            },
                            onLongPress = {
                                recipeToDelete = recipe
                            }
                        )
                    }
                }
            }
        }

        recipeToDelete?.let { recipe ->
            AlertDialog(
                onDismissRequest = { recipeToDelete = null },
                title = { Text("Delete Recipe") },
                text = { Text("Are you sure you want to delete \"${recipe.recipe.name}\"? This cannot be undone.") },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            recipeViewModel.deleteRecipe(recipe.recipe)
                            recipeToDelete = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { recipeToDelete = null },colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    recipe: RecipeWithDetails,
    onClick: () -> Unit,
    onUseRecipe: (RecipeWithDetails) -> Unit,
    onLongPress: () -> Unit
) {
    val totalCalories = recipe.ingredients.sumOf{
        (it.foodTemplate.caloriesPer100g * it.grams) / 100
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            recipe.recipe.imageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.toUri()),
                    contentDescription = recipe.recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$totalCalories kcal • ${recipe.ingredients.size} ingredients",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                recipe.recipe.instructions?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { onUseRecipe(recipe) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary // ✅ only content, not container
                    )

                ) {
                    Text("Log this Recipe")
                }
            }
        }
    }
}