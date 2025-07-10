package com.example.gymtracker.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.BottomBarDestination
import kotlin.collections.forEach

@Composable
fun AppBottomNavigationBar(
    navController: NavHostController,
    destinations: List<BottomBarDestination>
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentGraphRoute = navBackStackEntry?.destination?.parent?.route
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentGraphRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = { Text(destination.title) }
            )
        }
    }
}

@Preview
@Composable
fun AppBottomNavigationBarPreview() {
    // We need a NavController for the preview, rememberNavController() is a simple way to get one.
    val navController = rememberNavController()

    // We can use a 'remember' state here to simulate clicking on items in the preview.
    var currentRoute by remember { mutableStateOf(BottomBarDestination.Home.route) }

    // This is a simplified onClick logic just for the preview to work.
    val previewOnClick: (String) -> Unit = { route ->
        currentRoute = route
    }

    // We wrap our component in a simple container to give it context.
    NavigationBar {
        val destinations = listOf(BottomBarDestination.Home, BottomBarDestination.Gym)
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { previewOnClick(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.title) }
            )
        }
    }
}