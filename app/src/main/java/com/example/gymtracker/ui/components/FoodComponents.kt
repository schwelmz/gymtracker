package com.example.gymtracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.data.dao.FoodLogWithDetails

/**
 * A reusable composable to display a single food log entry.
 * It now handles a long press gesture to open an options dialog.
 */
@Composable
fun FoodCard(
    foodLog: FoodLogWithDetails,
    // 1. Change the parameter from onDelete to onLongPress
    onLongPress: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val cardHeight = 120.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(cardHeight)
            // 2. Use the onLongPress lambda here
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                )
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val imageWidth = 95.dp
            if (!foodLog.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = foodLog.imageUrl,
                    contentDescription = foodLog.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = foodLog.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                        .padding(16.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = foodLog.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Weight: ${foodLog.grams}g", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Calories: ${foodLog.calories}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}