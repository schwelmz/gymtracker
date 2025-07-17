package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    val plannedWorkoutsThisWeek by workoutPlanViewModel.plannedWorkoutsThisWeek.collectAsState(initial = emptyList())
    val incompletePlans by workoutPlanViewModel.incompleteWorkoutsThisWeek.collectAsState(initial = emptyList())
    val weeklyStreak by workoutPlanViewModel.globalWeeklyStreak.collectAsState()
    val streakWeeks by workoutPlanViewModel.streakWeeks.collectAsState()

    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding,
                        end = 16.dp
                    ),
                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        if (weeklyStreak > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.height(64.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
//                        Text(
//                            text = "🔥",
//                            style = MaterialTheme.typography.headlineMedium,
//                            modifier = Modifier.padding(end = 8.dp)
//                        )
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
        item {
            WorkoutCalendar(
                workoutDates = workoutDates,
                onDayClicked = { date -> onNavigateToWorkoutCalendarDay(date) },
                streakWeeks = streakWeeks
            )
        }

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
            Column {
                Text(planStatus.plan.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    "Progress: ${planStatus.currentWeekCompletedCount} / ${planStatus.plan.goal ?: 1}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = onLogWorkout
            ) {
                Text("Start Workout")
            }
        }
    }
}