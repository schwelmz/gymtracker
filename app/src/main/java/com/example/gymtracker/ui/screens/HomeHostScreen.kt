package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.GoalsViewModel
import com.example.gymtracker.viewmodel.HomeViewModel

@Composable
fun HomeHostScreen(
    mainNavController: NavHostController,
    // 1. Accept all the necessary ViewModels
    homeViewModel: HomeViewModel,
    foodViewModel: FoodViewModel,
    goalsViewModel: GoalsViewModel,
    onGrantPermissionsClick: () -> Unit,
    onNavigateToWeightHistory: () -> Unit
) {
    val homeRailNavController = rememberNavController()

    Row(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            // 2. Pass the ViewModels down to the NavHost
            HomeNavHost(
                navController = homeRailNavController,
                mainNavController = mainNavController,
                homeViewModel = homeViewModel,
                foodViewModel = foodViewModel,
                goalsViewModel = goalsViewModel,
                onGrantPermissionsClick = onGrantPermissionsClick,
                onNavigateToWeightHistory = onNavigateToWeightHistory
            )
        }
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    // 3. Define the function signature with explicit types for all ViewModels
    homeViewModel: HomeViewModel,
    foodViewModel: FoodViewModel,
    goalsViewModel: GoalsViewModel,
    onGrantPermissionsClick: () -> Unit,
    onNavigateToWeightHistory: () -> Unit
) {
    NavHost(navController = navController, startDestination = AppRoutes.HOME_SCREEN) {
        composable(route = AppRoutes.HOME_SCREEN) {
            // 4. Pass all the received ViewModel instances to the HomeScreen
            HomeScreen(
                homeViewModel = homeViewModel,
                foodViewModel = foodViewModel,
                goalsViewModel = goalsViewModel,
                onGrantPermissionsClick = onGrantPermissionsClick,
                onNavigateToWeightHistory = onNavigateToWeightHistory
            )
        }
    }
}