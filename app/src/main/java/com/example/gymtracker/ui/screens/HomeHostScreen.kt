package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.HomeViewModel
import com.example.gymtracker.viewmodel.FoodViewModel

@Composable
fun HomeHostScreen(mainNavController: NavHostController, onGrantPermissionsClick: () -> Unit) {
    val homeRailNavController = rememberNavController()

    val homeNavItems = listOf(
        RailNavItem(id = "overview", title = "Overview", route = AppRoutes.HOME_SCREEN)
    )

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = homeNavItems,
            selectedItemId = homeRailNavController.currentDestination?.route ?: AppRoutes.HOME_SCREEN,
            onItemSelected = { route -> homeRailNavController.navigate(route) }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeNavHost(navController = homeRailNavController, mainNavController = mainNavController, onGrantPermissionsClick = onGrantPermissionsClick)
        }
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    onGrantPermissionsClick: () -> Unit
) {
    NavHost(navController = navController, startDestination = AppRoutes.HOME_SCREEN) {
        composable(route = AppRoutes.HOME_SCREEN) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory)

            HomeScreen(
                homeViewModel = homeViewModel,
                foodViewModel = foodViewModel,
                onGrantPermissionsClick = onGrantPermissionsClick
            )
        }
    }
}