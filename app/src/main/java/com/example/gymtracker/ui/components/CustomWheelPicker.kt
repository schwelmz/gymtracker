package com.example.gymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
    val pickerHeight = itemHeight * visibleItems

    val initialIndex = items.indexOf(initialValue).coerceAtLeast(0)
    val listState = remember(initialIndex) {
        LazyListState(firstVisibleItemIndex = initialIndex)
    }

    var lastSnappedIndex by remember { mutableStateOf(-1) }
    val coroutineScope = rememberCoroutineScope()

    var itemList by remember { mutableStateOf(items.toMutableList()) }

    // Dialog state
    var showDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return@LaunchedEffect

            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
            val centerItem = visibleItemsInfo.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }

            centerItem?.let {
                if (it.index != lastSnappedIndex) {
                    lastSnappedIndex = it.index
                    onItemSelected(itemList[it.index])
                }
                coroutineScope.launch {
                    listState.animateScrollToItem(it.index)
                }
            }
        }
    }

    // Main wheel picker UI
    Box(modifier = modifier.height(pickerHeight)) {
        // Center highlight
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.large
                )
                .padding(horizontal = 16.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = (pickerHeight - itemHeight) / 2)
        ) {
            items(itemList.size) { index ->
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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

        // Fade overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.surface, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                    )
                )
        )
    }

    // Long-click input dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Change Value") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("New Value") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editIndex in itemList.indices) {
                        itemList[editIndex] = inputText
                        showDialog = false
                        coroutineScope.launch {
                            listState.animateScrollToItem(editIndex)
                        }
                        onItemSelected(inputText)
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
