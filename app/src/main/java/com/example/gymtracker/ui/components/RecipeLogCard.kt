package com.example.gymtracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gymtracker.data.model.IngredientDetails
import com.example.gymtracker.data.model.RecipeLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeLogCard(
    recipeLog: RecipeLog,
    onLongPress: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val ingredients = remember(recipeLog.ingredientsJson) {
        val type = object : TypeToken<List<IngredientDetails>>() {}.type
        try {
            Gson().fromJson<List<IngredientDetails>>(recipeLog.ingredientsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = onLongPress
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                recipeLog.imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = recipeLog.name,
                        modifier = Modifier
                            .size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(recipeLog.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${recipeLog.totalCalories} kcal", style = MaterialTheme.typography.bodyMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroStat(label = "P", value = recipeLog.totalProtein)
                    MacroStat(label = "C", value = recipeLog.totalCarbs)
                    MacroStat(label = "F", value = recipeLog.totalFat)
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                    )
                    ingredients.forEach { ingredient ->
                        ListItem(
                            headlineContent = { Text(ingredient.foodTemplate.name, style = MaterialTheme.typography.bodyMedium) },
                            supportingContent = { Text("${ingredient.grams}g") },
                            trailingContent = {
                                val calories = (ingredient.foodTemplate.caloriesPer100g * ingredient.grams) / 100
                                Text("$calories kcal", style = MaterialTheme.typography.bodyMedium)
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}