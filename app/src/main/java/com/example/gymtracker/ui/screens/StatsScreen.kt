// in ui/screens/StatsScreen.kt
package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.theme.AppTheme
import com.example.gymtracker.data.ExerciseSet
import com.example.gymtracker.data.WorkoutSession
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.draw.ChartDrawContext
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.context.MeasureContext

@Composable
fun StatsScreen(
    exerciseName: String,
    sessions: List<WorkoutSession>,
    ) {
    //val sessions by viewModel.getSessionsForChart(exerciseName).collectAsState(initial = emptyList())

    // Vico requires a ChartEntryModelProducer
    val chartModelProducer = sessions.takeIf { it.isNotEmpty() }?.let { sessionList ->
        // Calculate total volume (sets * reps * weight) for each session
        val chartEntries = sessionList.mapIndexed { index, session ->
            val totalVolume = session.sets.sumOf { it.reps * it.weight }
            entryOf(index.toFloat(), totalVolume.toFloat())
        }
        ChartEntryModelProducer(chartEntries)
    }
    val maxWeightChartModelProducer = sessions.takeIf { it.isNotEmpty() }?.let { sessionList ->
        val maxWeights = sessionList.map { session ->
            session.sets.maxOf { it.weight }
        }
        val chartEntries = maxWeights.mapIndexed { index, maxWeight ->
            entryOf(index.toFloat(), maxWeight.toFloat())
        }
        ChartEntryModelProducer(chartEntries)
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(text = "Progress for $exerciseName", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(40.dp))

            if (chartModelProducer != null && maxWeightChartModelProducer != null) {
                createChart(sessions,
                    chartModelProducer,
                    "Workout Session",
                    "Total Volume (kg)",
                    MaterialTheme.colorScheme.primary)
                createChart(sessions,
                    maxWeightChartModelProducer,
                    "Workout Session",
                    "Max Weight (kg)",
                    MaterialTheme.colorScheme.tertiary)
            } else {
                Text("Not enough data to display a chart.")
            }
    }
}

@Composable
fun createChart(
    sessions: List<WorkoutSession>,
    chartModelProducer: ChartEntryModelProducer,
    xAxisTitle: String,
    yAxisTitle: String,
    lineColor: Color
) {
    val customAxisLineColor = MaterialTheme.colorScheme.onBackground // Or any color you want
    val axisLine = LineComponent(
        color = customAxisLineColor.toArgb(), // Vico core components often use ARGB integers for color
        thicknessDp = 1f // Adjust thickness as needed
    )
    val axisTitleTextComponent = textComponent {color = MaterialTheme.colorScheme.onBackground.toArgb()}

    val maxY = chartModelProducer.getModel()!!.maxY
    val step = maxY/8
    //val step = 150f
    val maxTickValue = ((maxY + step - 1) / step).toInt()  // ceiling-ish
    val ticks = (0..maxTickValue).map { it * step }

    val yPlacer = remember { CustomVerticalPlacer(ticks) }

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
            )
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
                // Display date on X-axis
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
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

class CustomVerticalPlacer(private val tickValues: List<Float>) : AxisItemPlacer.Vertical {
    override fun getLabelValues(
        context: ChartDrawContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: AxisPosition.Vertical
    ): List<Float> {
        return tickValues
    }

    override fun getHeightMeasurementLabelValues(
        context: MeasureContext,
        position: AxisPosition.Vertical
    ): List<Float> {
        return tickValues
    }

    override fun getWidthMeasurementLabelValues(
        context: MeasureContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: AxisPosition.Vertical
    ): List<Float> {
        return tickValues
    }

    override fun getBottomVerticalAxisInset(
        verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
        maxLabelHeight: Float,
        maxLineThickness: Float
    ): Float =
        AxisItemPlacer.Vertical.default()
            .getBottomVerticalAxisInset(verticalLabelPosition, maxLabelHeight, maxLineThickness)

    override fun getTopVerticalAxisInset(
        verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
        maxLabelHeight: Float,
        maxLineThickness: Float
    ): Float =
        AxisItemPlacer.Vertical.default()
            .getTopVerticalAxisInset(verticalLabelPosition, maxLabelHeight, maxLineThickness)

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

