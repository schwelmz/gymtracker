package com.example.gymtracker.ui.screens.nutrition

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.data.model.CalorieMode
import com.example.gymtracker.data.model.DiaryEntry
import com.example.gymtracker.ui.components.FoodCard
import com.example.gymtracker.ui.components.RecipeLogCard
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import com.example.gymtracker.viewmodel.FoodViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.roundToInt

// Enum for Time Span Selection
enum class ChartTimeSpan(val days: Long, val title: String) {
    WEEK(7, "7 Days"),
    MONTH(30, "1 Month"),
    THREE_MONTHS(90, "3 Months"),
    ALL_TIME(Long.MAX_VALUE, "All Time")
}

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FoodDiaryScreen(
    viewModel: FoodViewModel,
    calorieGoal: Int,
    calorieMode: CalorieMode,
    onNavigateUp: () -> Unit
) {
    val foodHistory by viewModel.allDiaryHistory.collectAsState(initial = emptyList())
    var selectedTimeSpan by remember { mutableStateOf(ChartTimeSpan.WEEK) }
    var selectedBar by remember { mutableStateOf<Pair<LocalDate, Int>?>(null) }

    val (groupedFoodHistory, chartData) = remember(foodHistory, selectedTimeSpan) {
        val filteredHistory = if (selectedTimeSpan == ChartTimeSpan.ALL_TIME) {
            foodHistory
        } else {
            val startDate = LocalDate.now().minusDays(selectedTimeSpan.days - 1)
            foodHistory.filter {
                !Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                    .isBefore(startDate)
            }
        }

        val dailyCalories = mutableMapOf<LocalDate, Int>()
        filteredHistory.forEach { entry ->
            val date = Instant.ofEpochMilli(entry.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            val calories = when (entry) {
                is DiaryEntry.Food -> entry.details.calories
                is DiaryEntry.Recipe -> entry.log.totalCalories
            }
            dailyCalories[date] = (dailyCalories[date] ?: 0) + calories
        }

        val grouped = filteredHistory.groupBy {
            Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSortedMap(compareByDescending { it })

        val chart = dailyCalories.entries.map { (date, cals) -> date to cals }.sortedBy { it.first }

        grouped to chart
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
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
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Food Diary",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            item {
                TimeSpanSelector(
                    selectedTimeSpan = selectedTimeSpan,
                    onTimeSpanSelected = {
                        selectedBar = null
                        selectedTimeSpan = it
                    }
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
                        selectedBar = selectedBar,
                        onBarClick = { bar ->
                            selectedBar = if (selectedBar == bar) null else bar
                        },
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

            item { HorizontalDivider(modifier = Modifier.padding(top = 8.dp)) }

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

                items(logs, key = { it.id }) { log ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        when (log) {
                            is DiaryEntry.Food -> FoodCard(foodLog = log.details, onLongPress = {})
                            is DiaryEntry.Recipe -> RecipeLogCard(recipeLog = log.log, onLongPress = {})
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(64.dp)) }
        }
    }
}

@Composable
fun TimeSpanSelector(
    selectedTimeSpan: ChartTimeSpan,
    onTimeSpanSelected: (ChartTimeSpan) -> Unit
) {
    TabRow(selectedTabIndex = ChartTimeSpan.values().indexOf(selectedTimeSpan)) {
        ChartTimeSpan.values().forEach { timeSpan ->
            Tab(
                selected = selectedTimeSpan == timeSpan,
                onClick = { onTimeSpanSelected(timeSpan) },
                text = { Text(timeSpan.title) }
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun BarChart(
    data: List<Pair<LocalDate, Int>>,
    calorieGoal: Int,
    calorieMode: CalorieMode,
    selectedBar: Pair<LocalDate, Int>?,
    onBarClick: (Pair<LocalDate, Int>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVarColor = MaterialTheme.colorScheme.onSurfaceVariant

    // --- THIS IS THE CHANGE ---
    // Use the same colors as the HomeScreen for consistency
    val successColor = Color(0xFF8BC34A)
    val failureColor = MaterialTheme.colorScheme.error
    // --- END OF CHANGE ---

    val goalLabel = if (calorieMode == CalorieMode.DEFICIT) "Limit" else "Goal"
    val labelStyle = TextStyle(fontSize = 12.sp, color = onSurfaceVarColor)
    val barRects = remember { mutableStateListOf<Pair<Rect, Pair<LocalDate, Int>>>() }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val clickedBar = barRects.firstOrNull { (rect, _) -> rect.contains(offset) }
                onBarClick(clickedBar?.second)
            }
        }
    ) {
        barRects.clear()
        val yAxisLabelPadding = with(density) { 40.dp.toPx() }
        val xAxisLabelPadding = with(density) { 24.dp.toPx() }
        val chartWidth = size.width - yAxisLabelPadding
        val chartHeight = size.height - xAxisLabelPadding
        val maxDataValue = data.maxOfOrNull { it.second }?.toFloat() ?: 0f
        val maxValue = getNiceMaxValue(maxOf(maxDataValue, calorieGoal.toFloat()) * 1.1f)
        val goalY = chartHeight - (calorieGoal.toFloat() / maxValue) * chartHeight
        val barWidth = chartWidth / (data.size * 1.5f)
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM")

        data.forEachIndexed { index, (date, calories) ->
            val barX = yAxisLabelPadding + index * (barWidth * 1.5f)
            val barY = chartHeight - (calories.toFloat() / maxValue) * chartHeight
            val topY = minOf(goalY, barY)
            val barHeight = (goalY - barY).absoluteValue

            // --- THIS IS THE CHANGE ---
            // The logic is now based on the new consistent colors
            val barColor = (if (calorieMode == CalorieMode.DEFICIT) {
                if (calories <= calorieGoal) successColor else failureColor
            } else {
                if (calories >= calorieGoal) successColor else failureColor
            }).copy(alpha = 0.4f)
            // --- END OF CHANGE ---

            val barRect = Rect(topLeft = Offset(barX, 0f), bottomRight = Offset(barX + barWidth, chartHeight))
            barRects.add(barRect to (date to calories))

            drawRect(color = barColor, topLeft = Offset(barX, topY), size = Size(barWidth, barHeight.coerceAtLeast(2f)))
            val tickY = chartHeight - (calories.toFloat() / maxValue) * chartHeight
            drawLine(color = barColor.copy(alpha = 1f), start = Offset(barX, tickY), end = Offset(barX + barWidth, tickY), strokeWidth = 2.dp.toPx())
            val labelText = textMeasurer.measure(date.format(dateFormatter), style = labelStyle)
            drawText(labelText, topLeft = Offset(barX + (barWidth - labelText.size.width) / 2, size.height - labelText.size.height))
        }

        (0..5).forEach { i ->
            val value = i * (maxValue.toFloat() / 5)
            val y = chartHeight - (value / maxValue) * chartHeight
            drawLine(color = onSurfaceVarColor.copy(alpha = 0.3f), start = Offset(yAxisLabelPadding, y), end = Offset(size.width, y), strokeWidth = 1.dp.toPx())
            val labelText = textMeasurer.measure(value.roundToInt().toString(), style = labelStyle)
            drawText(labelText, topLeft = Offset(yAxisLabelPadding - labelText.size.width - 4.dp.toPx(), y - labelText.size.height / 2))
        }

        drawLine(color = primaryColor.copy(alpha = 0.8f), start = Offset(yAxisLabelPadding, goalY), end = Offset(size.width, goalY), strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
        val goalLabelText = textMeasurer.measure(goalLabel, style = labelStyle.copy(fontWeight = FontWeight.Bold, color = primaryColor))
        drawText(goalLabelText, topLeft = Offset(yAxisLabelPadding + 4.dp.toPx(), goalY - goalLabelText.size.height - 4.dp.toPx()))

        selectedBar?.let { (date, calories) ->
            val selectedIndex = data.indexOfFirst { it.first == date }
            if (selectedIndex != -1) {
                val barX = yAxisLabelPadding + selectedIndex * (barWidth * 1.5f)
                val barY = chartHeight - (calories.toFloat() / maxValue) * chartHeight
                val valueText = textMeasurer.measure(calories.toString(), style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor))
                val textBgSize = Size(valueText.size.width + 16.dp.toPx(), valueText.size.height + 8.dp.toPx())
                val textOffset = Offset(x = barX + (barWidth - textBgSize.width) / 2, y = barY - textBgSize.height - 4.dp.toPx())
                drawRoundRect(color = primaryColor.copy(alpha = 0.9f), topLeft = textOffset, size = textBgSize, cornerRadius = CornerRadius(4.dp.toPx()))
                drawText(valueText, topLeft = Offset(textOffset.x + 8.dp.toPx(), textOffset.y + 4.dp.toPx()))
            }
        }
    }
}

private fun getNiceMaxValue(value: Float): Int {
    if (value <= 0f) return 500
    val step = when {
        value <= 1000 -> 100
        value <= 2500 -> 250
        value <= 5000 -> 500
        else -> 1000
    }
    return (ceil(value / step) * step).toInt()
}

private fun formatDateHeader(date: LocalDate): String {
    val today = LocalDate.now()
    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
    }
}