package com.example.gymtracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarDestination(AppRoutes.HOME_GRAPH, "Home", Icons.Default.Home)
    object Exercises : BottomBarDestination(AppRoutes.EXERCISES_GRAPH, "Exercises", Icons.Default.Menu)

}