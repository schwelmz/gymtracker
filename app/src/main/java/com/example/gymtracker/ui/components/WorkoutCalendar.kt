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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape

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
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
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
                    weekHasWorkout = weekHasWorkout(day, workoutDates, firstDayOfWeek), // Check if this week has a workout
                    firstDayOfWeek = firstDayOfWeek,
                    onClick = { onDayClicked(day.date) }
                )
            }
        )
    }
}

//@Composable
//private fun Day(
//    day: CalendarDay,
//    hasWorkout: Boolean,
//    onClick: (CalendarDay) -> Unit = {}) {
//    // Each day is a box. We only draw content if it's part of the current month.
//
//        Box(
//            modifier = Modifier
//                .aspectRatio(1f), // Makes the day cell a square
//            contentAlignment = Alignment.Center
//        ) {
//            // If a workout exists on this day, draw a small dot underneath the number.
//            if (hasWorkout) {
//                Box(
//                    modifier = Modifier
//                        .size(30.dp)
//                        .clip(CircleShape)
//                        .background(color = MaterialTheme.colorScheme.tertiary)
//                        .clickable(
//                            enabled = day.position == DayPosition.MonthDate,
//                            onClick = { onClick(day) }
//                        )
//                )
//                Text(
//                    text = day.date.dayOfMonth.toString(),
//                    color = MaterialTheme.colorScheme.onTertiary
//                )
//            }
//            else {
//                // This is the text for the day number (e.g., "1", "25")
//                if (day.position == DayPosition.MonthDate) {
//                    Text(
//                        text = day.date.dayOfMonth.toString(),
//                        color =  MaterialTheme.colorScheme.onSurface
//                    )
//                }
//                else if (day.date == LocalDate.now()) {
//                    Text(
//                        text = day.date.dayOfMonth.toString(),
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//                else {
//                    Text(
//                        text = day.date.dayOfMonth.toString(),
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
//                    )
//                }
//
//            }
//        }
//
//}

@Composable
private fun Day(
    day: CalendarDay,
    hasWorkout: Boolean, // Does this specific day have a workout?
    weekHasWorkout: Boolean, // Does this entire week have a workout?
    firstDayOfWeek: DayOfWeek,
    onClick: (CalendarDay) -> Unit = {}
) {
    // This is the main container for each day cell
    Box(
        modifier = Modifier.aspectRatio(1f), // Makes it a square
        contentAlignment = Alignment.Center
    ) {
        // --- 1. DRAW THE PILL BACKGROUND FIRST ---
        if (day.position == DayPosition.MonthDate && weekHasWorkout) {
            // Determine the shape based on the day's position in the week
            val pillShape = when (day.date.dayOfWeek) {
                firstDayOfWeek -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
                firstDayOfWeek.plus(6) -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
                else -> RoundedCornerShape(0.dp)
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f) // Leave a small gap between weeks
                    .background(MaterialTheme.colorScheme.tertiaryContainer, shape = pillShape)
            )
        }

        // --- 2. DRAW THE DAY NUMBER AND DOT ON TOP ---
        // This Box ensures the content is aligned correctly within the cell
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // The text for the day number (e.g., "1", "25")
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (day.position != DayPosition.MonthDate) {
                    Color.Gray // Mute the color for days not in the current month
                } else if (day.date == LocalDate.now()) {
                    MaterialTheme.colorScheme.primary // Highlight today's date
                } else {
                    Color.Unspecified
                },
                fontWeight = if (hasWorkout) FontWeight.Bold else FontWeight.Normal
            )
            }
        // If a workout exists on this specific day, draw the dot.
        if (hasWorkout) {
            Box(
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
                color = MaterialTheme.colorScheme.onTertiary
            )
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

/**
 * Checks if the week containing the given day has any workouts.
 * @param day The specific day to check the week for.
 * @param workoutDates A set of all dates that have workouts.
 * @param firstDayOfWeek The first day of the week (e.g., Monday or Sunday).
 * @return True if at least one day in that week is in the workoutDates set.
 */
private fun weekHasWorkout(
    day: CalendarDay,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
): Boolean {
    // If the day is not a month date, it can't be part of a workout week.
    if (day.position != DayPosition.MonthDate) return false

    // Get the date of the first day of this week.
    var currentDay = day.date
    while (currentDay.dayOfWeek != firstDayOfWeek) {
        currentDay = currentDay.minusDays(1)
    }

    // Check every day from the start of the week for the next 7 days.
    for (i in 0..6) {
        if (currentDay.plusDays(i.toLong()) in workoutDates) {
            return true
        }
    }
    return false
}

private fun previousWeekHasWorkout(
    day: CalendarDay,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
): Boolean {
    // If the previous week has not workouts, return false.
    if (day.date.minusWeeks(1) !in workoutDates){
        return false
    }
    else {
        return true
    }
}

@Composable
@Preview(showBackground = true)
fun WorkoutCalendarPreview() {
    val fakeDates = setOf(LocalDate.of(2025, 7, 13), LocalDate.of(2025, 7, 8), LocalDate.of(2025, 7, 12), LocalDate.of(2025, 7, 23))
    WorkoutCalendar(
        workoutDates = fakeDates,
        onDayClicked = {},
        modifier = Modifier.padding(16.dp)
    )
}