package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun SettingsHostScreen(mainNavController: NavHostController) {
    val settingsRailNavController = rememberNavController()

    val settingsNavItems = listOf(
        RailNavItem(id = "settings", title = "Settings", route = AppRoutes.SETTINGS_SCREEN),
        RailNavItem(id = "about", title = "About", route = AppRoutes.ABOUT_SCREEN)
    )

    Row(modifier = Modifier.fillMaxSize()) {
        AppNavigationRail(
            items = settingsNavItems,
            selectedItemId = settingsRailNavController.currentDestination?.route ?: AppRoutes.SETTINGS_SCREEN,
            onItemSelected = { route -> settingsRailNavController.navigate(route) }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsNavHost(navController = settingsRailNavController, mainNavController = mainNavController)
        }
    }
}

@Composable
fun SettingsNavHost(navController: NavHostController, mainNavController: NavHostController) {
    NavHost(navController = navController, startDestination = AppRoutes.SETTINGS_SCREEN) {
        composable(route = AppRoutes.SETTINGS_SCREEN) {
            val uriHandler = LocalUriHandler.current
            val donationUrl = "https://www.buymeacoffee.com/your-username"

            SettingsScreen(
                onNavigateToAbout = {
                    navController.navigate(AppRoutes.ABOUT_SCREEN)
                },
                onNavigateToDonate = {
                    uriHandler.openUri(donationUrl)
                }
            )
        }
        composable(route = AppRoutes.ABOUT_SCREEN) {
            AboutScreen(
                onNavigateUp = {
                    navController.popBackStack()
                }
            )
        }
    }
}