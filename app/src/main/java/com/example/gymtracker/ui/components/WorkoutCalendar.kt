package com.example.gymtracker.ui.components

// In ui/components/WorkoutCalendar.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutCalendar(
    modifier: Modifier = Modifier,
    workoutDates: Set<LocalDate>, // The set of dates with workouts
    onDayClicked: (LocalDate) -> Unit // Callback for when a day is clicked
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Display 100 months back
    val endMonth = remember { currentMonth.plusMonths(100) } // Display 100 months forward
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Column(modifier = modifier) {
        // This is the header that shows the month and year
//        Text(
//            text = "${state.firstVisibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${state.firstVisibleMonth.year}",
//            style = MaterialTheme.typography.titleMedium,
//            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
//        )

        // This composable lays out the days of the week titles (Mon, Tue, etc.)
        DaysOfWeekTitle(daysOfWeek = state.firstDayOfWeek.let {
            val days = DayOfWeek.values()
            // Reorder the days to start with the locale's first day of the week
            days.slice(days.indexOf(it)..days.lastIndex) + days.slice(0 until days.indexOf(it))
        })

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    hasWorkout = day.date in workoutDates, // Check if this date has a workout
                    onClick = { onDayClicked(day.date) }
                )
            }
        )
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    hasWorkout: Boolean,
    onClick: (CalendarDay) -> Unit = {}) {
    // Each day is a box. We only draw content if it's part of the current month.
    if (day.position == DayPosition.MonthDate) {
        Box(
            modifier = Modifier
                .aspectRatio(1f), // Makes the day cell a square
            contentAlignment = Alignment.Center
        ) {
            // If a workout exists on this day, draw a small dot underneath the number.
            if (hasWorkout) {
                Box(
//                    modifier = Modifier
//                        .size(4.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.tertiary)
//                        .align(Alignment.BottomCenter)
//                        .offset(y = (-6).dp)
//                        .clickable(
//                            enabled = day.position == DayPosition.MonthDate,
//                            onClick = { onClick(day)}
//                        )
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.tertiary)
                        .clickable(
                            enabled = day.position == DayPosition.MonthDate,
                            onClick = { onClick(day) }
                        )
                )
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (day.date == LocalDate.now()) MaterialTheme.colorScheme.onTertiary else Color.Unspecified
                )
            }
            else {
                // This is the text for the day number (e.g., "1", "25")
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (day.date == LocalDate.now()) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
            }
        }
    }
}

@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight = FontWeight.Medium
            )
        }
    }
}