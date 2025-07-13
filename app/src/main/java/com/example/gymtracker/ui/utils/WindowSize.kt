package com.example.gymtracker.ui.utils

import android.annotation.SuppressLint
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Enum to represent the different navigation types
enum class NavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun rememberNavigationType(): NavigationType {
    val windowSizeClass = calculateWindowSizeClass(activity = LocalContext.current as android.app.Activity)
    return NavigationType.NAVIGATION_RAIL
}