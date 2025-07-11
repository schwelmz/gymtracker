package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.ui.AppNavigation
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.BottomBarDestination
import com.example.gymtracker.ui.components.AppBottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                GymApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymApp() {
    val navController = rememberNavController()
    // Observe the back stack to determine the current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                destinations = listOf(
                    BottomBarDestination.Home,
                    BottomBarDestination.Exercises
                )
            )
        },
        floatingActionButton = {
            // Show FAB only on the home screen
            val currentGraphRoute = currentBackStackEntry?.destination?.parent?.route
            if (currentRoute == AppRoutes.HOME_SCREEN) {
                FloatingActionButton(onClick = { navController.navigate(AppRoutes.ADD_WORKOUT_SCREEN) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Workout")
                }
            }
        }

    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(
                navController = navController)
        }
    }
}