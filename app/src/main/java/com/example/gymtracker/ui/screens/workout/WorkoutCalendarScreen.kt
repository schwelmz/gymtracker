package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.components.WorkoutCalendar
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import java.time.LocalDate

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
@Preview(showBackground = true)
fun WorkoutCalendarViewPreview() {
    WorkoutCalendarView(
        workoutDates = setOf(LocalDate.now()),
        onNavigateToWorkoutCalendarDay = {}

    )
}