// in ui/screens/StatsScreen.kt
package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Column
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
import com.patrykandpatrick.vico.core.component.text.textComponent

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
    val axisTitleTextComponent = textComponent {}
    // Calculate min and max Y values for the first chart

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(text = "Progress for $exerciseName", style = MaterialTheme.typography.headlineMedium)

        if (chartModelProducer != null && maxWeightChartModelProducer != null) {
            Chart(
                chart = lineChart(
                    lines = listOf(
                        lineSpec(
                            lineColor = MaterialTheme.colorScheme.primary,
                            pointConnector = DefaultPointConnector(0f),
                            point = shapeComponent(
                                shape = Shapes.pillShape,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            pointSize = 6.dp
                        )
                    )
                ),

                chartModelProducer = chartModelProducer,
                startAxis = rememberStartAxis(
                    title = "Total Volume (kg)",
                    titleComponent = axisTitleTextComponent,
                    valueFormatter = { value, _ -> value.toInt().toString() }
                ),
                bottomAxis = rememberBottomAxis(
                    title = "Workout Session",
                    valueFormatter = { value, _ ->
                        // Display date on X-axis
                        val index = value.toInt()
                        if (index in sessions.indices) {
                            val date = sessions[index].date
                            SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                        } else {
                            ""
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Chart(
                chart = lineChart(
                    lines = listOf(
                        lineSpec(
                            lineColor = MaterialTheme.colorScheme.primary,
                            pointConnector = DefaultPointConnector(0f),
                            point = shapeComponent(
                                shape = Shapes.pillShape,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            pointSize = 6.dp
                        )
                    )
                ),

                chartModelProducer = maxWeightChartModelProducer,
                startAxis = rememberStartAxis(
                    title = "Max. Volume (kg)",
                    titleComponent = axisTitleTextComponent,
                    valueFormatter = { value, _ -> value.toInt().toString() }
                ),
                bottomAxis = rememberBottomAxis(
                    title = "Workout Session",
                    valueFormatter = { value, _ ->
                        // Display date on X-axis
                        val index = value.toInt()
                        if (index in sessions.indices) {
                            val date = sessions[index].date
                            SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                        } else {
                            ""
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        } else {
            Text("Not enough data to display a chart.")
        }
    }
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

