package com.example.gymtracker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.data.ExerciseRepository
import com.example.gymtracker.ui.theme.GymTrackerTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AddWorkoutScreen(onExerciseSelected: (exerciseName: String, imageUri: String?) -> Unit) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Workout Image")
        }

        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Selected Workout Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Choose an Exercise", style = MaterialTheme.typography.titleLarge)
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(ExerciseRepository.getAvailableExercises()) { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            onExerciseSelected(exercise.name, selectedImageUri?.toString())
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                        Text(exercise.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddWorkoutScreenPreview() {
    GymTrackerTheme {
        AddWorkoutScreen(onExerciseSelected = { _, _ -> })
    }
}