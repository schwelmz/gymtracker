// In ui/screens/AddExerciseScreen.kt
package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    onSave: (name: String, description: String) -> Unit,
    onNavigateUp: () -> Unit // Action to go back
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Exercise") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        // You'll need to add the dependency for extended icons for this
                        // implementation("androidx.compose.material:material-icons-extended")
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Exercise Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onSave(name, description) },
                enabled = name.isNotBlank(), // Can only save if name is not empty
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Exercise")
            }
        }
    }
}