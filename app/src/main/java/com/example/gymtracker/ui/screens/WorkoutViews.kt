package com.example.gymtracker.ui.screens

import ExerciseCard
import WorkoutSessionCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cardHeight
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.WorkoutPlan
import com.example.gymtracker.data.WorkoutSession
import com.example.gymtracker.ui.components.WorkoutCalendar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

val headlineTopPadding = 64.dp
val headlineBottomPadding = 32.dp

@Composable
fun RecentWorkoutsView(
    sessions: List<WorkoutSession>,
    onSessionClicked: (String) -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit,
    onModifySession: (WorkoutSession) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn (
    ){
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
                    text = "Recent Workouts",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        if (sessions.isEmpty()) {
            item {
                Text(
                    "No workouts recorded yet."
                )
            }
        } else {
            val sessionDates = sessions.map { it.date }.distinct().sortedByDescending { it.time }
            items(items = sessionDates) { sessionDate ->
                Text(
                    text = SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault()
                    ).format(sessionDate),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    val filteredSessions = sessions.filter { it.date == sessionDate }
                    items(filteredSessions.take(10)) { session ->
                        val totalSets = session.sets.size
                        val totalReps = session.sets.sumOf { it.reps }
                        val details = "$totalSets sets, Total Reps: $totalReps"
                        WorkoutSessionCard(
                            session = session,
                            details = details,
                            onClick = { onSessionClicked(session.exerciseName) },
                            onDelete = { onDeleteSession(session) },
                            onModify = { onModifySession(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCalendarView(
    workoutDates: Set<LocalDate>,
    onNavigateToWorkoutCalendarDay: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "Calendar",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        item {
            Card (
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.height(100.dp).fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Text(
                    "You have a ... day Streak!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )

            }
        }
        item {
            WorkoutCalendar(
                workoutDates = workoutDates,
                onDayClicked = { date -> onNavigateToWorkoutCalendarDay(date) }
            )
        }
    }
}

@Composable
fun AllExercisesView(
    exercises: List<Exercise>,
    onAddExerciseClicked: () -> Unit,
    onExerciseClicked: (String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "All Exercises",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        item {
            Card(
                onClick = onAddExerciseClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add custom exercise",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        items(items = exercises) { exercise ->
            ExerciseCard(
                exercise,
                onClick = { onExerciseClicked(exercise.name) },
                onDelete = { onDeleteExercise(exercise) }
            )
        }
    }
}

@Composable
fun WorkoutPlansView(
    onExercisePicker: (WorkoutPlan) -> Unit,
    onLogWorkoutForPlan: (WorkoutPlan) -> Unit
){
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
                    text = "Workout Plans",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                    // textAlign can be removed if the Box handles the alignment
                )
            }
        }
        item {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        top = headlineTopPadding,
//                        bottom = headlineBottomPadding,
//                        end = 16.dp
//                    ),
//                contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
//            )
//            {
//                Text("work in progress...")
//            }
            WorkoutPlanScreen(
                onExercisePicker = onExercisePicker,
                onLogWorkoutForPlan = onLogWorkoutForPlan
            )
        }
    }
}