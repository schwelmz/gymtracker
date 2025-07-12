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
import java.time.temporal.TemporalAdjusters
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
        DaysOfWeekTitle(daysOfWeek = state.firstDayOfWeek.let {
            val days = DayOfWeek.values()
            days.slice(days.indexOf(it)..days.lastIndex) + days.slice(0 until days.indexOf(it))
        })

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    hasWorkout = day.date in workoutDates,
                    onDayClicked = { onDayClicked(day.date) },
                    workoutDates = workoutDates,
                    firstDayOfWeek = firstDayOfWeek
                )
            }
        )
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    hasWorkout: Boolean,
    onDayClicked: (LocalDate) -> Unit,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Makes it a square
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onDayClicked(day.date) }
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. DRAW THE PILL BACKGROUND ---
        val firstWorkout = getFirstWorkoutOfWeek(day, workoutDates, firstDayOfWeek)
        if (day.position == DayPosition.MonthDate && firstWorkout != null) {
            val lastWorkout = getLastWorkoutOfWeek(day, workoutDates, firstDayOfWeek)!!
            val prevWeekHasWorkout = previousWeekHasWorkout(day, workoutDates, firstDayOfWeek)
            val nextWeekHasWorkout = nextWeekHasWorkout(day, workoutDates, firstDayOfWeek)

            // Determine the start and end dates for the visual pill background.
            val weekStartDate = day.date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            val weekEndDate = weekStartDate.plusDays(6)
            val pillStartDate = if (prevWeekHasWorkout) weekStartDate else firstWorkout
            val pillEndDate = if (nextWeekHasWorkout) weekEndDate else lastWorkout

            // Draw the background only if the current day is within the pill's range.
            if (day.date >= pillStartDate && day.date <= pillEndDate) {
                val pillShape = when {
                    // Single-day workout in a non-connected week
                    pillStartDate == pillEndDate -> RoundedCornerShape(50)
                    // The first day of the pill
                    day.date == pillStartDate -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
                    // The last day of the pill
                    day.date == pillEndDate -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
                    // A day in the middle of the pill
                    else -> RoundedCornerShape(0.dp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.tertiaryContainer, shape = pillShape)
                )
            }
        }

        // --- 2. DRAW THE DAY NUMBER AND DOT ---
        val isToday = day.date == LocalDate.now()
        val textColor = when {
            isToday -> MaterialTheme.colorScheme.primary
            day.position != DayPosition.MonthDate -> Color.Gray
            else -> Color.Unspecified
        }

        // The day number text
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (hasWorkout) Color.Transparent else textColor,
            fontWeight = if (!hasWorkout && isToday) FontWeight.Bold else FontWeight.Normal,
        )

        // If a workout exists, draw the filled circle and the number on top of it.
        if (hasWorkout) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (isToday) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y=-4.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary) // Current color
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

private fun getFirstWorkoutOfWeek(
    day: CalendarDay,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
): LocalDate? {
    if (day.position != DayPosition.MonthDate) return null
    val weekStartDate = day.date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    for (i in 0..6) {
        val date = weekStartDate.plusDays(i.toLong())
        if (date in workoutDates) {
            return date
        }
    }
    return null
}

private fun getLastWorkoutOfWeek(
    day: CalendarDay,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
): LocalDate? {
    if (day.position != DayPosition.MonthDate) return null
    val weekStartDate = day.date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    for (i in 6 downTo 0) {
        val date = weekStartDate.plusDays(i.toLong())
        if (date in workoutDates) {
            return date
        }
    }
    return null
}


private fun previousWeekHasWorkout(
    day: CalendarDay,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
): Boolean {
    val prevWeekDate = day.date.minusWeeks(1)
    val prevWeekDay = CalendarDay(prevWeekDate, DayPosition.MonthDate) // Create a dummy CalendarDay for the check
    return getFirstWorkoutOfWeek(prevWeekDay, workoutDates, firstDayOfWeek) != null
}

private fun nextWeekHasWorkout(
    day: CalendarDay,
    workoutDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek
): Boolean {
    val nextWeekDate = day.date.plusWeeks(1)
    val nextWeekDay = CalendarDay(nextWeekDate, DayPosition.MonthDate) // Create a dummy CalendarDay for the check
    return getFirstWorkoutOfWeek(nextWeekDay, workoutDates, firstDayOfWeek) != null
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