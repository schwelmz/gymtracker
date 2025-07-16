package com.example.gymtracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons

import com.example.gymtracker.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val isFormValid = listOf(grams, calories, protein, carbs, fat).all { it.isNotBlank() && it.toIntOrNull() != null }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Grams Input
                OutlinedTextField(
                    value = grams,
                    onValueChange = { grams = it.filter { c -> c.isDigit() } },
                    label = { Text("Weight") },
                    leadingIcon = { R.drawable.scale },
                    suffix = { Text("g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Macros Inputs
                MacroInputRow(
                    label = "Calories",
                    value = calories,
                    onValueChange = { calories = it },
                    icon = {R.drawable.fireplace},
                    suffix = "kcal"
                )
                MacroInputRow(
                    label = "Protein",
                    value = protein,
                    onValueChange = { protein = it },
                    icon = { R.drawable.egg_icon},
                    suffix = "g"
                )
                MacroInputRow(
                    label = "Carbs",
                    value = carbs,
                    onValueChange = { carbs = it },
                    icon = { R.drawable.egg_icon},
                    suffix = "g"
                )
                MacroInputRow(
                    label = "Fat",
                    value = fat,
                    onValueChange = { fat = it },
                    icon = { R.drawable.oildrop},
                    suffix = "g"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        grams.toInt(),
                        calories.toInt(),
                        protein.toInt(),
                        carbs.toInt(),
                        fat.toInt()
                    )
                },
                enabled = isFormValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MacroInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable () -> Unit,
    suffix: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        icon()
        Text(text = label, modifier = Modifier.width(60.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            suffix = { Text(suffix) }
        )
    }
}