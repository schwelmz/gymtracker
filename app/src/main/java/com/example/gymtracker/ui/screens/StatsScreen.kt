// in ui/screens/StatsScreen.kt
package com.example.gymtracker.ui.screens

import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.patrykandpatrick.vico.core.chart.values.ChartValues
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

@Composable
fun StatsScreen(
    exerciseName: String,
    sessions: List<WorkoutSession>,
) {
    val chartModelProducer = sessions.takeIf { it.isNotEmpty() }?.let { sessionList ->
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

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Progress for $exerciseName", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(40.dp))

        if (chartModelProducer != null && maxWeightChartModelProducer != null) {
            createChart(
                sessions,
                chartModelProducer,
                "Workout Session",
                "Total Volume (kg)",
                MaterialTheme.colorScheme.primary
            )
            createChart(
                sessions,
                maxWeightChartModelProducer,
                "Workout Session",
                "Max Weight (kg)",
                MaterialTheme.colorScheme.tertiary
            )
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
            background = ShapeComponent(
                shape = Shapes.roundedCornerShape(allPercent = 50),
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
            val formatter = MarkerLabelFormatter { markedEntries, chartValuesProvider ->
                markedEntries.first().entry.y.toInt().toString()
            }

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

                val chartValues = chartValuesProvider.getChartValues()
                val labelText = formatter.getLabel(markedEntries, chartValues)

                // Small vertical offset to float just above the point
                val yOffset = context.dpToPx(16f)

                label.drawText(
                    context,
                    labelText,
                    entryX,
                    8f,
                    com.patrykandpatrick.vico.core.component.text.HorizontalPosition.Center,
                    com.patrykandpatrick.vico.core.component.text.VerticalPosition.Bottom
                )

                // Draw guideline up to the point (not above bubble)
                guideline.drawVertical(
                    context,
                    64f,
                    entryY,
                    entryX
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