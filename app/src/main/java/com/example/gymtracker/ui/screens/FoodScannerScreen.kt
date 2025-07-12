package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.gymtracker.viewmodel.FoodViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FoodScannerScreen(
    scannerViewModel: FoodScannerViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel(),
    onSave: () -> Unit
) {
    val uiState by scannerViewModel.uiState.collectAsState()
    var showScanner by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val name by remember { mutableStateOf("") }
    val calories by remember { mutableIntStateOf(0) }

    val context = LocalContext.current

    // A Box is used to allow for flexible placement of its children, like aligning
    // one element to the center and another to the bottom.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // This Column holds the main content and is aligned to the center of the screen.
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
                is FoodScannerUiState.Success -> {
                    ProductDetails(product = state.product)
                }
                is FoodScannerUiState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // This button is aligned to the bottom center of the Box, pushing it to the footer.
        Button(
            onClick = {
                if (cameraPermissionState.status.isGranted) {
                    showScanner = true
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            // The align modifier positions this specific composable within the parent Box.
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Scan Barcode")
        }

        Button(
            onClick = {
                onSave()
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        ){
            Text("Add Food")
        }

        if (showScanner) {
            BarcodeScannerView(onBarcodeScanned = { barcode ->
                showScanner = false
                scannerViewModel.getProductByBarcode(barcode)
            })
        }
    }
}

/**
 * A simple, reusable composable to display the details of a Product.
 * (This composable does not need any changes)
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