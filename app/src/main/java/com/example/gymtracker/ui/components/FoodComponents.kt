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
    onLongPress: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hasImage = !foodLog.imageUrl.isNullOrBlank()

    // Dynamic card height based on whether an image is available
    val cardHeight = if (hasImage) 80.dp else 60.dp
    val imageWidth = if (hasImage) 70.dp else 0.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .height(cardHeight)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasImage) {
                AsyncImage(
                    model = foodLog.imageUrl,
                    contentDescription = foodLog.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .fillMaxHeight()
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = if (hasImage) 12.dp else 16.dp, end = 12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = foodLog.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = "Weight: ${foodLog.grams}g",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Calories: ${foodLog.calories}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
