package com.example.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.WorkoutViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutModifyScreen(
    sessionId: Int,
    onWorkoutModified: () -> Unit
) {
    val viewModel: WorkoutViewModel = viewModel()
    val session by viewModel.getSessionById(sessionId).collectAsState()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = session.date.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp, top = 100.dp)
    ) {
        DatePicker(
            state = datePickerState
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            datePickerState.selectedDateMillis?.let {
                viewModel.updateSession(session.copy(date = Date(it)))
            }
            onWorkoutModified()
        }) {
            Text("Save Changes")
        }

    }
}