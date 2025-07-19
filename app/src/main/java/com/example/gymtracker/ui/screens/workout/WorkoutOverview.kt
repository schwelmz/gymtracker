package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.model.WorkoutPlanStatus
import com.example.gymtracker.ui.components.WorkoutCalendar
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import com.example.gymtracker.viewmodel.WorkoutPlanViewModel
import java.time.LocalDate

@Composable
fun WorkoutCalendarView(
    workoutDates: Set<LocalDate>,
    onNavigateToWorkoutCalendarDay: (LocalDate) -> Unit,
    onLogWorkoutForPlan: (List<String>, Int) -> Unit,
    modifier: Modifier = Modifier,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(factory = WorkoutPlanViewModel.Factory)
) {
    // Collecting state from the ViewModel
    val plannedWorkoutsThisWeek by workoutPlanViewModel.plannedWorkoutsThisWeek.collectAsState(initial = emptyList())
    val incompletePlans by workoutPlanViewModel.incompleteWorkoutsThisWeek.collectAsState(initial = emptyList())
    val weeklyStreak by workoutPlanViewModel.globalWeeklyStreak.collectAsState()
    val streakWeeks by workoutPlanViewModel.streakWeeks.collectAsState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        // Overview section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headlineTopPadding, bottom = headlineBottomPadding),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Weekly streak display if there's a streak
        if (weeklyStreak > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val streakUnit = if (weeklyStreak == 1) "week" else "weeks"
                        Text(
                            text = "You have a $weeklyStreak $streakUnit streak!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // Workout calendar display
        item {
            WorkoutCalendar(
                workoutDates = workoutDates,
                onDayClicked = { date -> onNavigateToWorkoutCalendarDay(date) },
                streakWeeks = streakWeeks
            )
        }

        // Display "Planned this week" section if there are incomplete plans
        item {
            Spacer(modifier = Modifier.height(24.dp))
            if (incompletePlans.isNotEmpty()) {
                Text(
                    text = "Planned this week",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Display workout plans
        items(incompletePlans) { planStatus ->
            WorkoutPlanGoalCard(
                planStatus = planStatus,
                onLogWorkout = {
                    onLogWorkoutForPlan(planStatus.exercises.map { it.name }, planStatus.plan.id)
                }
            )
        }
    }
}

@Composable
fun WorkoutPlanGoalCard(
    planStatus: WorkoutPlanStatus,
    onLogWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f) // Ensures proper space distribution between text and button
            ) {
                Text(planStatus.plan.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    "Progress: ${planStatus.currentWeekCompletedCount} / ${planStatus.plan.goal ?: 1}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = onLogWorkout) {
                Text("Start Workout")
            }
        }
    }
}
