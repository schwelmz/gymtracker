package com.example.gymtracker.ui

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.gymtracker.ui.screens.AddWorkoutScreen
import com.example.gymtracker.ui.screens.GymScreen
import com.example.gymtracker.ui.screens.HomeScreen
import com.example.gymtracker.ui.screens.StatsScreen
import com.example.gymtracker.ui.screens.WorkoutLogScreen
import com.example.gymtracker.viewmodel.WorkoutViewModel

object AppRoutes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"
    const val GYM_GRAPH = "gym_graph"
    const val GYM_SCREEN = "gym_screen"
    const val ADD_WORKOUT_SCREEN = "add_workout"
    const val WORKOUT_LOG_SCREEN = "workout_log/{exerciseName}"
    const val STATS_SCREEN = "stats/{exerciseName}"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    Log.d("AppNavigation", "Home route for NavHost: ${BottomBarDestination.Home.route}") // Add this
    Log.d("AppNavigation", "Gym route for NavHost: ${BottomBarDestination.Gym.route}")   // Add this

    // The start destination is now the ROUTE OF THE GRAPH for the home tab.
    NavHost(
        navController = navController,
        startDestination = BottomBarDestination.Home.route,
        modifier = modifier
    ) {
        // =====================================================================
        // HOME NAVIGATION GRAPH
        // =====================================================================
        // The 'route' here is what the bottom nav bar navigates to for this tab.
        // The 'startDestination' is the first screen WITHIN this tab's graph.
        navigation(
            startDestination = AppRoutes.HOME_SCREEN,
            route = BottomBarDestination.Home.route // "home_screen"
        ) {
            // These are all the screens accessible from the Home tab.

            // The main screen for the Home tab
            composable(route = AppRoutes.HOME_SCREEN) {
                val viewModel: WorkoutViewModel = viewModel()
                val sessions by viewModel.allSessions.collectAsState(initial = emptyList())

                HomeScreen(
                    sessions = sessions,
                    onNavigateToAddWorkout = { navController.navigate(AppRoutes.ADD_WORKOUT_SCREEN) },
                    onSessionClicked = { exerciseName ->
                        navController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                    },
                    onDeleteSession = { session ->
                        viewModel.deleteSession(session)
                    }
                )
            }

            // Screens you navigate to FROM the home screen
            composable(route = AppRoutes.ADD_WORKOUT_SCREEN) {
                AddWorkoutScreen(
                    onExerciseSelected = { exerciseName ->
                        navController.navigate("workout_log/$exerciseName")
                    }
                )
            }

            composable(route = AppRoutes.WORKOUT_LOG_SCREEN) { backStackEntry ->
                val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "Unknown"
                // 1. Get the ViewModel and collect state here
                val viewModel: WorkoutViewModel = viewModel()
                val sets by viewModel.currentSets.collectAsState()
                WorkoutLogScreen(
                    exerciseName = exerciseName,
                    sets = sets,
                    onAddSet = { reps, weight ->
                        // Add the set to the ViewModel
                        viewModel.addSet(reps, weight)
                    },
                    onWorkoutSaved = {
                        // Navigate back to the home screen
                        viewModel.saveWorkoutSession(exerciseName)
                        navController.popBackStack(AppRoutes.HOME_SCREEN, inclusive = false)
                    }
                )
            }

            composable(route = AppRoutes.STATS_SCREEN) { backStackEntry ->
                val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "Unknown"
                val viewModel: WorkoutViewModel = viewModel()
                val sessions by viewModel.getSessionsForChart(exerciseName).collectAsState(initial = emptyList())
                StatsScreen(exerciseName = exerciseName, sessions = sessions)
            }
        }

        // =====================================================================
        // GYM NAVIGATION GRAPH
        // =====================================================================
        navigation(
            startDestination = AppRoutes.GYM_SCREEN,
            route = BottomBarDestination.Gym.route
        ) {
            // The main screen for the Stats tab
            composable(route = AppRoutes.GYM_SCREEN) {
                GymScreen()
            }
            // If you had more screens accessible only from the Stats tab,
            // they would go here. For example:
            // composable("some_other_stats_detail") { ... }
        }
    }
}