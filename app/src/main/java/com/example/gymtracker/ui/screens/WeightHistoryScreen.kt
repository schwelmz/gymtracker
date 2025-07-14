package com.example.gymtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.WeightEntry
import com.example.gymtracker.viewmodel.WeightViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryScreen(
    onNavigateUp: () -> Unit,
    weightViewModel: WeightViewModel = viewModel(factory = WeightViewModel.Factory)
) {
    val weightEntries by weightViewModel.allWeightEntries.collectAsState(initial = emptyList())
    var showEntryDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<WeightEntry?>(null) }

    if (showEntryDialog) {
        WeightEntryDialog(
            initialEntry = selectedEntry,
            onDismiss = { showEntryDialog = false },
            onSave = { newDate, newWeight ->
                // If it was an edit, delete the original entry first
                // to handle cases where the date (primary key) changes.
                if (selectedEntry != null) {
                    weightViewModel.deleteWeight(selectedEntry!!)
                }
                weightViewModel.addOrUpdateWeight(newDate, newWeight)
                showEntryDialog = false
            },
            onDelete = { entry ->
                weightViewModel.deleteWeight(entry)
                showEntryDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedEntry = null // Ensure we are in "add" mode
                showEntryDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add new weight entry")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (weightEntries.isEmpty()) {
                item {
                    Text(
                        text = "No weight entries yet.\nTap the '+' button to add your first one.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            items(weightEntries, key = { it.date.toEpochDay() }) { entry ->
                WeightListItem(
                    entry = entry,
                    onClick = {
                        selectedEntry = entry
                        showEntryDialog = true
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightEntryDialog(
    initialEntry: WeightEntry?,
    onDismiss: () -> Unit,
    onSave: (LocalDate, Float) -> Unit,
    onDelete: (WeightEntry) -> Unit
) {
    val isEditing = initialEntry != null
    val initialDate = initialEntry?.date ?: LocalDate.now()
    val initialWeight = initialEntry?.let { "%.1f".format(it.weight) } ?: ""
    val dialogTitle = if (isEditing) "Edit Entry" else "Add Weight"

    var weight by remember { mutableStateOf(initialWeight) }
    var date by remember { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(date.format(formatter))
                }
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weightFloat = weight.toFloatOrNull()
                    if (weightFloat != null) {
                        onSave(date, weightFloat)
                    }
                },
                enabled = weight.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    IconButton(onClick = { onDelete(initialEntry!!) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
fun WeightListItem(entry: WeightEntry, onClick: () -> Unit) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy") }
    ListItem(
        headlineContent = { Text("%.1f kg".format(entry.weight), fontWeight = FontWeight.Bold) },
        supportingContent = { Text(entry.date.format(formatter)) },
        trailingContent = {
            Icon(Icons.Default.Edit, contentDescription = "Edit Entry")
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}