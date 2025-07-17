package com.example.gymtracker.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.repository.UserPreferencesRepository
import com.example.gymtracker.ui.utils.headlineBottomPadding
import com.example.gymtracker.ui.utils.headlineTopPadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateToDonate: () -> Unit,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("English", "Deutsch", "Español")
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefsRepo = remember { UserPreferencesRepository(context) }



    Scaffold{ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = headlineTopPadding,
                            bottom = headlineBottomPadding,
                            end = 16.dp
                        ),
                    contentAlignment = Alignment.CenterEnd // Aligns content to the end (right)
                ) {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary
                        // textAlign can be removed if the Box handles the alignment
                    )
                }
            }
            item {
                ListItem(
                    headlineContent = { Text("About") },
                    leadingContent = {
                        Icon(Icons.Filled.Info, contentDescription = "About")
                    },
                    modifier = Modifier.clickable(onClick = onNavigateToAbout)
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Reset Health Permissions Prompt") },
                    supportingContent = { Text("Click this prompt and you'll be asked again to grant health data access on the home screen.") },
                    modifier = Modifier.clickable {
                        scope.launch {
                            userPrefsRepo.setHealthPermissionsDeclined(false)
                        }
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Support the App") },
                    leadingContent = {
                        Icon(Icons.Filled.Favorite, contentDescription = "Donate")
                    },
                    modifier = Modifier.clickable(onClick = onNavigateToDonate)
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("Language: $currentLanguage") },
                    leadingContent = {
                        Icon(Icons.Filled.Info, contentDescription = "Language")
                    },
                    modifier = Modifier.clickable { showDialog = true }
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {},
                title = { Text("Choose Language") },
                text = {
                    Column {
                        languages.forEach { language ->
                            Text(
                                text = language,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        onLanguageSelected(language)
                                        showDialog = false
                                    }
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsScreen(
        onNavigateToAbout = {},
        onNavigateToDonate = {},
        currentLanguage = "English",
        onLanguageSelected = {}
    )

}