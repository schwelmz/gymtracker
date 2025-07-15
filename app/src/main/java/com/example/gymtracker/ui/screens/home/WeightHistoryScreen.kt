package com.example.gymtracker.ui.screens.home

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.data.model.WeightEntry
import com.example.gymtracker.viewmodel.WeightViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.Shapes
import kotlinx.coroutines.delay

enum class TimeFilter { SevenDays, OneMonth, ThreeMonths, All }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryScreen(
    onNavigateUp: () -> Unit,
    weightViewModel: WeightViewModel = viewModel(factory = WeightViewModel.Factory)
) {
    val weightEntries by weightViewModel.allWeightEntries.collectAsState(initial = emptyList())
    var showEntryDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<WeightEntry?>(null) }
    var timeFilter by remember { mutableStateOf(TimeFilter.All) }

    val filteredEntries = remember(weightEntries, timeFilter) {
        val now = LocalDate.now()
        when (timeFilter) {
            TimeFilter.SevenDays -> weightEntries.filter { !it.date.isBefore(now.minusDays(7)) }
            TimeFilter.OneMonth -> weightEntries.filter { !it.date.isBefore(now.minusMonths(1)) }
            TimeFilter.ThreeMonths -> weightEntries.filter { !it.date.isBefore(now.minusMonths(3)) }
            TimeFilter.All -> weightEntries
        }
    }

    if (showEntryDialog) {
        WeightEntryDialog(
            initialEntry = selectedEntry,
            onDismiss = { showEntryDialog = false },
//            onSave = { newDate, newWeight, imagePath ->
//                // This logic now ONLY handles saving the data. It no longer closes the dialog.
//                // This allows for auto-saving in the background.
//                val entryToUpdate = selectedEntry
//                if (entryToUpdate != null && entryToUpdate.date != newDate) {
//                    weightViewModel.deleteWeight(entryToUpdate)
//                }
//                weightViewModel.addOrUpdateWeight(newDate, newWeight, imagePath)
//            }
            onSave = { newDate, newWeight, imagePath ->
                selectedEntry?.let { oldEntry ->
                    // If the date was changed, delete the old one
                    if (oldEntry.date != newDate) {
                        weightViewModel.deleteWeight(oldEntry)
                    }
                }

                // Always add/update the new data
                weightViewModel.addOrUpdateWeight(
                    date = newDate,
                    weight = newWeight,
                    imageUri = imagePath
                )
            }
            ,
            onDelete = { entry ->
                // This logic now ONLY handles deleting the data.
                entry.imageUri?.let { path ->
                    try {
                        File(path).delete()
                    } catch (e: Exception) {
                        // Log or handle error
                    }
                }
                weightViewModel.deleteWeight(entry)
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
                selectedEntry = null
                showEntryDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add new weight entry")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            WeightChart(entries = weightEntries, timeFilter = timeFilter, onTimeFilterChanged = { timeFilter = it })
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredEntries.isEmpty()) {
                    item {
                        Text(
                            text = "No weight entries in this period.\nTap the '+' button to add one.",
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(filteredEntries, key = { it.date.toEpochDay() }) { entry ->
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
}


private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val imagesDir = File(context.filesDir, "weight_images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val outputFile = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outputFile).use { output ->
            inputStream.use { input ->
                input.copyTo(output)
            }
        }
        outputFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightEntryDialog(
    initialEntry: WeightEntry?,
    onDismiss: () -> Unit,
    onSave: (LocalDate, Float, String?) -> Unit,
    onDelete: (WeightEntry) -> Unit
) {
    val isEditing = initialEntry != null
    val dialogTitle = if (isEditing) "Edit Entry" else "Add Weight"
    val context = LocalContext.current

    val initialWeight = initialEntry?.weight
    val initialDate = initialEntry?.date ?: LocalDate.now()
    val initialImagePath = initialEntry?.imageUri

    var weightInput by remember { mutableStateOf(initialWeight?.toString() ?: "") }
    var date by remember { mutableStateOf(initialDate) }
    var imagePath by remember { mutableStateOf(initialImagePath) }

    val parsedWeight = weightInput.toFloatOrNull()
    val weightIsValid = parsedWeight != null

    val hasChanges = weightIsValid && (
            parsedWeight != initialWeight ||
                    date != initialDate ||
                    imagePath != initialImagePath
            )

    val isSaveEnabled = hasChanges

    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }

    // --- Auto-Save Logic ---
    LaunchedEffect(parsedWeight, date, imagePath) {
        if (isEditing && isSaveEnabled) {
            delay(1000)
            onSave(date, parsedWeight!!, imagePath)
        }
    }

    // --- End Auto-Save Logic ---

    val tempImageFile = remember {
        File.createTempFile("temp_image", ".jpg", context.cacheDir).apply { deleteOnExit() }
    }
    val tempImageUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", tempImageFile)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imagePath = copyUriToInternalStorage(context, it) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { imagePath = copyUriToInternalStorage(context, tempImageUri) }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) { cameraLauncher.launch(tempImageUri) }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AsyncImage(
                    model = imagePath?.let { File(it) },
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.placeholder_image)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }, modifier = Modifier.weight(1f)) {
                        Icon(painter = painterResource(id = R.drawable.photocamera_icon), contentDescription = "Camera", modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Camera")
                    }
                    Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.weight(1f)) {
                        Icon(painter = painterResource(id = R.drawable.photolibrary_icon), contentDescription = "Gallery", modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Gallery")
                    }
                }
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(date.format(formatter))
                }
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it.filter { c -> c.isDigit() || c == '.' } },
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
                    parsedWeight?.let {
                        onSave(date, it, imagePath)
                        onDismiss()
                    }
                },
                enabled = isSaveEnabled
            ) { Text("Save") }
        },
        dismissButton = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (isEditing) {
                    IconButton(onClick = {
                        // Manually delete and then dismiss the dialog.
                        onDelete(initialEntry!!)
                        onDismiss()
                    }) {
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
        leadingContent = {
            AsyncImage(
                model = entry.imageUri?.let { File(it) },
                contentDescription = "Weight entry image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_image)
            )
        },
        trailingContent = {
            Icon(Icons.Default.Edit, contentDescription = "Edit Entry")
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}


@Composable
fun WeightChart(
    entries: List<WeightEntry>,
    timeFilter: TimeFilter,
    onTimeFilterChanged: (TimeFilter) -> Unit
) {
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM") }

    val (chartEntries, bottomAxisValues) = remember(entries, timeFilter) {
        val now = LocalDate.now()
        val filtered = when (timeFilter) {
            TimeFilter.SevenDays -> entries.filter { !it.date.isBefore(now.minusDays(7)) }
            TimeFilter.OneMonth -> entries.filter { !it.date.isBefore(now.minusMonths(1)) }
            TimeFilter.ThreeMonths -> entries.filter { !it.date.isBefore(now.minusMonths(3)) }
            TimeFilter.All -> entries
        }.sortedBy { it.date }

        val chartData = filtered.mapIndexed { index, entry -> entryOf(index.toFloat(), entry.weight) }
        val axisLabels = filtered.map { it.date.format(formatter) }
        Pair(chartData, axisLabels)
    }

    LaunchedEffect(chartEntries) {
        if (chartEntries.isNotEmpty()) {
            chartEntryModelProducer.setEntries(chartEntries)
        } else {
            chartEntryModelProducer.setEntries(listOf(entryOf(0f, 0f)))
        }
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        bottomAxisValues.getOrNull(value.toInt()) ?: ""
    }

    val maxY = remember(entries) {
        val maxWeight = entries.maxOfOrNull { it.weight }
        if (maxWeight != null && maxWeight > 0f) {
            maxWeight * 1.1f
        } else {
            100f
        }
    }

    val chart = lineChart(
        lines = listOf(
            LineChart.LineSpec(
                lineColor = currentChartStyle.lineChart.lines[0].lineColor,
                point = shapeComponent(
                    shape = Shapes.pillShape,
                    color = MaterialTheme.colorScheme.primary
                ),
                pointSizeDp = 8f
            )
        ),
        axisValuesOverrider = AxisValuesOverrider.fixed(minY = 0f, maxY = maxY)
    )

    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Chart(
                chart = chart,
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
                modifier = Modifier.height(200.dp)
            )
            SegmentedButtonRow(timeFilter, onTimeFilterChanged)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonRow(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        TimeFilter.values().forEach { filter ->
            SegmentedButton(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                shape = CircleShape,
                label = {
                    Text(when(filter) {
                        TimeFilter.SevenDays -> "7D"
                        TimeFilter.OneMonth -> "1M"
                        TimeFilter.ThreeMonths -> "3M"
                        TimeFilter.All -> "All"
                    })
                }
            )
        }
    }
}