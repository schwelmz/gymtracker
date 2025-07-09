package com.example.gymtracker.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymtracker.ui.screens.AddWorkoutScreen
import com.example.gymtracker.ui.screens.HomeScreen
import com.example.gymtracker.ui.screens.WorkoutLogScreen

object AppRoutes {
    const val HOME_SCREEN = "home"
    const val ADD_WORKOUT_SCREEN = "add_workout"
    const val WORKOUT_LOG_SCREEN = "workout_log/{exerciseName}"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppRoutes.HOME_SCREEN) {
        composable(AppRoutes.HOME_SCREEN) {
            HomeScreen(
                onNavigateToAddWorkout = {
                    navController.navigate(AppRoutes.ADD_WORKOUT_SCREEN)
                },
                viewModel = viewModel() // Pass the same viewmodel instance
            )
        }

        composable(AppRoutes.ADD_WORKOUT_SCREEN) {
            AddWorkoutScreen(
                onExerciseSelected = { exerciseName ->
                    navController.navigate("workout_log/$exerciseName")
                }
            )
        }

        composable(AppRoutes.WORKOUT_LOG_SCREEN) { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "Unknown"
            WorkoutLogScreen(
                exerciseName = exerciseName,
                onWorkoutSaved = {
                    // Navigate back to the home screen
                    navController.popBackStack(AppRoutes.HOME_SCREEN, inclusive = false)
                },
                viewModel = viewModel() // Pass the same viewmodel instance
            )
        }
    }
}