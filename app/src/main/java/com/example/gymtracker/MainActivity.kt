package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.data.HealthConnectManager
import com.example.gymtracker.ui.AppNavigation
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.BottomBarDestination
import com.example.gymtracker.ui.components.AppBottomNavigationBar
import com.example.gymtracker.ui.theme.AppTheme
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
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val healthConnectManager = remember { HealthConnectManager(context) }

    val requestPermissionsLauncher =
        rememberLauncherForActivityResult(healthConnectManager.requestPermissionsContract()) {
            homeViewModel.checkAvailabilityAndPermissions()
        }

    val onGrantPermissionsClick: () -> Unit = {
        requestPermissionsLauncher.launch(homeViewModel.permissions)
    }


    // --- END OF HEALTH CONNECT LOGIC ---


    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = navController,
                destinations = listOf(
                    BottomBarDestination.Home,
                    BottomBarDestination.Workout,
                    BottomBarDestination.Nutrition,
                    BottomBarDestination.Settings
                )
            )
        },
        floatingActionButton = {
            // Show FAB for the Workout screen
            if (currentRoute == AppRoutes.WORKOUT_SCREEN) {
                FloatingActionButton(onClick = { navController.navigate(AppRoutes.ADD_WORKOUT_SCREEN) }) {
                    Box(contentAlignment = Alignment.Center) {
                        // Layer 1: The background icon
                        Icon(
                            painter = painterResource(id = R.drawable.dumbell_icon),
                            contentDescription = "Add Workout", // Accessibility description
                            modifier = Modifier.size(42.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            // Show FAB for the Nutrition screen with the custom icon and text
            if (currentRoute == AppRoutes.NUTRITION_SCREEN) {
                FloatingActionButton(
                    onClick = {
                        // Navigate to the scanner screen and pass 'true' to open the camera
                        val route = AppRoutes.FOOD_SCANNER_SCREEN.replace("{open_camera}", "true")
                        navController.navigate(route)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ean_icon),
                        contentDescription = "Scan Food Item",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation(
                    navController = navController,
                    onGrantPermissionsClick = onGrantPermissionsClick
                )
            }
        }
    }
}