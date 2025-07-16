package com.example.gymtracker.ui.screens.nutrition

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.data.model.CalorieMode
import com.example.gymtracker.data.dao.FoodLogWithDetails
import com.example.gymtracker.ui.components.FoodCard
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import com.example.gymtracker.ui.components.DateTimePickerDialog
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import kotlin.math.absoluteValue

// Enum for Time Span Selection
enum class ChartTimeSpan(val days: Long, val title: String) {
    WEEK(7, "7 Days"),
    MONTH(30, "1 Month"),
    THREE_MONTHS(90, "3 Months"),
    ALL_TIME(Long.MAX_VALUE, "All Time")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun FoodDiaryScreen(
    viewModel: FoodViewModel,
    calorieGoal: Int,
    calorieMode: CalorieMode,
    onNavigateUp: () -> Unit
) {
    val foodHistory by viewModel.allFoodHistory.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showOptionsDialog by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var selectedLog by remember { mutableStateOf<FoodLogWithDetails?>(null) }
    var selectedTimeSpan by remember { mutableStateOf(ChartTimeSpan.WEEK) }
    var showGramsEditor by remember { mutableStateOf(false) }

    if (showOptionsDialog && selectedLog != null) {
        OptionsDialog(
            foodName = selectedLog!!.name,
            onDismiss = { showOptionsDialog = false },
            onEditClick = {
                showOptionsDialog = false
                showDateTimePicker = true
            },
            onEditGramsClick = {
                showOptionsDialog = false
                showGramsEditor = true
            },
            onDeleteClick = {
                scope.launch { viewModel.deleteFoodLog(selectedLog!!.logId) }
                showOptionsDialog = false
            }
        )
    }
    if (showGramsEditor && selectedLog != null) {
        GramsEditDialog(
            initialGrams = selectedLog!!.grams,
            onDismiss = { showGramsEditor = false },
            onSave = { newGrams ->
                viewModel.updateLogGrams(selectedLog!!.logId, newGrams)
                showGramsEditor = false
            }
        )
    }
    if (showDateTimePicker && selectedLog != null) {
        DateTimePickerDialog(
            initialTimestamp = selectedLog!!.timestamp,
            onDismiss = { showDateTimePicker = false },
            onDateTimeSelected = { newTimestamp ->
                viewModel.updateLogTimestamp(selectedLog!!.logId, newTimestamp)
                showDateTimePicker = false
            }
        )
    }

    // --- THIS IS THE CORRECTED LOGIC ---
    // Both `groupedFoodHistory` and `chartData` are now directly derived from the
    // source `foodHistory` and the `selectedTimeSpan` state. When the tabs change
    // `selectedTimeSpan`, both of these will be correctly recalculated.
    val (groupedFoodHistory, chartData) = remember(foodHistory, selectedTimeSpan) {
        val filteredHistory = if (selectedTimeSpan == ChartTimeSpan.ALL_TIME) {
            foodHistory
        } else {
            // For a "7 Day" period, we want today plus the previous 6 days.
            // So the start date is 6 days ago.
            val startDate = LocalDate.now().minusDays(selectedTimeSpan.days - 1)
            // The filter should be inclusive of the start date.
            foodHistory.filter {
                !Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                    .isBefore(startDate)
            }
        }

        val grouped = filteredHistory.groupBy { log ->
            Instant.ofEpochMilli(log.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSortedMap(compareByDescending { it })

        val chart = grouped.entries.map { (date, logs) ->
            date to logs.sumOf { it.calories }
        }.reversed()

        grouped to chart
    }
    // --- END OF CORRECTION ---

    Scaffold { padding ->
        LazyColumn(
            //modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
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
                        text = "Food Diary",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary
                        // textAlign can be removed if the Box handles the alignment
                    )
                }
            }
            item {
                TimeSpanSelector(
                    selectedTimeSpan = selectedTimeSpan,
                    onTimeSpanSelected = { selectedTimeSpan = it }
                )
            }

            item {
                Text(
                    text = "Calorie History",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
            item {
                if (chartData.isNotEmpty()) {
                    BarChart(
                        data = chartData,
                        calorieGoal = calorieGoal,
                        calorieMode = calorieMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Log your first meal to see the chart.")
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }

            groupedFoodHistory.forEach { (date, logs) ->
                item {
                    Text(
                        text = formatDateHeader(date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }

                items(logs, key = { it.logId }) { log ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FoodCard(
                            foodLog = log,
                            onLongPress = {
                                selectedLog = log
                                showOptionsDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GramsEditDialog(
    initialGrams: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var grams by remember { mutableStateOf(initialGrams.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Grams") },
        text = {
            OutlinedTextField(
                value = grams,
                onValueChange = { grams = it.filter { char -> char.isDigit() } },
                label = { Text("New weight (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val newGrams = grams.toIntOrNull()
                    if (newGrams != null) {
                        onSave(newGrams)
                    }
                },
                enabled = grams.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun TimeSpanSelector(
    selectedTimeSpan: ChartTimeSpan,
    onTimeSpanSelected: (ChartTimeSpan) -> Unit
) {
    val timeSpans = ChartTimeSpan.values()
    TabRow(selectedTabIndex = timeSpans.indexOf(selectedTimeSpan)) {
        timeSpans.forEach { timeSpan ->
            Tab(
                selected = selectedTimeSpan == timeSpan,
                onClick = { onTimeSpanSelected(timeSpan) },
                text = { Text(timeSpan.title) }
            )
        }
    }
}
@Composable
private fun OptionsDialog(
    foodName: String,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onEditGramsClick: ()->Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(foodName) },
        text = { Text("What would you like to do?") },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        confirmButton = {
            Row{
                TextButton(onClick = onEditGramsClick) { Text("Edit Grams") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onEditClick) { Text("Edit Time") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDeleteClick) { Text("Delete") }
            }
        }
    )
}
/**
 * The final, beautifully rendered Line Chart.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun BarChart(
    data: List<Pair<LocalDate, Int>>,
    calorieGoal: Int,
    calorieMode: CalorieMode,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val col = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
    val greenColor = Color(0xFF4CAF50)
    val redColor = Color(0xFFF44336)

    val goalLabel = when (calorieMode) {
        CalorieMode.DEFICIT -> "Limit"
        CalorieMode.SURPLUS -> "Goal"
    }

    val labelStyle = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

    Canvas(modifier = modifier) {
        val yAxisLabelPadding = with(density) { 40.dp.toPx() }
        val xAxisLabelPadding = with(density) { 24.dp.toPx() }

        val chartWidth = size.width - yAxisLabelPadding
        val chartHeight = size.height - xAxisLabelPadding
        val calorieValues = data.map { it.second }

        val maxDataValue = calorieValues.maxOrNull()?.toFloat() ?: 0f
        val maxValue = maxOf(maxDataValue, calorieGoal.toFloat()) * 1.1f

        val goalY = chartHeight - (calorieGoal / maxValue) * chartHeight
        val barWidth = chartWidth / (data.size * 1.5f)

        val dateFormatter = DateTimeFormatter.ofPattern("d MMM")

        // 1. Draw bars (with transparency) + calorie line inside
        data.forEachIndexed { index, (date, calories) ->
            val barX = yAxisLabelPadding + index * (barWidth * 1.5f)
            val barY = chartHeight - (calories / maxValue) * chartHeight
            val topY = minOf(goalY, barY)
            val barHeight = (goalY - barY).absoluteValue

            val barColor = when (calorieMode) {
                CalorieMode.DEFICIT -> if (calories <= calorieGoal) greenColor else redColor
                CalorieMode.SURPLUS -> if (calories >= calorieGoal) greenColor else redColor
            }.copy(alpha = 0.4f)

            drawRect(
                color = barColor,
                topLeft = Offset(barX, topY),
                size = Size(barWidth, barHeight.coerceAtLeast(2f))
            )

            // Draw calorie marker line (tick inside bar)
            val tickY = chartHeight - (calories / maxValue) * chartHeight
            drawLine(
                color = barColor.copy(alpha = 1f),
                start = Offset(barX, tickY),
                end = Offset(barX + barWidth, tickY),
                strokeWidth = 2.dp.toPx()
            )

            // X-axis label
            val labelText = textMeasurer.measure(date.format(dateFormatter), style = labelStyle)
            drawText(
                textLayoutResult = labelText,
                topLeft = Offset(
                    barX + (barWidth - labelText.size.width) / 2,
                    size.height - labelText.size.height
                )
            )
        }

        // 2. Draw Y-axis ticks and labels
        val tickCount = 5
        for (i in 0..tickCount) {
            val value = i * (maxValue / tickCount)
            val y = chartHeight - (value / maxValue) * chartHeight

            drawLine(
                color = col.copy(alpha = 0.3f),
                start = Offset(yAxisLabelPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )

            val labelText = textMeasurer.measure(value.roundToInt().toString(), style = labelStyle)
            drawText(
                textLayoutResult = labelText,
                topLeft = Offset(yAxisLabelPadding - labelText.size.width - 4.dp.toPx(), y - labelText.size.height / 2)
            )
        }

        // 3. Draw dashed goal/limit line LAST so it's on top of everything
        drawLine(
            color = col,
            start = Offset(yAxisLabelPadding, goalY),
            end = Offset(size.width, goalY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )

        val goalLabelText = textMeasurer.measure(goalLabel, style = labelStyle.copy(fontWeight = FontWeight.Bold, color = col))
        drawText(
            textLayoutResult = goalLabelText,
            topLeft = Offset(yAxisLabelPadding + 4.dp.toPx(), goalY - goalLabelText.size.height - 4.dp.toPx())
        )
    }

}



/**
 * A helper function to format date headers nicely.
 */
private fun formatDateHeader(date: LocalDate): String {
    val today = LocalDate.now()
    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
    }
}