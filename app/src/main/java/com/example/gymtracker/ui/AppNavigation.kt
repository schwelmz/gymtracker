package com.example.gymtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.gymtracker.ui.screens.AddExerciseScreen
import com.example.gymtracker.ui.screens.AddWorkoutScreen
import com.example.gymtracker.ui.screens.ExercisesScreen
import com.example.gymtracker.ui.screens.HomeScreen
import com.example.gymtracker.ui.screens.StatsScreen
import com.example.gymtracker.ui.screens.WorkoutLogScreen
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import com.example.gymtracker.ui.screens.FoodScannerScreen
import com.example.gymtracker.ui.screens.WorkoutScreen // Assuming you will create a WorkoutScreen
import com.example.gymtracker.ui.screens.AboutScreen
import com.example.gymtracker.ui.screens.SettingsScreen

object AppRoutes {
    // Home Graph

    const val HOME_SCREEN = "home_screen"
    const val ADD_WORKOUT_SCREEN = "add_workout"
    const val WORKOUT_LOG_SCREEN = "workout_log/{exerciseName}"
    const val STATS_SCREEN = "stats/{exerciseName}"

    // Exercises Graph

    const val EXERCISES_SCREEN = "exercises_screen"
    const val ADD_EXERCISE_SCREEN = "add_exercise"

    // --- ADD ROUTES FOR NEW GRAPHS ---

    const val WORKOUT_SCREEN = "workout_screen"

    const val SCANNER_SCREEN = "scanner_screen"
    // Settings Graph
    const val SETTINGS_SCREEN = "settings_screen"
    const val ABOUT_SCREEN = "about_screen"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {

    // The start destination is the route of the Home graph.
    NavHost(
        navController = navController,
        startDestination = BottomBarDestination.Home.route,
        modifier = modifier
    ) {
        // =====================================================================
        // HOME NAVIGATION GRAPH
        // =====================================================================
        navigation(
            startDestination = AppRoutes.HOME_SCREEN,
            route = BottomBarDestination.Home.route
        ) {
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
                val viewModel: WorkoutViewModel = viewModel()
                val sets by viewModel.currentSets.collectAsState()
                WorkoutLogScreen(
                    exerciseName = exerciseName,
                    sets = sets,
                    onAddSet = { reps, weight -> viewModel.addSet(reps, weight) },
                    onWorkoutSaved = {
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
        // EXERCISES NAVIGATION GRAPH
        // =====================================================================
        navigation(
            startDestination = AppRoutes.EXERCISES_SCREEN,
            route = BottomBarDestination.Exercises.route
        ) {
            composable(route = AppRoutes.EXERCISES_SCREEN) {
                val exerciseViewModel: ExerciseViewModel = viewModel()
                val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
                ExercisesScreen(
                    exercises,
                    onAddExerciseClicked = { navController.navigate(AppRoutes.ADD_EXERCISE_SCREEN) },
                    onExerciseClicked = { exerciseName ->
                        navController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                    },
                    onDeleteExercise = { exercise -> exerciseViewModel.deleteExercise(exercise) }
                )
            }
            composable(route = AppRoutes.ADD_EXERCISE_SCREEN) {
                val viewModel: ExerciseViewModel = viewModel()
                AddExerciseScreen(
                    onSave = { name, description, imageUri ->
                        viewModel.addCustomExercise(name, description, imageUri)
                        navController.popBackStack()
                    },
                    onNavigateUp = { navController.popBackStack() }
                )
            }
        }

        // =====================================================================
        // WORKOUT NAVIGATION GRAPH (NEW)
        // =====================================================================
        navigation(
            startDestination = AppRoutes.WORKOUT_SCREEN, // The main screen for this tab
            route = BottomBarDestination.Workout.route // The route from the Bottom Nav Bar
        ) {
            composable(route = AppRoutes.WORKOUT_SCREEN) {
                // You will need to create a WorkoutScreen composable.
                // For now, we can use a placeholder.
                WorkoutScreen()
            }
            // You can add other screens you navigate to from the WorkoutScreen here.
        }

        // =====================================================================
        // SCANNER NAVIGATION GRAPH (NEW)
        // =====================================================================
        navigation(
            startDestination = AppRoutes.SCANNER_SCREEN,
            route = BottomBarDestination.Scanner.route
        ) {
            composable(route = AppRoutes.SCANNER_SCREEN) {
                // This will be the screen with the barcode scanner.
                FoodScannerScreen()
            }
        }
        // =====================================================================
        // SETTINGS NAVIGATION GRAPH (NEW)
        // =====================================================================
        navigation(
            startDestination = AppRoutes.SETTINGS_SCREEN,
            route = BottomBarDestination.Settings.route
        ) {
            composable(route = AppRoutes.SETTINGS_SCREEN) {
                SettingsScreen(
                    onNavigateToAbout = {
                        navController.navigate(AppRoutes.ABOUT_SCREEN)
                    }
                )
            }
            composable(route = AppRoutes.ABOUT_SCREEN) {
                AboutScreen(
                    onNavigateUp = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}