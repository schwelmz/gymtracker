package com.example.gymtracker.ui.screens.nutrition

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gymtracker.R
import com.example.gymtracker.data.model.FoodTemplate
import com.example.gymtracker.viewmodel.AddEditRecipeViewModel
import com.example.gymtracker.viewmodel.RecipeViewModel
import com.example.gymtracker.viewmodel.ScannerResultViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: Int,
    // The main RecipeViewModel is still needed for the food template list
    recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory),
    // The new ViewModel for this screen's state
    addEditRecipeViewModel: AddEditRecipeViewModel = viewModel(factory = AddEditRecipeViewModel.Factory(recipeId)),
    scannerResultViewModel: ScannerResultViewModel = viewModel(),
    onNavigateUp: () -> Unit,
    onNavigateToScanner: () -> Unit
) {
    val isEditing = recipeId != -1
    val title = if (isEditing) "Edit Recipe" else "Add Recipe"

    // State for the food template search dialog
    val allFoodTemplates by recipeViewModel.allFoodTemplates.collectAsState(initial = emptyList())
    var showAddIngredientDialog by remember { mutableStateOf(false) }

    // Observe the result from the scanner and pass it to the ViewModel
    val scannedIngredient by scannerResultViewModel.scannedIngredient.collectAsState()
    LaunchedEffect(scannedIngredient) {
        scannedIngredient?.let { (template, grams) ->
            addEditRecipeViewModel.addIngredient(template, grams)
            showAddIngredientDialog = false // Close the dialog after scanning
            scannerResultViewModel.consumeScannedIngredient() // Clear the result
        }
    }

    // The save button is enabled only if the recipe has a name and at least one ingredient.
    val isSaveEnabled by remember(addEditRecipeViewModel.recipeName, addEditRecipeViewModel.recipeIngredients) {
        derivedStateOf {
            addEditRecipeViewModel.recipeName.isNotBlank() && addEditRecipeViewModel.recipeIngredients.isNotEmpty()
        }
    }

    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val imageUri = addEditRecipeViewModel.recipeImageUri?.let { Uri.parse(it) }

    // Permission state handler for the camera
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    // Launcher for the camera activity
    val cameraActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            // If the picture was taken successfully, the URI we provided will be populated.
            if (success) {
                addEditRecipeViewModel.recipeImageUri = tempUri.toString()
            }
        }
    )
    val context = LocalContext.current

    if (showAddIngredientDialog) {
        AddIngredientDialog(
            allFoodTemplates = allFoodTemplates,
            onDismiss = { showAddIngredientDialog = false },
            onIngredientSelected = { foodTemplate, grams ->
                addEditRecipeViewModel.addIngredient(foodTemplate, grams)
                showAddIngredientDialog = false
            },
            onNavigateToScanner = onNavigateToScanner
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = {
                            addEditRecipeViewModel.saveRecipe()
                            onNavigateUp()
                        },
                        enabled = isSaveEnabled,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f) // Common aspect ratio for images
                        .border(1.dp, MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.Center
                ) {
                    // Show the image if we have a URI, otherwise show text
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Recipe Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("No Image Taken")
                    }
                }

                OutlinedButton(
                    onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            // Create a file and URI for the camera to save the image to
                            val uri = createImageUri(context)
                            tempUri = uri // Update our state
                            cameraActivityLauncher.launch(uri)
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
            item {
                OutlinedTextField(
                    value = addEditRecipeViewModel.recipeName,
                    onValueChange = { addEditRecipeViewModel.recipeName = it },
                    label = { Text("Recipe Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            item {
                OutlinedTextField(
                    value = addEditRecipeViewModel.recipeInstructions,
                    onValueChange = { addEditRecipeViewModel.recipeInstructions = it },
                    label = { Text("Instructions (Optional)") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ingredients", style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(onClick = { showAddIngredientDialog = true }) {
                        Text("Add")
                    }
                }
            }

            if (addEditRecipeViewModel.recipeIngredients.isEmpty()){
                item {
                    Text(
                        text = "No ingredients added yet.",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            items(addEditRecipeViewModel.recipeIngredients.toList(), key = { (template, _) -> template.id }) { (template, grams) ->
                IngredientListItem(
                    template = template,
                    grams = grams,
                    onRemove = { addEditRecipeViewModel.removeIngredient(template) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun IngredientListItem(template: FoodTemplate, grams: Int, onRemove: () -> Unit) {
    val calories = (template.caloriesPer100g * grams) / 100
    ListItem(
        headlineContent = { Text(template.name, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text("$grams g") },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$calories kcal", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove ingredient")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientDialog(
    allFoodTemplates: List<FoodTemplate>,
    onDismiss: () -> Unit,
    onIngredientSelected: (FoodTemplate, Int) -> Unit,
    onNavigateToScanner: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<FoodTemplate?>(null) }
    var grams by remember { mutableStateOf("") }

    val filteredTemplates = remember(searchText, allFoodTemplates) {
        if (searchText.length >= 1) {
            allFoodTemplates.filter {
                it.name.contains(searchText, ignoreCase = true)
            }.take(4)
        } else {
            emptyList()
        }
    }

    val isConfirmEnabled = selectedTemplate != null && grams.isNotBlank() && grams.toIntOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Ingredient") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Row for Search Bar and Scan Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Bar with Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = {
                                    searchText = it
                                    expanded = true
                                },
                                label = { Text("Search food") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    if (searchText.isNotEmpty()) {
                                        IconButton(onClick = { searchText = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear text"
                                            )
                                        }
                                    } else {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    }
                                }

                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                filteredTemplates.forEach { template ->
                                    DropdownMenuItem(
                                        text = { Text(template.name) },
                                        onClick = {
                                            selectedTemplate = template
                                            searchText = template.name
                                            expanded = false
                                        }
                                    )
                                }
                                if (filteredTemplates.isEmpty() && searchText.length >= 3) {
                                    DropdownMenuItem(
                                        text = { Text("No results found") },
                                        onClick = { },
                                        enabled = false
                                    )
                                }
                            }
                        }
                    }
                    // Scan Button
                    IconButton(onClick = onNavigateToScanner) {
                        Icon(
                            painter = painterResource(id = R.drawable.ean_icon),
                            contentDescription = "Scan Food Item",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Weight Input Field
                OutlinedTextField(
                    value = grams,
                    onValueChange = { grams = it.filter { char -> char.isDigit() } },
                    label = { Text("Weight (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedTemplate?.let { template ->
                        grams.toIntOrNull()?.let { weight ->
                            onIngredientSelected(template, weight)
                        }
                    }
                },
                enabled = isConfirmEnabled
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
                Text("Cancel")
            }
        }
    )
}

private fun createImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images")
    imagesDir.mkdirs()
    val file = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(context, authority, file)
}