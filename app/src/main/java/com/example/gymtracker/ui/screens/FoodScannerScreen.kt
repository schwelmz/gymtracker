package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymtracker.data.FoodTemplate
import com.example.gymtracker.data.model.Product
import com.example.gymtracker.ui.components.BarcodeScannerView
import com.example.gymtracker.viewmodel.FoodScannerUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.gymtracker.viewmodel.FoodScannerViewModel
import com.example.gymtracker.viewmodel.FoodViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FoodScannerScreen(
    scannerViewModel: FoodScannerViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel(),
    // 1. ACCEPT THE ARGUMENT FROM THE NAVIGATION GRAPH
    shouldOpenCameraDirectly: Boolean,
    onSave: () -> Unit
) {
    val uiState by scannerViewModel.uiState.collectAsState()
    var showScanner by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // --- 2. USE LaunchedEffect TO OPEN THE SCANNER ON SCREEN ENTRY ---
    // This will only run once when the screen is first composed.
    LaunchedEffect(Unit) {
        if (shouldOpenCameraDirectly) {
            if (cameraPermissionState.status.isGranted) {
                showScanner = true
            } else {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    // This effect will run AFTER the permission is granted or denied
    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted && shouldOpenCameraDirectly) {
            showScanner = true
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is FoodScannerUiState.Idle -> {
                    Text("Scan a product to see its details.")
                }
                is FoodScannerUiState.Loading -> {
                    CircularProgressIndicator()
                }
//                is FoodScannerUiState.Success -> {
//                    ProductDetails(
//                        product = state.product,
//                        onAddFood = { grams ->
//                            foodViewModel.addScannedFood(state.product, grams)
//                            onSave()
//                        }
//                    )
//                }
                is FoodScannerUiState.Success -> {
                    // Keep a remembered state for the FoodTemplate
                    var template by remember { mutableStateOf<FoodTemplate?>(null) }

                    // Automatically create the FoodTemplate only once
                    LaunchedEffect(state.product) {
                        val createdTemplate = foodViewModel.getOrCreateTemplateFromProduct(state.product)
                        template = createdTemplate
                    }

                    // Only show UI once template is ready
                    if (template != null) {
                        ProductDetails(
                            product = state.product,
                            onAddFood = { grams, name ->
                                foodViewModel.addScannedFoodWithCustomName(state.product, grams, name)
                                onSave()
                            }
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
                is FoodScannerUiState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // 3. This button is still here for manual scanning if the user
        // arrives on this screen without the 'open_camera' flag.
        Button(
            onClick = {
                if (cameraPermissionState.status.isGranted) {
                    showScanner = true
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Scan Barcode")
        }

        if (showScanner) {
            BarcodeScannerView(onBarcodeScanned = { barcode ->
                showScanner = false
                scannerViewModel.getProductByBarcode(barcode)
            })
        }
    }
}


@Composable
fun ProductDetails(product: Product, onAddFood: (Int, String) -> Unit) {
    var grams by remember { mutableStateOf("100") }

    val defaultName = remember(product) {
        if (!product.name.isNullOrBlank()) {
            product.name.split("–", "-").firstOrNull()?.trim() ?: product.name
        } else if (!product.genericName.isNullOrBlank()) {
            product.genericName
        } else {
            "Unknown Product"
        }
    }

    var customName by remember { mutableStateOf("") }
    val isUnknown = defaultName == "Unknown Product"
    val displayName = if (isUnknown) customName else defaultName

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = displayName,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isUnknown) {
            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text("Enter Product Name") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        product.brands?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        product.nutriments?.let {
            Text("Energy (per 100g): ${it.energyKcalPer100g} kcal")
            Text("Carbohydrates (per 100g): ${it.carbohydratesPer100g} g")
            Text("Sugars (per 100g): ${it.sugarsPer100g} g")
            Text("Salt (per 100g): ${it.saltPer100g} g")
            Text("Proteins (per 100g): ${it.proteinsPer100g} g")
            Text("Fat (per 100g): ${it.fatPer100g} g")
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = grams,
            onValueChange = { grams = it.filter { c -> c.isDigit() } },
            label = { Text("Grams") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val gramsInt = grams.toIntOrNull() ?: 100
                if (!isUnknown || customName.isNotBlank()) {
                    onAddFood(gramsInt, displayName)
                }
            },
            enabled = !isUnknown || customName.isNotBlank()
        ) {
            Text("Add Food")
        }
    }
}
