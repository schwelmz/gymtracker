package com.example.gymtracker.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.screens.workout.AllExercisesView
import com.example.gymtracker.ui.screens.workout.RecentWorkoutsView
import com.example.gymtracker.ui.screens.workout.WorkoutCalendarView
import com.example.gymtracker.ui.screens.workout.WorkoutPlansView
import com.example.gymtracker.viewmodel.ExerciseViewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun WorkoutHostScreen(mainNavController: NavHostController) {
    val workoutNavItems = listOf(
        RailNavItem(id = "overview", title = "Overview", route = AppRoutes.WORKOUT_OVERVIEW_SCREEN),
        RailNavItem(id = "recent", title = "Recent", route = AppRoutes.WORKOUT_RECENT_SCREEN),
        RailNavItem(id = "exercises", title = "Exercises", route = AppRoutes.WORKOUT_ALL_EXERCISES_SCREEN),
        RailNavItem(id = "plans", title = "Plans", route = AppRoutes.WORKOUT_PLANS_SCREEN)
    )

    val pagerState = rememberPagerState { workoutNavItems.size }
    val scope = rememberCoroutineScope()

    // Synchronize pager state with rail selection
    LaunchedEffect(pagerState.currentPage) {
        // Ensure rail highlights the current page when scrolled
    }

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = workoutNavItems,
            selectedItemId = workoutNavItems[pagerState.currentPage].id,
            onItemSelected = { route ->
                val index = workoutNavItems.indexOfFirst { it.route == route }
                if (index != -1) {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val workoutViewModel: WorkoutViewModel = viewModel()
                val exerciseViewModel: ExerciseViewModel = viewModel()

                when (workoutNavItems[page].route) {
                    AppRoutes.WORKOUT_OVERVIEW_SCREEN -> {
                        val workoutDates by workoutViewModel.workoutDates.collectAsState(initial = emptySet())
                        WorkoutCalendarView(
                            workoutDates = workoutDates,
                            onNavigateToWorkoutCalendarDay = { date: LocalDate ->
                                val route = AppRoutes.WORKOUT_CALENDAR_DAY_SCREEN.replace(
                                    "{day}",
                                    date.toString()
                                )
                                mainNavController.navigate(route)
                            },
                            onLogWorkoutForPlan = { exercises, planId ->
                                val exercisesString = exercises.joinToString(separator = ",")
                                val route = AppRoutes.PLAN_WORKOUT_LOG_SCREEN
                                    .replace("{exerciseNames}", exercisesString)
                                    .replace("{planId}", planId.toString())
                                mainNavController.navigate(route)
                            }
                        )
                    }
                    AppRoutes.WORKOUT_RECENT_SCREEN -> {
                        val sessions by workoutViewModel.allSessions.collectAsState(initial = emptyList())
                        RecentWorkoutsView(
                            sessions = sessions,
                            onSessionClicked = { exerciseName ->
                                mainNavController.navigate(
                                    AppRoutes.STATS_SCREEN.replace(
                                        "{exerciseName}",
                                        exerciseName
                                    )
                                )
                            },
                            onDeleteSession = { session ->
                                scope.launch {
                                    // Ensure deletion occurs on a background thread
                                    withContext(Dispatchers.IO) {
                                        workoutViewModel.deleteSession(session)
                                    }
                                }
                            },
                            onModifySession = { session ->
                                val route = AppRoutes.WORKOUT_MODIFY_SCREEN.replace(
                                    "{sessionId}",
                                    session.id.toString()
                                )
                                mainNavController.navigate(route)
                            }
                        )
                    }
                    AppRoutes.WORKOUT_ALL_EXERCISES_SCREEN -> {
                        val exercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
                        AllExercisesView(
                            exercises = exercises,
                            onAddExerciseClicked = { mainNavController.navigate(AppRoutes.ADD_EXERCISE_SCREEN) },
                            onExerciseClicked = { exerciseName ->
                                mainNavController.navigate(
                                    AppRoutes.STATS_SCREEN.replace(
                                        "{exerciseName}",
                                        exerciseName
                                    )
                                )
                            },
                            onDeleteExercise = { exercise ->
                                scope.launch {
                                    // Perform exercise deletion on a background thread
                                    withContext(Dispatchers.IO) {
                                        exerciseViewModel.deleteExercise(exercise)
                                    }
                                }
                            }
                        )
                    }
                    AppRoutes.WORKOUT_PLANS_SCREEN -> {
                        WorkoutPlansView(
                            onExercisePicker = { plan ->
                                val route = AppRoutes.EXERCISE_PICKER_SCREEN.replace(
                                    "{planId}",
                                    plan.id.toString()
                                )
                                mainNavController.navigate(route)
                            },
                            onLogWorkoutForPlan = { exercises, planId ->
                                val exercisesString = exercises.joinToString(separator = ",")
                                val route = AppRoutes.PLAN_WORKOUT_LOG_SCREEN
                                    .replace("{exerciseNames}", exercisesString)
                                    .replace("{planId}", planId.toString())
                                mainNavController.navigate(route)
                            }
                        )
                    }
                }
            }
        }
    }
}
