package com.example.gymtracker.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("About") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "About"
                        )
                    },
                    modifier = Modifier.clickable(onClick = onNavigateToAbout)
                )
            }
            // You can add more settings items here in the future
        }
    }
}