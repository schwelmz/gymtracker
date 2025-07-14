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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.FoodLogWithDetails // <-- IMPORT THE CORRECT CLASS
import com.example.gymtracker.viewmodel.FoodScannerViewModel
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

@Composable
fun NutritionHostScreen(mainNavController: NavHostController) {
    val nutritionRailNavController = rememberNavController()

    Row(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NutritionNavHost(navController = nutritionRailNavController, mainNavController = mainNavController)
        }
    }
}

@Composable
fun NutritionNavHost(navController: NavHostController, mainNavController: NavHostController) {
    val scope = rememberCoroutineScope()
    // --- IMPORTANT: Assume you create your FoodViewModel at a higher level (like MainActivity)
    // and pass it down. If you create it here with viewModel(), you might get different instances.
    // For this fix, I'll keep viewModel() as is, but this is a point for future refactoring.
    val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory)

    NavHost(navController = navController, startDestination = AppRoutes.NUTRITION_SCREEN) {
        composable(route = AppRoutes.NUTRITION_SCREEN) {
            NutritionScreen(
                viewModel = foodViewModel,
                // --- THIS IS THE CORRECTED SECTION ---
                // The lambda now accepts the new `FoodLogWithDetails` object
                onDeleteFoodEntry = { foodLog ->
                    scope.launch {
                        // We call the new `deleteFoodLog` function with the log's ID
                        foodViewModel.deleteFoodLog(foodLog.logId)
                    }
                },
                onNavigateToDiary = { navController.navigate(AppRoutes.FOOD_DIARY_SCREEN) },
                onNavigateToScanner = { navController.navigate(AppRoutes.FOOD_SCANNER_SCREEN) },
                onNavigateToCustomFood = { navController.navigate(AppRoutes.CUSTOM_FOOD_LIST_SCREEN) }
            )
        }
        composable(route = AppRoutes.FOOD_SCANNER_SCREEN) {
            // Re-use the same foodViewModel instance for consistency
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
            FoodDiaryScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }
        composable(route = AppRoutes.CUSTOM_FOOD_LIST_SCREEN) {
            CustomFoodListScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() },
                onNavigateToAddCustomFood = { navController.navigate(AppRoutes.ADD_CUSTOM_FOOD_SCREEN) }
            )
        }
        composable(route = AppRoutes.ADD_CUSTOM_FOOD_SCREEN) {
            AddCustomFoodScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}