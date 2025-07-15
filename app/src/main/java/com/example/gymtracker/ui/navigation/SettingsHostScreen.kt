package com.example.gymtracker.ui.navigation

import com.example.gymtracker.ui.screens.settings.SettingsScreen
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.screens.settings.AboutScreen
import com.example.gymtracker.ui.screens.settings.SettingsSetGoalsScreen
import com.example.gymtracker.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsHostScreen(mainNavController: NavHostController) {
    //val settingsRailNavController = rememberNavController()

    val settingsNavItems = listOf(
        RailNavItem(id = "settings", title = "App", route = AppRoutes.SETTINGS_SCREEN),
        RailNavItem(id = "about", title = "Goals", route = AppRoutes.SETTINGS_SET_GOALS_SCREEN)
    )

    val pagerState = rememberPagerState { settingsNavItems.size }
    val scope = rememberCoroutineScope()

    // Synchronize pager state with rail selection
    LaunchedEffect(pagerState.currentPage) {
    }

    Row(modifier = Modifier.fillMaxSize()) {
//        AppNavigationRail(
//            items = settingsNavItems,
//            selectedItemId = settingsRailNavController.currentDestination?.route ?: AppRoutes.SETTINGS_SCREEN,
//            onItemSelected = { route -> settingsRailNavController.navigate(route) }
//        )
        AppNavigationRail(
            items = settingsNavItems,
            selectedItemId = settingsNavItems[pagerState.currentPage].id, // Use id for selection
            onItemSelected = { route ->
                val index = settingsNavItems.indexOfFirst { it.route == route }
                if (index != -1) {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val uriHandler = LocalUriHandler.current
                val donationUrl = "https://www.buymeacoffee.com/your-username"
                val settingsViewModel: SettingsViewModel = viewModel()
                val currentLanguage by settingsViewModel.language.collectAsState()

                when (settingsNavItems[page].route) {
                    AppRoutes.SETTINGS_SCREEN -> {
                        SettingsScreen(
                            onNavigateToAbout = {
                                mainNavController.navigate(AppRoutes.ABOUT_SCREEN)
                            },
                            onNavigateToDonate = {
                                uriHandler.openUri(donationUrl)
                            },
                            currentLanguage = currentLanguage,
                            onLanguageSelected = { settingsViewModel.setLanguage(it) }
                        )
                    }
                    AppRoutes.SETTINGS_SET_GOALS_SCREEN -> {
                        SettingsSetGoalsScreen(

                        )
                    }
                }
            }
        }
    }
}

//@Composable
//fun SettingsNavHost(navController: NavHostController, mainNavController: NavHostController) {
//    NavHost(navController = navController, startDestination = AppRoutes.SETTINGS_SCREEN) {
//        composable(route = AppRoutes.SETTINGS_SCREEN) {
//            val uriHandler = LocalUriHandler.current
//            val donationUrl = "https://www.buymeacoffee.com/your-username"
//            val settingsViewModel: SettingsViewModel = viewModel()
//            val currentLanguage by settingsViewModel.language.collectAsState()
//            SettingsScreen(
//                onNavigateToAbout = {
//                    navController.navigate(AppRoutes.ABOUT_SCREEN)
//                },
//                onNavigateToDonate = {
//                    uriHandler.openUri(donationUrl)
//                },
//                currentLanguage = currentLanguage,
//                onLanguageSelected = { settingsViewModel.setLanguage(it) }
//            )
//        }
//        composable(route = AppRoutes.ABOUT_SCREEN) {
//            AboutScreen(
//                onNavigateUp = {
//                    navController.popBackStack()
//                }
//            )
//        }
//    }
//}