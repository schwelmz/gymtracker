package com.example.gymtracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.FavoriteBorder


sealed class BottomBarDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarDestination(
        route = "home_graph",
        title = "Home",
        icon = Icons.Filled.Home
    )

    object Exercises : BottomBarDestination(
        route = "exercises_graph",
        title = "Exercises",
        icon = Icons.AutoMirrored.Filled.List
    )

    // --- ADD THESE TWO OBJECTS IF THEY ARE MISSING ---

    object Workout : BottomBarDestination(
        route = "workout_graph",
        title = "Workout",
        icon = Icons.Filled.Star
    )

    object Scanner : BottomBarDestination(
        route = "scanner_graph",
        title = "Scanner",
        icon = Icons.Filled.FavoriteBorder
    )
}