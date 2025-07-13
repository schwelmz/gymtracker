package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.example.gymtracker.viewmodel.FoodViewModel
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
    // --- VIEWMODEL CREATION ---
    // Create ViewModels once at the top-level of the app using their factories.
    // This ensures they are shared across all screens and prevents crashes.
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    // You will need to create a FoodViewModel.Factory similar to HomeViewModel.Factory
    val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory)

    // --- HEALTH CONNECT PERMISSION HANDLING ---
    val context = LocalContext.current
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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Workout",
                    )
                }
            }
            // Show FAB for the Nutrition screen
            if (currentRoute == AppRoutes.NUTRITION_SCREEN) {
                FloatingActionButton(onClick = { navController.navigate(AppRoutes.FOOD_SCANNER_SCREEN) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ean_icon),
                        contentDescription = "Scan Food Item",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                // Use the innerPadding from the Scaffold for proper layout
                .padding(innerPadding)
        ) {
            // --- CORRECTED NAVIGATION CALL ---
            // Pass the single instances of the ViewModels and the modifier to the navigation host.
            AppNavigation(
                modifier = Modifier, // You can pass the surface modifier here if needed
                navController = navController,
                onGrantPermissionsClick = onGrantPermissionsClick,
                homeViewModel = homeViewModel,
                foodViewModel = foodViewModel
            )
        }
    }
}