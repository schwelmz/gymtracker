package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.viewmodel.FoodScannerViewModel
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.GoalsViewModel
import kotlinx.coroutines.launch

@Composable
fun NutritionHostScreen(
    mainNavController: NavHostController,
    // 1. Accept both required ViewModels
    foodViewModel: FoodViewModel,
    goalsViewModel: GoalsViewModel
) {
    val nutritionRailNavController = rememberNavController()

    Row(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            // 2. Pass them down to the NavHost
            NutritionNavHost(
                navController = nutritionRailNavController,
                mainNavController = mainNavController,
                foodViewModel = foodViewModel,
                goalsViewModel = goalsViewModel
            )
        }
    }
}

@Composable
fun NutritionNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    // 3. Accept both ViewModels with explicit types
    foodViewModel: FoodViewModel,
    goalsViewModel: GoalsViewModel
) {
    val scope = rememberCoroutineScope()

    // 4. Collect the goals state here to be used by child screens
    val goalsState by goalsViewModel.uiState.collectAsState()
    val calorieGoal = goalsState.calorieGoal

    NavHost(navController = navController, startDestination = AppRoutes.NUTRITION_SCREEN) {
        composable(route = AppRoutes.NUTRITION_SCREEN) {
            NutritionScreen(
                viewModel = foodViewModel,
                onNavigateToDiary = { navController.navigate(AppRoutes.FOOD_DIARY_SCREEN) },
                onNavigateToScanner = { navController.navigate(AppRoutes.FOOD_SCANNER_SCREEN) },
                onNavigateToCustomFood = { navController.navigate(AppRoutes.CUSTOM_FOOD_LIST_SCREEN) }
            )
        }
        composable(route = AppRoutes.FOOD_SCANNER_SCREEN) {
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
            // 5. Pass the collected calorieGoal to the FoodDiaryScreen
            FoodDiaryScreen(
                viewModel = foodViewModel,
                onNavigateUp = { navController.popBackStack() },
                calorieGoal = calorieGoal
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