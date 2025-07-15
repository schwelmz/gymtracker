package com.example.gymtracker.ui.navigation // Or wherever your file is located

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.gymtracker.R // <-- IMPORTANT: Make sure this is imported

sealed class BottomBarDestination(
    val route: String,
    val title: String,
    // Make these nullable so we can provide one or the other
    val icon: ImageVector? = null,
    val iconResId: Int? = null
) {
    object Home : BottomBarDestination(
        route = "home_graph",
        title = "Home",
        // Use the named 'icon' parameter
        icon = Icons.Outlined.Home
    )

    object Workout : BottomBarDestination(
        route = "workout_graph",
        title = "Workout",
        // Use the new 'iconResId' parameter for your custom drawable
        iconResId = R.drawable.watterbottle_icon
    )

    object Nutrition : BottomBarDestination(
        route = "nutrition_graph",
        title = "Nutrition",
        iconResId = R.drawable.nutrition_icon
    )
    object Settings : BottomBarDestination(
        route = "settings_graph",
        title = "Settings",
        icon = Icons.Outlined.Menu
    )
}