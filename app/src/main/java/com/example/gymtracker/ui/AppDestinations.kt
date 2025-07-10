package com.example.gymtracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarDestination(AppRoutes.HOME_GRAPH, "Home", Icons.Default.Home)
    object Gym : BottomBarDestination(AppRoutes.GYM_GRAPH, "Gym", Icons.Default.Build)

}