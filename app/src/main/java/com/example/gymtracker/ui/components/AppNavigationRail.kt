// In app/src/main/java/com/example/gymtracker/ui/components/AppNavigationRail.kt

package com.example.gymtracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AppNavigationRail(
    items: List<RailNavItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .width(50.dp), // A narrow width for the rail itself
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        items.forEach { item ->
            val isSelected = item.id == selectedItemId
            val indicatorColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                label = "indicatorColor"
            )
            val textColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                label = "textColor"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onItemSelected(item.route) },
                contentAlignment = Alignment.Center
            ) {
                RotatedPill(backgroundColor = indicatorColor) {
                    Text(
                        text = item.title,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

// The RotatedPill helper composable remains the same.
@Composable
private fun RotatedPill(
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints.copy(maxWidth = Int.MAX_VALUE))
        layout(width = placeable.height, height = placeable.width) {
            placeable.placeWithLayer(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            ) {
                rotationZ = -90f
            }
        }
    }
}