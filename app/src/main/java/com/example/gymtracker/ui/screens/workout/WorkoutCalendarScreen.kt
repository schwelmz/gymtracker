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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.model.WorkoutPlanWithCompletionStatus
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
    val incompletePlans = plannedWorkoutsThisWeek.filter { !it.isGoalMetThisWeek }

    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = headlineTopPadding,
                        bottom = headlineBottomPadding,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Calendar",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        item {
            WorkoutCalendar(
                workoutDates = workoutDates,
                onDayClicked = { date -> onNavigateToWorkoutCalendarDay(date) },
                plannedWorkoutsThisWeek = plannedWorkoutsThisWeek
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            if (incompletePlans.isNotEmpty()) {
                Text(
                    text = "Weekly Goals",
                    style = MaterialTheme.typography.headlineMedium,
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
    planStatus: WorkoutPlanWithCompletionStatus,
    onLogWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(planStatus.plan.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Progress: ${planStatus.currentWeekCompletedCount} / ${planStatus.plan.goal ?: 1}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onLogWorkout,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Start Workout")
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun WorkoutCalendarViewPreview() {
    WorkoutCalendarView(
        workoutDates = setOf(LocalDate.now()),
        onNavigateToWorkoutCalendarDay = {},
        onLogWorkoutForPlan = { _, _ -> }
    )
}