package com.example.gymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@Composable
fun CustomWheelPicker(
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialValue: String,
    itemHeight: Dp = 48.dp,
    visibleItems: Int = 3
) {
    val pickerHeight = itemHeight * visibleItems
    val itemList = remember { mutableStateListOf<String>().apply { addAll(items) } }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    // Scroll to initial value once
    LaunchedEffect(Unit) {
        if (!initialized) {
            val index = itemList.indexOf(initialValue).coerceAtLeast(0)
            listState.scrollToItem(index)
            onItemSelected(itemList[index])
            initialized = true
        }
    }

    // Snap to center and notify onItemSelected
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            delay(50) // give layout a tiny moment to stabilize
            val layoutInfo = listState.layoutInfo
            val center = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
            val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                abs((it.offset + it.size / 2) - center)
            } ?: return@LaunchedEffect

            coroutineScope.launch {
                listState.scrollToItem(centerItem.index)
                onItemSelected(itemList[centerItem.index])
            }
        }
    }

    // UI
    Box(modifier = modifier.height(pickerHeight)) {
        // Highlight line
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.large)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = (pickerHeight - itemHeight) / 2)
        ) {
            items(itemList.size) { index ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                editIndex = index
                                inputText = itemList[index]
                                showDialog = true
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemList[index],
                        style = MaterialTheme.typography.bodyLarge.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )
                }
            }
        }

        // Top and bottom fades
        Box(
            Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.surface, Color.Transparent)
                    )
                )
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                    )
                )
        )
    }

    // Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Change Value") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = {
                        inputText = it.replace(',', '.')  // force '.' as decimal
                    },
                    label = { Text("New Value") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )

            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = inputText.toFloatOrNull()
                    if (parsed != null && editIndex in itemList.indices) {
                        val formatted = String.format(Locale.US, "%.2f", parsed)
                        itemList[editIndex] = formatted
                        showDialog = false
                        coroutineScope.launch {
                            listState.scrollToItem(editIndex)
                            onItemSelected(formatted)
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
