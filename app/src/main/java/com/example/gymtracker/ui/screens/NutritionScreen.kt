package com.example.gymtracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.Food
import com.example.gymtracker.viewmodel.FoodViewModel

@Composable
fun NutritionScreen(
    viewModel: FoodViewModel,
    onDeleteFoodEntry: (Food) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val allFoodEntries by viewModel.allFood.collectAsState(initial = emptyList())
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            item {
                Text(
                    text = "Nutrition", // Changed title to be more specific
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
                )
            }
            items(allFoodEntries) { food ->
                foodCard(food = food, onDelete = { onDeleteFoodEntry(food) })
            }
        }
    }
}

@Composable
fun foodCard(
    food: Food,
    onDelete: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Exercise") },
            text = { Text("Are you sure you want to delete this exercise?") },
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
        Row {
            val imageWidth = 95.dp
            if (!food.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = food.imageUri, // Coil handles the loading
                    contentDescription = food.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = food.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = food.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Calories: ${food.calories}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}