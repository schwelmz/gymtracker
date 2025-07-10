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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.AppNavigation
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.BottomBarDestination
import com.example.gymtracker.ui.components.AppBottomNavigationBar
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.patrykandpatrick.vico.core.axis.AxisPosition

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymTrackerTheme {
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
        topBar = {
            TopAppBar(
                title = { Text("Gym Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                destinations = listOf(
                    BottomBarDestination.Home,
                    BottomBarDestination.Gym
                )
            )
        },
        floatingActionButton = {
            // Show FAB only on the home screen
            val currentGraphRoute = currentBackStackEntry?.destination?.parent?.route
            if (currentGraphRoute == BottomBarDestination.Home.route) {
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GymTrackerTheme {
        GymApp()
    }
}