package com.example.gymtracker.ui.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.TodayHealthStats
import com.example.gymtracker.ui.theme.AppTheme // Import your app's theme for accurate previews
import com.example.gymtracker.viewmodel.HomeUiState
import com.example.gymtracker.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onGrantPermissionsClick: () -> Unit
) {
    // ... (Your existing HomeScreen code remains unchanged)
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.checkAvailabilityAndPermissions()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Today's Activity",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            when (val state = uiState) {
                is HomeUiState.Idle -> {
                    CircularProgressIndicator()
                }
                is HomeUiState.HealthConnectNotInstalled -> {
                    PermissionCard(
                        title = "Health Connect Not Installed",
                        description = "To view your health stats, please install the Health Connect app.",
                        buttonText = "Install",
                        onButtonClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
                                setPackage("com.android.vending")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
                is HomeUiState.PermissionsNotGranted -> {
                    PermissionCard(
                        title = "Permissions Required",
                        description = "This app needs permission to read your health data. Tap below to grant access.",
                        buttonText = "Grant Permissions",
                        onButtonClick = onGrantPermissionsClick
                    )
                }
                is HomeUiState.Success -> {
                    HealthStatsGrid(stats = state.stats)
                }
            }
        }
    }
}

// ... (Your existing HealthStatsGrid, StatCard, and PermissionCard code remains unchanged)

// A grid to display the fetched health stats
@Composable
fun HealthStatsGrid(stats: TodayHealthStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                icon = Icons.Default.Face,
                label = "Steps",
                value = stats.steps.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Info,
                label = "Calories",
                value = "%.0f kcal".format(stats.caloriesBurned),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                icon = Icons.Default.FavoriteBorder, // Example icon
                label = "Distance",
                value = "%.2f km".format(stats.distanceMeters / 1000),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Reusable card for a single stat
@Composable
fun StatCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Reusable card for asking for permissions or installation
@Composable
fun PermissionCard(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}


//=================================================================//
//                          PREVIEWS                               //
//=================================================================//

@Preview(showBackground = true)
@Composable
fun PermissionCardPreview() {
    AppTheme {
        PermissionCard(
            title = "Permissions Required",
            description = "This app needs permission to read your health data. Tap below to grant access.",
            buttonText = "Grant Permissions",
            onButtonClick = {} // In a preview, the click action does nothing.
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HealthStatsGridPreview() {
    AppTheme {
        HealthStatsGrid(
            stats = TodayHealthStats(
                steps = 10456,
                distanceMeters = 8364.8,
                caloriesBurned = 418.0
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun HomeScreen_Success_Preview() {
    // This is a fake ViewModel that always returns a success state for the preview
    val fakeViewModel = object : HomeViewModel(Application()) {
        override val uiState = MutableStateFlow<HomeUiState>(
            HomeUiState.Success(
                TodayHealthStats(
                    steps = 10456,
                    distanceMeters = 8364.8,
                    caloriesBurned = 418.0
                )
            )
        )
    }
    AppTheme {
        HomeScreen(homeViewModel = fakeViewModel, onGrantPermissionsClick = {})
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun HomeScreen_PermissionsNotGranted_Preview() {
    val fakeViewModel = object : HomeViewModel(Application()) {
        override val uiState = MutableStateFlow<HomeUiState>(HomeUiState.PermissionsNotGranted)
    }
    AppTheme {
        HomeScreen(homeViewModel = fakeViewModel, onGrantPermissionsClick = {})
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun HomeScreen_NotInstalled_Preview() {
    val fakeViewModel = object : HomeViewModel(Application()) {
        override val uiState = MutableStateFlow<HomeUiState>(HomeUiState.HealthConnectNotInstalled)
    }
    AppTheme {
        HomeScreen(homeViewModel = fakeViewModel, onGrantPermissionsClick = {})
    }
}