package com.example.gymtracker.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding

@Composable
fun SettingsSetGoalsScreen() {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = headlineTopPadding,
                            bottom = headlineBottomPadding,
                            end = 16.dp
                        ),
                    contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
                ) {
                    Text(
                        text = "Goal Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary
                        // textAlign can be removed if the Box handles the alignment
                    )
                }
            }
            item {
                Box(
                ) {
                    Text(text = "Goals")
                }
            }
        }
    }
}