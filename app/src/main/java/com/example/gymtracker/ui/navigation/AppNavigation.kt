package com.example.gymtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.gymtracker.ui.screens.nutrition.AddCustomFoodScreen
import com.example.gymtracker.ui.screens.nutrition.AddEditRecipeScreen
import com.example.gymtracker.ui.screens.workout.AddExerciseScreen
import com.example.gymtracker.ui.screens.workout.AddWorkoutScreen
import com.example.gymtracker.ui.screens.nutrition.CustomFoodListScreen
import com.example.gymtracker.ui.screens.workout.ExercisePickerScreen
import com.example.gymtracker.ui.screens.nutrition.FoodScannerScreen
import com.example.gymtracker.ui.screens.workout.PlanWorkoutLogScreen
import com.example.gymtracker.ui.screens.workout.StatsScreen
import com.example.gymtracker.ui.screens.home.WeightHistoryScreen
import com.example.gymtracker.ui.screens.settings.AboutScreen
import com.example.gymtracker.ui.screens.workout.WorkoutCalendarDayScreen
import com.example.gymtracker.ui.screens.workout.WorkoutLogScreen
import com.example.gymtracker.ui.screens.workout.WorkoutModifyScreen
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.FoodScannerViewModel
import com.example.gymtracker.viewmodel.FoodViewModel
import com.example.gymtracker.viewmodel.GoalsViewModel
import com.example.gymtracker.viewmodel.HomeViewModel
import com.example.gymtracker.viewmodel.RecipeViewModel
import com.example.gymtracker.viewmodel.ScannerResultViewModel
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

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
    const val WORKOUT_OVERVIEW_SCREEN = "workout_calendar_view_screen"
    const val WORKOUT_ALL_EXERCISES_SCREEN = "workout_all_exercises_screen"
    const val WORKOUT_PLANS_SCREEN = "workout_plans_screen"
    const val EXERCISE_PICKER_SCREEN = "exercise_picker/{planId}"
    const val PLAN_WORKOUT_LOG_SCREEN = "plan_workout_log_screen/{exerciseNames}?planId={planId}"

    // Nutrition Graph
    const val NUTRITION_SCREEN = "nutrition_screen"
    const val FOOD_SCANNER_SCREEN = "food_scanner_screen" // Cleaned route
    const val FOOD_DIARY_SCREEN = "food_diary_screen"
    const val RECIPE_SCREEN = "recipe_screen"
    const val RECIPE_ADD_EDIT_SCREEN = "recipe_add_edit_screen/{recipeId}"
    const val CUSTOM_FOOD_LIST_SCREEN = "custom_food_list_screen"
    const val ADD_CUSTOM_FOOD_SCREEN = "add_custom_food_screen"

    // Settings Graph
    const val SETTINGS_SCREEN = "settings_screen"
    const val ABOUT_SCREEN = "about_screen"
    const val SETTINGS_SET_GOALS_SCREEN = "settings_set_goals_screen"
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
    val recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)
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
                val lastReps by viewModel.lastReps.collectAsState()
                val lastWeight by viewModel.lastWeight.collectAsState()

                LaunchedEffect(exerciseName) {
                    if (exerciseName != "Unknown") {
                        viewModel.loadLastSession(exerciseName)
                        viewModel.resetCurrentSets()
                    }
                }

                if (lastReps != null && lastWeight != null) {
                    WorkoutLogScreen(
                        exerciseName = exerciseName,
                        sets = sets,
                        onAddSet = { reps, weight ->
                            viewModel.addSet(reps, weight)
                        },
                        onWorkoutSaved = {
                            viewModel.saveWorkoutSession(exerciseName)
                            navController.popBackStack(AppRoutes.ADD_WORKOUT_SCREEN, inclusive = true)
                        },
                        initialReps = lastReps,
                        initialWeight = lastWeight
                    )
                }
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
            composable(
                route = AppRoutes.PLAN_WORKOUT_LOG_SCREEN,
                arguments = listOf(
                    navArgument("exerciseNames") { type = NavType.StringType },
                    navArgument("planId") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val exerciseNames = backStackEntry.arguments?.getString("exerciseNames")
                val planId = backStackEntry.arguments?.getString("planId")?.toIntOrNull()
                PlanWorkoutLogScreen(
                    exerciseNames = exerciseNames,
                    planId = planId,
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
            composable(
                route = AppRoutes.EXERCISE_PICKER_SCREEN,
                arguments = listOf(navArgument("planId") { type = NavType.IntType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getInt("planId") ?: return@composable
                val workoutPlanViewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory)
                ExercisePickerScreen(
                    planId = planId,
                    onNavigateUp = { navController.popBackStack() },
                    workoutPlanViewModel = workoutPlanViewModel
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
        // NUTRITION NAVIGATION GRAPH (CORRECTED)
        // =====================================================================
        navigation(
            startDestination = AppRoutes.NUTRITION_SCREEN,
            route = BottomBarDestination.Nutrition.route
        ) {
            composable(route = AppRoutes.NUTRITION_SCREEN) {
                NutritionHostScreen(
                    mainNavController = navController,
                    foodViewModel = foodViewModel,
                    goalsViewModel = goalsViewModel,
                    recipeViewModel = recipeViewModel
                )
            }
            composable(
                route = AppRoutes.FOOD_SCANNER_SCREEN + "?open_camera={open_camera}&is_for_recipe={is_for_recipe}",
                arguments = listOf(
                    navArgument("open_camera") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("is_for_recipe") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Nutrition.route)
                }
                val foodViewModel: FoodViewModel = viewModel(parentEntry)
                val scannerViewModel: FoodScannerViewModel = viewModel(parentEntry)
                val scannerResultViewModel: ScannerResultViewModel = viewModel(parentEntry)

                val shouldOpenCamera = navBackStackEntry.arguments?.getBoolean("open_camera") ?: false
                val isForRecipe = navBackStackEntry.arguments?.getBoolean("is_for_recipe") ?: false

                FoodScannerScreen(
                    foodViewModel = foodViewModel,
                    scannerViewModel = scannerViewModel,
                    scannerResultViewModel = scannerResultViewModel,
                    onSave = { navController.popBackStack() },
                    shouldOpenCameraDirectly = shouldOpenCamera,
                    isForRecipe = isForRecipe
                )
            }
            composable(
                route = AppRoutes.RECIPE_ADD_EDIT_SCREEN,
                arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(BottomBarDestination.Nutrition.route)
                }
                val scannerResultViewModel: ScannerResultViewModel = viewModel(parentEntry)
                val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1

                AddEditRecipeScreen(
                    recipeId = recipeId,
                    scannerResultViewModel = scannerResultViewModel,
                    onNavigateUp = { navController.popBackStack() },
                    onNavigateToScanner = {
                        val route = AppRoutes.FOOD_SCANNER_SCREEN + "?open_camera=true&is_for_recipe=true"
                        navController.navigate(route)
                    }
                )
            }
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
            composable(route = AppRoutes.ABOUT_SCREEN) {
                AboutScreen(onNavigateUp = { navController.popBackStack() })
            }
        }
    }
}