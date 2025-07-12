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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.HealthConnectManager
import com.example.gymtracker.viewmodel.HomeViewModel

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
    // --- HEALTH CONNECT PERMISSION HANDLING ---
    // Get the ViewModel and the HealthConnectManager
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val healthConnectManager = remember { HealthConnectManager(context) }

    // Create the launcher to request Health Connect permissions
    val requestPermissionsLauncher =
        rememberLauncherForActivityResult(healthConnectManager.requestPermissionsContract()) {
            // After the user responds, check permissions again to update the UI
            homeViewModel.checkAvailabilityAndPermissions()
        }

    // This is the function that will be passed down to the HomeScreen's button
    val onGrantPermissionsClick: () -> Unit = {
        requestPermissionsLauncher.launch(homeViewModel.permissions)
    }
    // --- END OF HEALTH CONNECT LOGIC ---


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
                    BottomBarDestination.Workout,
                    BottomBarDestination.Scanner,
                    BottomBarDestination.Exercises,
                    BottomBarDestination.Settings
                )
            )
        },
        floatingActionButton = {
            // Show FAB only on the home screen
            if (currentRoute == AppRoutes.WORKOUT_SCREEN) {
                FloatingActionButton(onClick = { navController.navigate(AppRoutes.ADD_WORKOUT_SCREEN) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Workout")
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Apply padding from the Scaffold
            color = MaterialTheme.colorScheme.background
        ) {
            // AppNavigation is now the main content and receives the permission handler
            AppNavigation(
                navController = navController,
                onGrantPermissionsClick = onGrantPermissionsClick
            )
        }
    }
}