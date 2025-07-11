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
import com.example.gymtracker.data.ExerciseRepository
import com.example.gymtracker.ui.screens.AddExerciseScreen
import com.example.gymtracker.ui.screens.AddWorkoutScreen
import com.example.gymtracker.ui.screens.ExercisesScreen
import com.example.gymtracker.ui.screens.HomeScreen
import com.example.gymtracker.ui.screens.StatsScreen
import com.example.gymtracker.ui.screens.WorkoutLogScreen
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel

object AppRoutes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"
    const val EXERCISES_GRAPH = "exercises_graph"
    const val EXERCISES_SCREEN = "exercises_screen"
    const val ADD_EXERCISE_SCREEN = "add_exercise"
    const val ADD_WORKOUT_SCREEN = "add_workout"
    const val WORKOUT_LOG_SCREEN = "workout_log/{exerciseName}"
    const val STATS_SCREEN = "stats/{exerciseName}"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {

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
                val workoutViewModel: WorkoutViewModel = viewModel()
                val exerciseViewModel: ExerciseViewModel = viewModel()
                val sessions by workoutViewModel.allSessions.collectAsState(initial = emptyList())
                val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())

                HomeScreen(
                    sessions = sessions,
                    exercises = exercises,
                    onNavigateToAddWorkout = { navController.navigate(AppRoutes.ADD_WORKOUT_SCREEN) },
                    onSessionClicked = { exerciseName ->
                        navController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                    },
                    onDeleteSession = { session ->
                        workoutViewModel.deleteSession(session)
                    }
                )
            }

            // Screens you navigate to FROM the home screen
            composable(route = AppRoutes.ADD_WORKOUT_SCREEN) {
                val exerciseViewModel: ExerciseViewModel = viewModel()
                val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
                AddWorkoutScreen(
                    onExerciseSelected = { exerciseName ->
                        navController.navigate(AppRoutes.WORKOUT_LOG_SCREEN.replace("{exerciseName}", exerciseName))
                    },
                    exercises = exercises
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
            startDestination = AppRoutes.EXERCISES_SCREEN,
            route = BottomBarDestination.Exercises.route
        ) {
            // The main screen for the Stats tab
            composable(route = AppRoutes.EXERCISES_SCREEN) {
                val exerciseViewModel: ExerciseViewModel = viewModel()
                val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
                ExercisesScreen(
                    exercises,
                    onAddExerciseClicked = {
                        navController.navigate(AppRoutes.ADD_EXERCISE_SCREEN)
                    },
                    onExerciseClicked = { exerciseName ->
                        navController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                    },
                    onDeleteExercise = { exercise ->
                        exerciseViewModel.deleteExercise(exercise)
                    }
                )
            }

            composable(route = AppRoutes.ADD_EXERCISE_SCREEN) {
                val viewModel: ExerciseViewModel = viewModel()
                AddExerciseScreen(
                    onSave = { name, description ->
                        viewModel.addCustomExercise(name, description)
                        // After saving, go back to the previous screen
                        navController.popBackStack()
                    },
                    onNavigateUp = {
                        // Just go back without saving
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}