package com.example.gymtracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home

import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomBarDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarDestination(
        route = "home_graph",
        title = "Home",
        icon = Icons.Outlined.Home
    )

//    object Exercises : BottomBarDestination(
//        route = "exercises_graph",
//        title = "Exercises",
//        icon = Icons.AutoMirrored.Filled.List
//    )

    // --- ADD THESE TWO OBJECTS IF THEY ARE MISSING ---

    object Workout : BottomBarDestination(
        route = "workout_graph",
        title = "Workout",
        icon = Icons.Outlined.Star
    )

    object Nutrition : BottomBarDestination(
        route = "nutrition_graph",
        title = "Nutrition",
        icon = Icons.Outlined.FavoriteBorder
    )
    object Settings : BottomBarDestination(
        "settings_graph",
        "Settings",
        icon = Icons.Outlined.Menu)
}