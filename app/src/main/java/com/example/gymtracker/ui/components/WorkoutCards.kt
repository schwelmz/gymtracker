import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gymtracker.R
import com.example.gymtracker.data.Exercise
import com.example.gymtracker.data.WorkoutSession

val cardHeight = 95.dp

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Exercise") },
            text = { Text("Are you sure you want to delete this exercise?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(cardHeight)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDialog = true
                    }
                )
            }
    ) {
        Row {
            val imageWidth = 95.dp
            if (!exercise.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = exercise.imageUri, // Coil handles the loading
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            } else if (exercise.imageResId != null) {
                Image(
                    painter = painterResource(id = exercise.imageResId),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.outline_picture_in_picture_center_24),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(cardHeight)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutSessionCard(
    session: WorkoutSession,
    details: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onModify: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Workout Options") },
            text = { Text("What would you like to do with this workout?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onDelete()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    onModify()
                }) {
                    Text("Modify Date")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDialog = true
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.exerciseName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}