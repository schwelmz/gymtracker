package com.example.gymtracker.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import com.example.gymtracker.ui.screens.nutrition.FoodDiaryScreen
import com.example.gymtracker.ui.screens.nutrition.NutritionScreen
import com.example.gymtracker.ui.screens.nutrition.RecipesScreen
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.GoalsViewModel
import com.example.gymtracker.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

@Composable
fun NutritionHostScreen(
    mainNavController: NavHostController,
    // 1. Accept both required ViewModels
    foodViewModel: FoodViewModel,
    recipeViewModel: RecipeViewModel,
    goalsViewModel: GoalsViewModel
) {
    val nutritionNavItems = listOf(
        RailNavItem(id = "today", title = "Today", route = AppRoutes.NUTRITION_SCREEN),
        RailNavItem(id = "diary", title = "Diary", route = AppRoutes.FOOD_DIARY_SCREEN),
        RailNavItem(id = "recipes", title = "Recipes", route = AppRoutes.RECIPE_SCREEN)
    )
    val pagerState = rememberPagerState { nutritionNavItems.size }
    val scope = rememberCoroutineScope()

    // Synchronize pager state with rail selection
    LaunchedEffect(pagerState.currentPage) {
    }

    //val nutritionRailNavController = rememberNavController()

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = nutritionNavItems,
            selectedItemId = nutritionNavItems[pagerState.currentPage].id, // Use id for selection
            onItemSelected = { route ->
                val index = nutritionNavItems.indexOfFirst { it.route == route }
                if (index != -1) {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
//                val foodViewModel: FoodViewModel = viewModel()
//                val goalsViewModel: GoalsViewModel = viewModel()
                val goalsState by goalsViewModel.uiState.collectAsState()
                val calorieGoal = goalsState.calorieGoal
                val calorieMode = goalsState.calorieMode

                when (nutritionNavItems[page].route) {
                    AppRoutes.NUTRITION_SCREEN -> {
                        NutritionScreen(
                            viewModel = foodViewModel,
//                            onNavigateToDiary = { mainNavController.navigate(AppRoutes.FOOD_DIARY_SCREEN) },
//                            onNavigateToScanner = { mainNavController.navigate(AppRoutes.FOOD_SCANNER_SCREEN) },
                            onNavigateToCustomFood = { mainNavController.navigate(AppRoutes.CUSTOM_FOOD_LIST_SCREEN) }
                        )
                    }

                    AppRoutes.FOOD_DIARY_SCREEN -> {
                        FoodDiaryScreen(
                            viewModel = foodViewModel,
                            onNavigateUp = { mainNavController.popBackStack() },
                            calorieGoal = calorieGoal,
                            calorieMode = calorieMode
                        )
                    }

                    AppRoutes.RECIPE_SCREEN -> {
                        RecipesScreen(
                            recipeViewModel = recipeViewModel,
                            onNavigateToAddRecipe = {
                                // Navigate to the add screen without an ID
                                mainNavController.navigate(
                                    AppRoutes.RECIPE_ADD_EDIT_SCREEN.replace(
                                        "{recipeId}",
                                        "-1"
                                    )
                                )
                            },
                            onNavigateToRecipeDetails = { recipeId ->
                                // --- THIS IS THE CORRECTED NAVIGATION LOGIC ---
                                // Navigate to the edit screen with the recipe's ID
                                mainNavController.navigate(
                                    AppRoutes.RECIPE_ADD_EDIT_SCREEN.replace(
                                        "{recipeId}",
                                        recipeId.toString()
                                    )
                                )

                            },
                            foodViewModel = foodViewModel
                        )
                    }
                }
            }
        }
    }
}

