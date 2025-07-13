package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun WorkoutHostScreen(mainNavController: NavHostController) {
    val workoutRailNavController = rememberNavController()

    val workoutNavItems = listOf(
        RailNavItem(id = "recent", title = "Recent Workouts", route = AppRoutes.WORKOUT_RECENT_SCREEN),
        RailNavItem(id = "calendar", title = "Workout Calendar", route = AppRoutes.WORKOUT_CALENDAR_VIEW_SCREEN),
        RailNavItem(id = "exercises", title = "All Exercises", route = AppRoutes.WORKOUT_ALL_EXERCISES_SCREEN)
    )

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = workoutNavItems,
            selectedItemId = workoutRailNavController.currentDestination?.route ?: AppRoutes.WORKOUT_RECENT_SCREEN,
            onItemSelected = { route -> workoutRailNavController.navigate(route) }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            WorkoutNavHost(navController = workoutRailNavController, mainNavController = mainNavController)
        }
    }
}

@Composable
fun WorkoutNavHost(navController: NavHostController, mainNavController: NavHostController) {
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = AppRoutes.WORKOUT_RECENT_SCREEN) {
        composable(AppRoutes.WORKOUT_RECENT_SCREEN) {
            val workoutViewModel: WorkoutViewModel = viewModel()
            val sessions by workoutViewModel.allSessions.collectAsState(initial = emptyList())
            RecentWorkoutsView(
                sessions = sessions,
                onSessionClicked = { exerciseName ->
                    mainNavController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                },
                onDeleteSession = { session -> scope.launch { workoutViewModel.deleteSession(session) } },
                onModifySession = { session ->
                    val route = AppRoutes.WORKOUT_MODIFY_SCREEN.replace("{sessionId}", session.id.toString())
                    mainNavController.navigate(route)
                }
            )
        }
        composable(AppRoutes.WORKOUT_CALENDAR_VIEW_SCREEN) {
            val workoutViewModel: WorkoutViewModel = viewModel()
            val workoutDates by workoutViewModel.workoutDates.collectAsState(initial = emptySet())
            WorkoutCalendarView(
                workoutDates = workoutDates,
                onNavigateToWorkoutCalendarDay = { date: LocalDate ->
                    val route = AppRoutes.WORKOUT_CALENDAR_DAY_SCREEN.replace("{day}", date.toString())
                    mainNavController.navigate(route)
                }
            )
        }
        composable(AppRoutes.WORKOUT_ALL_EXERCISES_SCREEN) {
            val exerciseViewModel: ExerciseViewModel = viewModel()
            val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
            AllExercisesView(
                exercises = exercises,
                onAddExerciseClicked = { mainNavController.navigate(AppRoutes.ADD_EXERCISE_SCREEN) },
                onExerciseClicked = { exerciseName ->
                    mainNavController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                },
                onDeleteExercise = { exercise -> scope.launch { exerciseViewModel.deleteExercise(exercise) } }
            )
        }
        composable(route = AppRoutes.WORKOUT_CALENDAR_DAY_SCREEN) { navBackStackEntry ->
            val day = navBackStackEntry.arguments?.getString("day")
            val workoutViewModel: WorkoutViewModel = viewModel()

            val sessions by workoutViewModel.allSessions.collectAsState(initial = emptyList())
            WorkoutCalendarDayScreen(
                day = day,
                sessions = sessions,
                onSessionClicked = { exerciseName ->
                    mainNavController.navigate(AppRoutes.STATS_SCREEN.replace("{exerciseName}", exerciseName))
                },
                onDeleteSession = { session -> scope.launch { workoutViewModel.deleteSession(session) } },
                onModifySession = { session ->
                    val route = AppRoutes.WORKOUT_MODIFY_SCREEN.replace("{sessionId}", session.id.toString())
                    mainNavController.navigate(route)
                }
            )
        }
        composable(route = AppRoutes.WORKOUT_MODIFY_SCREEN) { navBackStackEntry ->
            val sessionId = navBackStackEntry.arguments?.getString("sessionId")?.toIntOrNull()
            if (sessionId != null) {
                WorkoutModifyScreen(
                    sessionId = sessionId,
                    onWorkoutModified = { mainNavController.popBackStack() }
                )
            }
        }
        composable(route = AppRoutes.ADD_EXERCISE_SCREEN) {
            val viewModel: ExerciseViewModel = viewModel()

            AddExerciseScreen(
                onSave = { name, description, imageUri ->
                    scope.launch {
                        viewModel.addCustomExercise(name, description, imageUri)
                    }
                    mainNavController.popBackStack()
                },
                onNavigateUp = { mainNavController.popBackStack() }
            )
        }
        composable(route = AppRoutes.STATS_SCREEN) { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: "Unknown"
            val viewModel: WorkoutViewModel = viewModel()
            val sessions by viewModel.getSessionsForChart(exerciseName).collectAsState(initial = emptyList())
            StatsScreen(exerciseName = exerciseName, sessions = sessions)
        }
    }
}
