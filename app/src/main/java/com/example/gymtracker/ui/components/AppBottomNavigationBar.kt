package com.example.gymtracker.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource // <-- ADD THIS IMPORT
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymtracker.ui.BottomBarDestination

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    destinations: List<BottomBarDestination>
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.parent?.route

        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        // Pop up to the start destination of the graph to avoid building up a large
                        // back stack as users select items
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    // --- THIS IS THE CRITICAL CHANGE ---
                    // Check which type of icon to display
                    if (destination.icon != null) {
                        // Display the built-in ImageVector icon
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.title
                        )
                    } else if (destination.iconResId != null) {
                        // Display the custom XML drawable icon
                        Icon(
                            painter = painterResource(id = destination.iconResId),
                            contentDescription = destination.title
                        )
                    }
                },
                label = { Text(destination.title) }
            )
        }
    }
}