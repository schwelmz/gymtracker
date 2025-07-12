package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WorkoutCalendarDayScreen () {
    LazyColumn(modifier = Modifier.fillMaxSize()){
        item {
            Text(text = "Calendar Day Screen")
        }
    }
}