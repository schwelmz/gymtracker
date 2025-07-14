package com.example.gymtracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialTimestamp: Long,
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialTimestamp)
    var showDatePicker by remember { mutableStateOf(true) }
    var selectedDateMillis by remember { mutableStateOf(initialTimestamp) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        // User confirmed the date, now show the time picker
                        selectedDateMillis = datePickerState.selectedDateMillis ?: initialTimestamp
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        // Time Picker Logic
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )

        TimePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        // User confirmed the time, combine date and time and return the final timestamp
                        val finalCalendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDateMillis
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        onDateTimeSelected(finalCalendar.timeInMillis)
                        onDismiss()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

// Helper composable for the TimePickerDialog since it's not a standard dialog
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = { Box(modifier = Modifier.fillMaxWidth()) { content() } },
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}