package com.example.gymtracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodLogDialog(
    initialGrams: Int,
    initialCalories: Int,
    initialProtein: Int,
    initialCarbs: Int,
    initialFat: Int,
    onDismiss: () -> Unit,
    onSave: (newGrams: Int, newCalories: Int, newProtein: Int, newCarbs: Int, newFat: Int) -> Unit
) {
    var grams by remember { mutableStateOf(initialGrams.toString()) }
    var calories by remember { mutableStateOf(initialCalories.toString()) }
    var protein by remember { mutableStateOf(initialProtein.toString()) }
    var carbs by remember { mutableStateOf(initialCarbs.toString()) }
    var fat by remember { mutableStateOf(initialFat.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Nutritional Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = grams,
                    onValueChange = { grams = it.filter { c -> c.isDigit() } },
                    label = { Text("Grams") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter { c -> c.isDigit() } },
                    label = { Text("Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it.filter { c -> c.isDigit() } },
                    label = { Text("Protein (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it.filter { c -> c.isDigit() } },
                    label = { Text("Carbs (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it.filter { c -> c.isDigit() } },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val g = grams.toIntOrNull()
                    val c = calories.toIntOrNull()
                    val p = protein.toIntOrNull()
                    val carb = carbs.toIntOrNull()
                    val f = fat.toIntOrNull()

                    if (g != null && c != null && p != null && carb != null && f != null) {
                        onSave(g, c, p, carb, f)
                    }
                },
                enabled = listOf(grams, calories, protein, carbs, fat).all { it.isNotBlank() }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}