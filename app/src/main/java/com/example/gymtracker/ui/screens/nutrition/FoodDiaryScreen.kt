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
import com.example.gymtracker.ui.screens.workout.headlineBottomPadding
import com.example.gymtracker.ui.screens.workout.headlineTopPadding

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
                    LineChart(
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
fun LineChart(
    data: List<Pair<LocalDate, Int>>,
    calorieGoal: Int,
    calorieMode: CalorieMode,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val primaryColor = MaterialTheme.colorScheme.primary
    val goalColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val goalLabel = when (calorieMode) {
        CalorieMode.DEFICIT -> "Limit"
        CalorieMode.SURPLUS -> "Goal"
    }
    val labelStyle = TextStyle(fontSize = 12.sp, color = onSurfaceVariant)

    Canvas(modifier = modifier) {
        val yAxisLabelPadding = with(density) { 40.dp.toPx() }
        val xAxisLabelPadding = with(density) { 24.dp.toPx() }

        val chartWidth = size.width - yAxisLabelPadding
        val chartHeight = size.height - xAxisLabelPadding

        val maxCalories = (data.maxOfOrNull { it.second } ?: calorieGoal).toFloat().coerceAtLeast(calorieGoal.toFloat()) * 1.1f
        val numTicks = 4

        (0..numTicks).forEach { i ->
            val tickValue = maxCalories * i / numTicks
            val tickY = chartHeight - (tickValue / maxCalories) * chartHeight

            drawLine(
                color = onSurfaceVariant.copy(alpha = 0.2f),
                start = Offset(yAxisLabelPadding, tickY),
                end = Offset(size.width, tickY),
                strokeWidth = 1.dp.toPx()
            )
            val labelText = textMeasurer.measure(tickValue.roundToInt().toString(), style = labelStyle)
            drawText(
                textLayoutResult = labelText,
                topLeft = Offset(yAxisLabelPadding - labelText.size.width - 4.dp.toPx(), tickY - (labelText.size.height / 2))
            )
        }

        val goalY = chartHeight - (calorieGoal / maxCalories) * chartHeight
        val goalPath = Path().apply {
            moveTo(yAxisLabelPadding, goalY)
            lineTo(size.width, goalY)
        }
        drawPath(
            path = goalPath,
            color = goalColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
        )

        val goalLabelText = textMeasurer.measure(goalLabel, style = labelStyle.copy(color = goalColor, fontWeight = FontWeight.Bold))
        drawText(
            textLayoutResult = goalLabelText,
            topLeft = Offset(yAxisLabelPadding + 4.dp.toPx(), goalY - goalLabelText.size.height - 4.dp.toPx())
        )

        // --- DATA DRAWING LOGIC (REFACTORED) ---
        if (data.size == 1) {
            // --- HANDLE SINGLE, CENTERED DATA POINT ---
            val (date, calories) = data.first()
            val x = yAxisLabelPadding + chartWidth / 2 // Center the point
            val y = chartHeight - ((calories / maxCalories) * chartHeight)
            drawCircle(color = primaryColor, radius = 8f, center = Offset(x, y))
        } else if (data.size > 1) {
            // --- HANDLE MULTIPLE DATA POINTS (LINE, GRADIENT, DOTS) ---
            val linePath = Path()
            val fillPath = Path()
            val pointSpacing = chartWidth / (data.size - 1)

            val firstPointX = yAxisLabelPadding
            val firstPointY = chartHeight - ((data.first().second / maxCalories) * chartHeight)
            fillPath.moveTo(firstPointX, chartHeight)
            fillPath.lineTo(firstPointX, firstPointY)
            linePath.moveTo(firstPointX, firstPointY)

            data.forEachIndexed { index, (_, calories) ->
                val x = yAxisLabelPadding + (index * pointSpacing)
                val y = chartHeight - ((calories / maxCalories) * chartHeight)
                if (index != 0) linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            fillPath.lineTo(yAxisLabelPadding + chartWidth, chartHeight)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = chartHeight
                )
            )
            drawPath(path = linePath, color = primaryColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
            data.forEachIndexed { index, (_, calories) ->
                val x = yAxisLabelPadding + (index * pointSpacing)
                val y = chartHeight - ((calories / maxCalories) * chartHeight)
                drawCircle(color = primaryColor, radius = 8f, center = Offset(x, y))
            }
        }

        // --- X-AXIS LABEL LOGIC (REFACTORED) ---
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
        if (data.size == 1) {
            // --- SINGLE, CENTERED LABEL ---
            val date = data.first().first
            val labelText = textMeasurer.measure(date.format(dateFormatter), style = labelStyle)
            val x = yAxisLabelPadding + (chartWidth / 2) - (labelText.size.width / 2)
            val y = size.height - (labelText.size.height)
            drawText(textLayoutResult = labelText, topLeft = Offset(x, y))
        } else if (data.size > 1) {
            // --- INTELLIGENTLY SPACED LABELS ---
            val numLabels = (data.size - 1).coerceAtMost(4).coerceAtLeast(1)
            (0..numLabels).forEach { i ->
                val dataIndex = (i * (data.size - 1) / numLabels)
                val date = data[dataIndex].first
                val labelText = textMeasurer.measure(date.format(dateFormatter), style = labelStyle)
                val pointSpacing = chartWidth / (data.size - 1)
                val x = yAxisLabelPadding + (dataIndex * pointSpacing) - (labelText.size.width / 2)
                val y = size.height - (labelText.size.height)
                drawText(textLayoutResult = labelText, topLeft = Offset(x, y))
            }
        }
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