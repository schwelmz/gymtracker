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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.FoodScannerViewModel
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

@Composable
fun NutritionHostScreen(mainNavController: NavHostController) {
    val nutritionRailNavController = rememberNavController()

    val nutritionNavItems = listOf(
        RailNavItem(id = "diary", title = "Food Diary", route = AppRoutes.FOOD_DIARY_SCREEN),
        RailNavItem(id = "scanner", title = "Food Scanner", route = AppRoutes.FOOD_SCANNER_SCREEN),
        RailNavItem(id = "custom_food", title = "Custom Foods", route = AppRoutes.CUSTOM_FOOD_LIST_SCREEN)
    )

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = nutritionNavItems,
            selectedItemId = nutritionRailNavController.currentDestination?.route ?: AppRoutes.FOOD_DIARY_SCREEN,
            onItemSelected = { route -> nutritionRailNavController.navigate(route) }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            NutritionNavHost(navController = nutritionRailNavController, mainNavController = mainNavController)
        }
    }
}

@Composable
fun NutritionNavHost(navController: NavHostController, mainNavController: NavHostController) {
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = AppRoutes.FOOD_DIARY_SCREEN) {
        composable(route = AppRoutes.NUTRITION_SCREEN) { // This will be the default for the rail
            val foodViewModel: FoodViewModel = viewModel()
            NutritionScreen(
                viewModel = foodViewModel,
                onDeleteFoodEntry = { food ->
                    scope.launch {
                        foodViewModel.deleteFood(food)
                    }
                },
                onNavigateToDiary = { navController.navigate(AppRoutes.FOOD_DIARY_SCREEN) },
                onNavigateToScanner = { navController.navigate(AppRoutes.FOOD_SCANNER_SCREEN) },
                onNavigateToCustomFood = { navController.navigate(AppRoutes.CUSTOM_FOOD_LIST_SCREEN) }
            )
        }
        composable(route = AppRoutes.FOOD_SCANNER_SCREEN) {
            val foodViewModel: FoodViewModel = viewModel()
            val scannerViewModel: FoodScannerViewModel = viewModel()

            FoodScannerScreen(
                foodViewModel = foodViewModel,
                scannerViewModel = scannerViewModel,
                onSave = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = AppRoutes.FOOD_DIARY_SCREEN) {
            val foodViewModel: FoodViewModel = viewModel()

            FoodDiaryScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }
        composable(route = AppRoutes.CUSTOM_FOOD_LIST_SCREEN) {
            val foodViewModel: FoodViewModel = viewModel()
            CustomFoodListScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() },
                onNavigateToAddCustomFood = { navController.navigate(AppRoutes.ADD_CUSTOM_FOOD_SCREEN) }
            )
        }

        composable(route = AppRoutes.ADD_CUSTOM_FOOD_SCREEN) {
            val foodViewModel: FoodViewModel = viewModel()
            AddCustomFoodScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}