package com.example.gymtracker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gymtracker.data.dao.FoodLogWithDetails

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodCard(
    foodLog: FoodLogWithDetails,
    onLongPress: () -> Unit
) {
    // 1. Process the name to limit it to two words and add an ellipsis if longer
    val displayName = remember(foodLog.name) {
        val words = foodLog.name.split(" ").filter { it.isNotBlank() }
        if (words.size > 2) {
            words.take(2).joinToString(" ") + "..."
        } else {
            foodLog.name
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { /* No-op for single click */ },
                onLongClick = onLongPress
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // 2. Apply a fixed height to the Row to ensure static card size
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // A good, standard height for list items with thumbnails
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box to handle cases with and without an image, maintaining layout consistency
            Box(
                modifier = Modifier
                    .size(56.dp) // Slightly smaller image to fit within the 72dp row height
                    .clip(MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                foodLog.imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = foodLog.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Text content column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2, // Allow for two lines if the two words are long
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${foodLog.grams}g • ${foodLog.calories} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.width(16.dp))

            // Macros
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroStat(label = "P", value = foodLog.protein)
                MacroStat(label = "C", value = foodLog.carbs)
                MacroStat(label = "F", value = foodLog.fat)
            }
        }
    }
}

// MacroStat remains the same
@Composable
fun MacroStat(label: String, value: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = "${value}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}