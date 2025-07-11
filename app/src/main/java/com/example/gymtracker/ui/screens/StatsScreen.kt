// in ui/screens/StatsScreen.kt
package com.example.gymtracker.ui.screens

import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.ExerciseSet
import com.example.gymtracker.data.WorkoutSession
import com.example.gymtracker.ui.theme.AppTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.chart.values.ChartValuesProvider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.context.DrawContext
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class) // Required for DatePicker
@Composable
fun StatsScreen(
    exerciseName: String,
    sessions: List<WorkoutSession>,
) {
    // --- 1. STATE MANAGEMENT FOR DATE RANGE ---

    // Find the earliest and latest dates from the session data to set as the initial range.
    val minDate = remember(sessions) { sessions.minOfOrNull { it.date } }
    val maxDate = remember(sessions) { sessions.maxOfOrNull { it.date } }

    var startDate by remember { mutableStateOf(minDate) }
    var endDate by remember { mutableStateOf(maxDate) }

    // State to control the visibility of the date pickers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // --- 2. FILTER DATA BASED ON THE SELECTED DATE RANGE ---

    val filteredSessions = remember(sessions, startDate, endDate) {
        sessions.filter { session ->
            // Use Calendar for reliable date comparisons
            val sessionCal = Calendar.getInstance().apply { time = session.date }
            val startCal = startDate?.let { Calendar.getInstance().apply { time = it } }
            val endCal = endDate?.let { Calendar.getInstance().apply { time = it } }

            // Normalize calendars to compare dates only, ignoring time.
            sessionCal.set(Calendar.HOUR_OF_DAY, 0); sessionCal.set(Calendar.MINUTE, 0); sessionCal.set(Calendar.SECOND, 0)
            startCal?.set(Calendar.HOUR_OF_DAY, 0); startCal?.set(Calendar.MINUTE, 0); startCal?.set(Calendar.SECOND, 0)
            endCal?.set(Calendar.HOUR_OF_DAY, 0); endCal?.set(Calendar.MINUTE, 0); endCal?.set(Calendar.SECOND, 0)

            val isAfterStart = startCal == null || !sessionCal.before(startCal)
            val isBeforeEnd = endCal == null || !sessionCal.after(endCal)
            isAfterStart && isBeforeEnd
        }
    }

    // --- CHART MODELS (based on the newly filtered data) ---
    val chartModelProducer = filteredSessions.takeIf { it.isNotEmpty() }?.let { sessionList ->
        val chartEntries = sessionList.mapIndexed { index, session ->
            val totalVolume = session.sets.sumOf { it.reps * it.weight }
            entryOf(index.toFloat(), totalVolume.toFloat())
        }
        ChartEntryModelProducer(chartEntries)
    }

    val maxWeightChartModelProducer = filteredSessions.takeIf { it.isNotEmpty() }?.let { sessionList ->
        val maxWeights = sessionList.map { session -> session.sets.maxOfOrNull { it.weight } ?: 0.0 }
        val chartEntries = maxWeights.mapIndexed { index, maxWeight -> entryOf(index.toFloat(), maxWeight.toFloat()) }
        ChartEntryModelProducer(chartEntries)
    }

    // --- UI COMPOSITION ---
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Progress for $exerciseName", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. UI FOR DATE SELECTION ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            Button(onClick = { showStartDatePicker = true }) {
                Text(text = "Start: ${startDate?.let(dateFormat::format) ?: "Any"}")
            }
            Button(onClick = { showEndDatePicker = true }) {
                Text(text = "End: ${endDate?.let(dateFormat::format) ?: "Any"}")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (chartModelProducer != null && maxWeightChartModelProducer != null) {
            val volumeMarkerFormatter = MarkerLabelFormatter { markedEntries, _ ->
                val index = markedEntries.first().entry.x.toInt()
                if (index in filteredSessions.indices) {
                    val sets = filteredSessions[index].sets
                    sets.joinToString("\n") { "R: ${it.reps}, W: ${it.weight}kg" }
                } else { "" }
            }
            createChart(
                filteredSessions,
                chartModelProducer,
                "Workout Session",
                "Total Volume (kg)",
                MaterialTheme.colorScheme.primary,
                volumeMarkerFormatter
            )

            val weightMarkerFormatter = MarkerLabelFormatter { markedEntries, _ ->
                "${markedEntries.first().entry.y.toInt()} kg"
            }
            createChart(
                filteredSessions,
                maxWeightChartModelProducer,
                "Workout Session",
                "Max Weight (kg)",
                MaterialTheme.colorScheme.tertiary,
                weightMarkerFormatter
            )
        } else {
            Text("No data available for the selected date range.")
        }
    }

    // --- DATE PICKER DIALOGS ---
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate?.time)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = datePickerState.selectedDateMillis?.let { Date(it) }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate?.time)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = datePickerState.selectedDateMillis?.let { Date(it) }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// The createChart and CustomVerticalPlacer functions remain unchanged.
@Composable
fun createChart(
    sessions: List<WorkoutSession>,
    chartModelProducer: ChartEntryModelProducer,
    xAxisTitle: String,
    yAxisTitle: String,
    lineColor: Color,
    markerFormatter: MarkerLabelFormatter // Accept the formatter as a parameter
) {
    val customAxisLineColor = MaterialTheme.colorScheme.onBackground
    val axisLine = LineComponent(
        color = customAxisLineColor.toArgb(),
        thicknessDp = 1f
    )
    val axisTitleTextComponent = textComponent { color = MaterialTheme.colorScheme.onBackground.toArgb() }

    val dataMaxY = chartModelProducer.getModel()?.maxY ?: 0f
    val chartMaxY = dataMaxY * 1.5f

    val step = if (chartMaxY > 0) chartMaxY / 8 else 1f
    val maxTickValue = ((chartMaxY + step - 1) / step).toInt()
    val ticks = (0..maxTickValue).map { it * step }

    val yPlacer = remember(ticks) { CustomVerticalPlacer(ticks) }

    val markerLabelColor = Color.White
    val markerBackgroundColor = lineColor
    val markerGuidelineColor = lineColor

    val marker = remember(markerLabelColor, markerBackgroundColor, markerGuidelineColor) {
        val label = textComponent {
            color = markerLabelColor.toArgb()
            lineCount = 3 // Allow for multi-line text
            background = ShapeComponent(
                shape = Shapes.roundedCornerShape(allPercent = 25), // A less circular bubble
                color = markerBackgroundColor.toArgb()
            )
            padding = MutableDimensions(horizontalDp = 8.dp.value, verticalDp = 4.dp.value)
            margins = MutableDimensions(startDp = 0f, topDp = 0f, endDp = 0f, bottomDp = 4.dp.value)
            typeface = Typeface.MONOSPACE
        }

        val guideline = LineComponent(
            color = markerGuidelineColor.toArgb(),
            thicknessDp = 2f,
        )

        object : Marker {
            // Use the passed-in formatter
            val formatter = markerFormatter

            override fun getInsets(
                context: MeasureContext,
                outInsets: Insets,
                horizontalDimensions: com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
            ) {
                outInsets.set(0f)
            }

            override fun draw(
                context: DrawContext,
                bounds: RectF,
                markedEntries: List<Marker.EntryModel>,
                chartValuesProvider: ChartValuesProvider
            ) {
                val markedEntry = markedEntries.firstOrNull() ?: return
                val entryX = markedEntry.location.x
                val entryY = markedEntry.location.y
                val bubbleSpacingFromLinePx = context.run { context.dpToPx(8f) }

                val chartValues = chartValuesProvider.getChartValues()
                val labelText = formatter.getLabel(markedEntries, chartValues)

                val labelBottomY = entryY - bubbleSpacingFromLinePx
                val guidelineTop = 64f
                val guidelineBottom = labelBottomY
                guideline.drawVertical(
                    context,
                    guidelineTop,
                    guidelineBottom,
                    entryX
                )
                label.drawText(
                    context = context,
                    text = labelText,
                    textX = entryX,
                    textY = 0f,
                    horizontalPosition = com.patrykandpatrick.vico.core.component.text.HorizontalPosition.Center,
                    verticalPosition = com.patrykandpatrick.vico.core.component.text.VerticalPosition.Bottom
                )


            }
        }
    }

    Chart(
        chart = lineChart(
            lines = listOf(
                lineSpec(
                    lineColor = lineColor,
                    pointConnector = DefaultPointConnector(0f),
                    point = shapeComponent(
                        shape = Shapes.pillShape,
                        color = lineColor
                    ),
                    pointSize = 6.dp
                )
            ),
            axisValuesOverrider = AxisValuesOverrider.fixed(maxY = chartMaxY)
        ),
        chartModelProducer = chartModelProducer,
        startAxis = rememberStartAxis(
            title = yAxisTitle,
            titleComponent = axisTitleTextComponent,
            valueFormatter = { value, _ -> value.toInt().toString() },
            axis = axisLine,
            label = axisTitleTextComponent,
            guideline = null,
            itemPlacer = yPlacer
        ),
        bottomAxis = rememberBottomAxis(
            title = xAxisTitle,
            valueFormatter = { value, _ ->
                val index = value.toInt()
                if (index in sessions.indices) {
                    val date = sessions[index].date
                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                } else {
                    ""
                }
            },
            axis = axisLine,
            titleComponent = axisTitleTextComponent,
            label = axisTitleTextComponent,
            guideline = null
        ),
        marker = marker,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

class CustomVerticalPlacer(private val tickValues: List<Float>) : AxisItemPlacer.Vertical {
    override fun getLabelValues(
        context: com.patrykandpatrick.vico.core.chart.draw.ChartDrawContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: AxisPosition.Vertical
    ): List<Float> = tickValues

    override fun getHeightMeasurementLabelValues(
        context: MeasureContext,
        position: AxisPosition.Vertical
    ): List<Float> = tickValues

    override fun getWidthMeasurementLabelValues(
        context: MeasureContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: AxisPosition.Vertical
    ): List<Float> = tickValues

    override fun getBottomVerticalAxisInset(
        verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
        maxLabelHeight: Float,
        maxLineThickness: Float
    ): Float =
        AxisItemPlacer.Vertical.default().getBottomVerticalAxisInset(verticalLabelPosition, maxLabelHeight, maxLineThickness)

    override fun getTopVerticalAxisInset(
        verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
        maxLabelHeight: Float,
        maxLineThickness: Float
    ): Float =
        AxisItemPlacer.Vertical.default().getTopVerticalAxisInset(verticalLabelPosition, maxLabelHeight, maxLineThickness)
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    val fakeSessions = listOf(
        WorkoutSession(1, "Benchpress", sets = listOf(ExerciseSet(10, 50.0)), Date()),
        WorkoutSession(2, "Benchpress", sets = listOf(ExerciseSet(13, 55.0)), Date()),
        WorkoutSession(3, "Benchpress", sets = listOf(ExerciseSet(16, 55.0)), Date()),
        WorkoutSession(4, "Benchpress", sets = listOf(ExerciseSet(17, 56.0)), Date()),
        WorkoutSession(5, "Benchpress", sets = listOf(ExerciseSet(23, 56.0)), Date())
    )
    AppTheme {
        StatsScreen("Benchpress", fakeSessions)
    }
}