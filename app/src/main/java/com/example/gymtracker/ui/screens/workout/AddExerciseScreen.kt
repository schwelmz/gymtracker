package com.example.gymtracker.ui.screens.workout

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddExerciseScreen(
    onSave: (name: String, description: String, imageUri: String?) -> Unit,
    onNavigateUp: () -> Unit // Action to go back
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // State to hold the URI of the image after it's taken
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Launcher for the camera activity
    val cameraActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            // If the picture was taken successfully, the URI we provided will be populated.
            if (success) {
                // The 'imageUri' state is already updated because we passed it to the launcher.
                imageUri = tempUri
            }
        }
    )

    // Permission state handler for the camera
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Exercise") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
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
            ImagePreviewSection(imageUri = imageUri)

            // Camera permission button
            CameraPermissionButton(
                cameraPermissionState = cameraPermissionState,
                onTakePicture = { uri ->
                    tempUri = uri // Update state
                    cameraActivityLauncher.launch(uri)
                }
            )

            // Text fields for name and description
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
                onClick = { onSave(name, description, imageUri?.toString()) },
                enabled = name.isNotBlank(), // Can only save if name is not empty
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Exercise")
            }
        }
    }
}

// Image preview section
@Composable
fun ImagePreviewSection(imageUri: Uri?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f) // Common aspect ratio for images
            .border(1.dp, MaterialTheme.colorScheme.outline),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Exercise Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("No Image Taken")
        }
    }
}

// Camera permission button with logic
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionButton(
    cameraPermissionState: PermissionState,
    onTakePicture: (Uri) -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (cameraPermissionState.status.isGranted) {
                // Create a file and URI for the camera to save the image to
                val uri = createImageUri(context)
                onTakePicture(uri)
            } else {
                // Request the permission
                cameraPermissionState.launchPermissionRequest()
            }
        },
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Text(if (cameraPermissionState.status.isGranted) "Take Picture" else "Request Camera Permission")
    }
}

// Helper function to create a temporary file and its URI
private fun createImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images")
    imagesDir.mkdirs()
    val file = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(context, authority, file)
}
