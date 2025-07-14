package com.example.gymtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.gymtracker.ui.screens.AddExerciseScreen
import com.example.gymtracker.ui.screens.AddWorkoutScreen
import com.example.gymtracker.ui.screens.HomeScreen
import com.example.gymtracker.ui.screens.StatsScreen
import com.example.gymtracker.ui.screens.WorkoutLogScreen
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import com.example.gymtracker.ui.screens.FoodScannerScreen
import com.example.gymtracker.data.UserGoals
import com.example.gymtracker.ui.screens.AboutScreen
import com.example.gymtracker.ui.screens.SettingsScreen
import androidx.compose.ui.platform.LocalUriHandler
import com.example.gymtracker.ui.screens.NutritionScreen
import com.example.gymtracker.ui.screens.WorkoutCalendarDayScreen
import com.example.gymtracker.ui.screens.WorkoutModifyScreen
import androidx.compose.runtime.rememberCoroutineScope
import com.example.gymtracker.ui.screens.FoodDiaryScreen
import com.example.gymtracker.viewmodel.FoodScannerUiState
import com.example.gymtracker.viewmodel.FoodScannerViewModel
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import com.example.gymtracker.data.CalorieMode
import com.example.gymtracker.ui.screens.AddCustomFoodScreen
import com.example.gymtracker.ui.screens.AllExercisesView
import com.example.gymtracker.ui.screens.CustomFoodListScreen
import com.example.gymtracker.ui.screens.HomeHostScreen
import com.example.gymtracker.ui.screens.NutritionHostScreen
import com.example.gymtracker.ui.screens.RecentWorkoutsView
import com.example.gymtracker.ui.screens.SettingsHostScreen
import com.example.gymtracker.ui.screens.WeightHistoryScreen
import com.example.gymtracker.ui.screens.WorkoutCalendarView
import com.example.gymtracker.ui.screens.WorkoutHostScreen
import com.example.gymtracker.viewmodel.GoalsViewModel
import com.example.gymtracker.viewmodel.HomeViewModel

object AppRoutes {
    // Home Graph

    const val HOME_SCREEN = "home_screen"
    const val ADD_WORKOUT_SCREEN = "add_workout"
    const val WORKOUT_LOG_SCREEN = "workout_log/{exerciseName}"
    const val STATS_SCREEN = "stats/{exerciseName}"
    const val WEIGHT_HISTORY_SCREEN = "weight_history_screen"

    // Exercises Graph

    const val ADD_EXERCISE_SCREEN = "add_exercise"

    // Workout Graph

    const val WORKOUT_SCREEN = "workout_screen"
    const val WORKOUT_CALENDAR_DAY_SCREEN = "workout_calendar_day_screen/{day}"
    const val WORKOUT_MODIFY_SCREEN = "workout_modify_screen/{sessionId}"
    const val WORKOUT_RECENT_SCREEN = "workout_recent_screen"
    const val WORKOUT_CALENDAR_VIEW_SCREEN = "workout_calendar_view_screen"
    const val WORKOUT_ALL_EXERCISES_SCREEN = "workout_all_exercises_screen"
    const val WORKOUT_PLANS_SCREEN = "workout_plans_screen"

    // Nutrition Graph
    const val NUTRITION_SCREEN = "nutrition_screen"
    const val FOOD_SCANNER_SCREEN = "food_scanner_screen?open_camera={open_camera}"
    const val FOOD_DIARY_SCREEN = "food_diary_screen"
    const val RECIPE_SCREEN = "recipe_screen"

    // Settings Graph
    const val SETTINGS_SCREEN = "settings_screen"
    const val ABOUT_SCREEN = "about_screen"
    const val CUSTOM_FOOD_LIST_SCREEN = "custom_food_list_screen"
    const val ADD_CUSTOM_FOOD_SCREEN = "add_custom_food_screen"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onGrantPermissionsClick: () -> Unit) {

    val scope = rememberCoroutineScope()
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory)
    val goalsViewModel: GoalsViewModel = viewModel(factory = GoalsViewModel.Factory)
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
                HomeHostScreen(
                    mainNavController = navController,
                    onGrantPermissionsClick = onGrantPermissionsClick,
                    homeViewModel = homeViewModel,
                    foodViewModel = foodViewModel,
                    goalsViewModel = goalsViewModel,
                    onNavigateToWeightHistory = { navController.navigate(AppRoutes.WEIGHT_HISTORY_SCREEN) }
                )
            }

            composable(route = AppRoutes.WEIGHT_HISTORY_SCREEN) {
                WeightHistoryScreen(onNavigateUp = { navController.popBackStack() })
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
        // WORKOUT NAVIGATION GRAPH (CORRECTED)
        // =====================================================================
        navigation(
            startDestination = AppRoutes.WORKOUT_SCREEN,
            route = BottomBarDestination.Workout.route
        ) {
            composable(route = AppRoutes.WORKOUT_SCREEN) {
                WorkoutHostScreen(mainNavController = navController)
            }
            composable(route = AppRoutes.ADD_EXERCISE_SCREEN) { navBackStackEntry -> // FIX: Explicitly name the parameter
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Workout.route)
                }
                val viewModel: ExerciseViewModel = viewModel(parentEntry)

                AddExerciseScreen(
                    onSave = { name, description, imageUri ->
                        scope.launch {
                            viewModel.addCustomExercise(name, description, imageUri)
                        }
                        navController.popBackStack()
                    },
                    onNavigateUp = { navController.popBackStack() }
                )
            }
            composable(route = AppRoutes.WORKOUT_CALENDAR_DAY_SCREEN) { navBackStackEntry -> // FIX: Explicit name
                val day = navBackStackEntry.arguments?.getString("day")
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Workout.route)
                }
                val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)

                val sessions by workoutViewModel.allSessions.collectAsState(initial = emptyList())
                WorkoutCalendarDayScreen(
                    day = day,
                    sessions = sessions,
                    onSessionClicked = { exerciseName ->
                        navController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                    },
                    onDeleteSession = { session -> scope.launch { workoutViewModel.deleteSession(session) } },
                    onModifySession = { session ->
                        val route = AppRoutes.WORKOUT_MODIFY_SCREEN.replace("{sessionId}", session.id.toString())
                        navController.navigate(route)
                    }
                )
            }
            composable(route = AppRoutes.WORKOUT_MODIFY_SCREEN) { navBackStackEntry ->
                val sessionId = navBackStackEntry.arguments?.getString("sessionId")?.toIntOrNull()
                if (sessionId != null) {
                    WorkoutModifyScreen(
                        sessionId = sessionId,
                        onWorkoutModified = { navController.popBackStack() }
                    )
                }
            }
            composable(route = AppRoutes.STATS_SCREEN) { backStackEntry ->
                val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "Unknown"
                val viewModel: WorkoutViewModel = viewModel()
                val sessions by viewModel.getSessionsForChart(exerciseName).collectAsState(initial = emptyList())
                StatsScreen(exerciseName = exerciseName, sessions = sessions)
            }
        }

        // =====================================================================
        // NUTRITION NAVIGATION GRAPH (CORRECTED)
        // =====================================================================
        navigation(
            startDestination = AppRoutes.NUTRITION_SCREEN,
            route = BottomBarDestination.Nutrition.route
        ) {
            composable(route = AppRoutes.NUTRITION_SCREEN) {
                NutritionHostScreen(mainNavController = navController, foodViewModel = foodViewModel, goalsViewModel = goalsViewModel)
            }
            composable(route = AppRoutes.FOOD_SCANNER_SCREEN) { navBackStackEntry -> // FIX: Explicit name
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Nutrition.route)
                }
                val foodViewModel: FoodViewModel = viewModel(parentEntry)
                val scannerViewModel: FoodScannerViewModel = viewModel(parentEntry)

                FoodScannerScreen(
                    foodViewModel = foodViewModel,
                    scannerViewModel = scannerViewModel,
                    onSave = {
                        navController.popBackStack()
                    },
                    shouldOpenCameraDirectly = true
                )
            }
//            composable(route = AppRoutes.FOOD_DIARY_SCREEN) { navBackStackEntry -> // FIX: Explicit name
//                val parentEntry = remember(navBackStackEntry) {
//                    navController.getBackStackEntry(BottomBarDestination.Nutrition.route)
//                }
//                val foodViewModel: FoodViewModel = viewModel(parentEntry)
//                FoodDiaryScreen(
//                    viewModel = foodViewModel,
//                    calorieGoal = 2000,
//                    onNavigateUp = { navController.popBackStack() },
//                    calorieMode = CalorieMode.SURPLUS
//                )
//            }
            // Add the new destinations
            composable(route = AppRoutes.CUSTOM_FOOD_LIST_SCREEN) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Nutrition.route)
                }
                val foodViewModel: FoodViewModel = viewModel(parentEntry)
                CustomFoodListScreen(
                    viewModel = foodViewModel,
                    onNavigateUp = { navController.popBackStack() },
                    onNavigateToAddCustomFood = { navController.navigate(AppRoutes.ADD_CUSTOM_FOOD_SCREEN) }
                )
            }

            composable(route = AppRoutes.ADD_CUSTOM_FOOD_SCREEN) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Nutrition.route)
                }
                val foodViewModel: FoodViewModel = viewModel(parentEntry)
                AddCustomFoodScreen(
                    viewModel = foodViewModel,
                    onNavigateUp = { navController.popBackStack() }
                )
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
                SettingsHostScreen(mainNavController = navController)
            }
        }
    }
}