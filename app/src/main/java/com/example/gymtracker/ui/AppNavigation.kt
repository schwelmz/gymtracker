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
import androidx.compose.ui.platform.LocalUriHandler

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
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController,onGrantPermissionsClick: () -> Unit) {

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
            // --- THIS IS THE CORRECTED SECTION ---
            // The main screen for the Home tab now calls the simple HomeScreen.
            composable(route = AppRoutes.HOME_SCREEN) {
                // The parameters from the old implementation are no longer needed here.
                HomeScreen(onGrantPermissionsClick = onGrantPermissionsClick)
            }

            // The routes you navigate to FROM the home screen remain the same.
            // These are still needed because the Floating Action Button uses them.
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
                    onAddSet = { reps, weight ->
                        viewModel.addSet(reps, weight)
                    },
                    onWorkoutSaved = {
                        viewModel.saveWorkoutSession(exerciseName)
                        navController.popBackStack(AppRoutes.WORKOUT_SCREEN, inclusive = false) // Go back to the workout list
                    }
                )
            }

            // StatsScreen is now mainly accessed from the WorkoutScreen, but can be kept here
            // if you have other ways to access it from the Home graph.
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
        // WORKOUT NAVIGATION GRAPH
        // =====================================================================
        navigation(
            startDestination = AppRoutes.WORKOUT_SCREEN,
            route = BottomBarDestination.Workout.route
        ) {
            composable(route = AppRoutes.WORKOUT_SCREEN) {
                // --- THIS IS THE UPDATED SECTION ---
                // Provide the same data to WorkoutScreen as you did for HomeScreen
                val workoutViewModel: WorkoutViewModel = viewModel()
                val exerciseViewModel: ExerciseViewModel = viewModel()
                val sessions by workoutViewModel.allSessions.collectAsState(initial = emptyList())
                val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())

                WorkoutScreen(
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
        // SETTINGS NAVIGATION GRAPH
        // =====================================================================
        navigation(
            startDestination = AppRoutes.SETTINGS_SCREEN,
            route = BottomBarDestination.Settings.route
        ) {
            composable(route = AppRoutes.SETTINGS_SCREEN) {
                // --- THIS IS THE SECTION TO UPDATE ---
                val uriHandler = LocalUriHandler.current
                // !! IMPORTANT !! Replace this with your actual donation link
                val donationUrl = "https://www.buymeacoffee.com/your-username"

                SettingsScreen(
                    onNavigateToAbout = {
                        navController.navigate(AppRoutes.ABOUT_SCREEN)
                    },
                    onNavigateToDonate = {
                        uriHandler.openUri(donationUrl)
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