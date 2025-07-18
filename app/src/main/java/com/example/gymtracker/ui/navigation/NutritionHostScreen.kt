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

    LaunchedEffect(pagerState.currentPage) {
    }

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = nutritionNavItems,
            selectedItemId = nutritionNavItems[pagerState.currentPage].id,
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
                val goalsState by goalsViewModel.userGoals.collectAsState()
                val calorieGoal = goalsState.calorieGoal
                val calorieMode = goalsState.calorieMode

                when (nutritionNavItems[page].route) {
                    AppRoutes.NUTRITION_SCREEN -> {
                        // --- THIS IS THE CHANGE ---
                        NutritionScreen(
                            viewModel = foodViewModel,
                            onNavigateToCustomFood = { mainNavController.navigate(AppRoutes.CUSTOM_FOOD_LIST_SCREEN) },
                            // Pass the goals to the screen
                            calorieGoal = calorieGoal,
                            calorieMode = calorieMode
                        )
                        // --- END OF CHANGE ---
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
                                mainNavController.navigate(
                                    AppRoutes.RECIPE_ADD_EDIT_SCREEN.replace("{recipeId}", "-1")
                                )
                            },
                            onNavigateToRecipeDetails = { recipeId ->
                                mainNavController.navigate(
                                    AppRoutes.RECIPE_ADD_EDIT_SCREEN.replace("{recipeId}", recipeId.toString())
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