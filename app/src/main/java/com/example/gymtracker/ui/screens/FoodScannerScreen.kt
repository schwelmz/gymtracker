package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gymtracker.data.model.Product
import com.example.gymtracker.ui.components.BarcodeScannerView
import com.example.gymtracker.viewmodel.FoodScannerUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.gymtracker.viewmodel.FoodScannerViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FoodScannerScreen(
    // The viewModel is automatically provided by the framework.
    viewModel: FoodScannerViewModel = viewModel()
) {
    // Collect the current state from the ViewModel. The UI will automatically
    // update whenever this state changes.
    val uiState by viewModel.uiState.collectAsState()

    // State to decide when to show the full-screen camera view.
    var showScanner by remember { mutableStateOf(false) }

    // State for handling the camera permission request.
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (cameraPermissionState.status.isGranted) {
                // If permission is granted, show the scanner.
                showScanner = true
            } else {
                // Otherwise, request the permission.
                cameraPermissionState.launchPermissionRequest()
            }
        }) {
            Text("Scan Barcode")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // This 'when' block reacts to the state from the ViewModel.
        when (val state = uiState) {
            is FoodScannerUiState.Idle -> {
                Text("Scan a product to see its details.")
            }
            is FoodScannerUiState.Loading -> {
                CircularProgressIndicator()
            }
            is FoodScannerUiState.Success -> {
                // If data is fetched successfully, show the ProductDetails component.
                ProductDetails(product = state.product)
            }
            is FoodScannerUiState.Error -> {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }

        // If showScanner is true, display the camera scanner view.
        if (showScanner) {
            BarcodeScannerView(onBarcodeScanned = { barcode ->
                showScanner = false // Hide scanner after getting a result
                viewModel.getProductByBarcode(barcode) // Trigger the API call
            })
        }
    }
}

/**
 * A simple, reusable composable to display the details of a Product.
 */
@Composable
fun ProductDetails(product: Product) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(product.name ?: "N/A", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(product.brands ?: "N/A", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        product.nutriments?.let {
            Text("Energy (per 100g): ${it.energyKcalPer100g} kcal")
            Text("Sugars (per 100g): ${it.sugarsPer100g} g")
            Text("Salt (per 100g): ${it.saltPer100g} g")
            Text("Proteins (per 100g): ${it.proteinsPer100g} g")
            Text("Fat (per 100g): ${it.fatPer100g} g")
        }
    }
}