package com.example.gymtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodLogDialog(
    initialGrams: Float,
    initialCalories: Float,
    initialProtein: Float,
    initialCarbs: Float,
    initialFat: Float,
    onDismiss: () -> Unit,
    onSave: (newGrams: Float, newCalories:Float, newProtein: Float, newCarbs: Float, newFat:Float) -> Unit
) {
    var grams by remember { mutableStateOf(initialGrams.toString()) }
    var calories by remember { mutableStateOf(initialCalories.toString()) }
    var protein by remember { mutableStateOf(initialProtein.toString()) }
    var carbs by remember { mutableStateOf(initialCarbs.toString()) }
    var fat by remember { mutableStateOf(initialFat.toString()) }

    val isFormValid = listOf(grams, calories, protein, carbs, fat).all {
        it.isNotBlank() && it.toFloatOrNull() != null
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Grams Input
                OutlinedTextField(
                    value = grams,
                    onValueChange = {
                        val cleaned = it.replace(',', '.')
                            .filterIndexed { index, c ->
                                c.isDigit() || (c == '.' && !it.take(index).contains('.'))
                            }
                        grams = cleaned
                    },

                    label = { Text("Weight") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.scale),
                            contentDescription = null
                        )
                    },
                    suffix = { Text("g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Macro Inputs
                MacroInputRow(
                    label = "Calories",
                    value = calories,
                    onValueChange = { calories = it },
                    iconRes = R.drawable.fireplace,
                    suffix = "kcal"
                )
                MacroInputRow(
                    label = "Protein",
                    value = protein,
                    onValueChange = { protein = it },
                    iconRes = R.drawable.egg_icon,
                    suffix = "g"
                )
                MacroInputRow(
                    label = "Carbs",
                    value = carbs,
                    onValueChange = { carbs = it },
                    iconRes = R.drawable.carbs,
                    suffix = "g"
                )
                MacroInputRow(
                    label = "Fat",
                    value = fat,
                    onValueChange = { fat = it },
                    iconRes = R.drawable.oildrop,
                    suffix = "g"
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onSave(
                        grams.toFloat(),
                        calories.toFloat(),
                        protein.toFloat(),
                        carbs.toFloat(),
                        fat.toFloat()
                    )
                },
                enabled = isFormValid,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss,colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun FoodOptionsDialog(
    foodName: String,
    onDismiss: () -> Unit,
    onEditStatsClick: () -> Unit,
    onEditTimeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = foodName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "What would you like to do?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Divider()

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditStatsClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Stats")
                    }

                    OutlinedButton(
                        onClick = onEditTimeClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Time")
                    }

                    OutlinedButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MacroInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconRes: Int,
    suffix: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            val cleaned = it.replace(',', '.')
                .filterIndexed { index, c ->
                    c.isDigit() || (c == '.' && !it.take(index).contains('.'))
                }
            onValueChange(cleaned)
        },
        label = { Text(label) },
        leadingIcon = {
            Icon(painter = painterResource(id = iconRes), contentDescription = null)
        },
        suffix = { Text(suffix) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
