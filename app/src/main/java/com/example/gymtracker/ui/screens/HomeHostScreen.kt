package com.example.gymtracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.example.gymtracker.ui.AppRoutes
import com.example.gymtracker.ui.components.AppNavigationRail
import com.example.gymtracker.ui.components.RailNavItem
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.viewmodel.HomeViewModel
import com.example.gymtracker.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeHostScreen(mainNavController: NavHostController, onGrantPermissionsClick: () -> Unit) {
    val homeNavItems = listOf(
        RailNavItem(id = "overview", title = "Overview", route = AppRoutes.HOME_SCREEN)
    )

    val pagerState = rememberPagerState { homeNavItems.size }
    val scope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxSize()) {
//        AppNavigationRail(
//            items = homeNavItems,
//            selectedItemId = homeNavItems[pagerState.currentPage].id,
//            onItemSelected = { route ->
//                val index = homeNavItems.indexOfFirst { it.route == route }
//                if (index != -1) {
//                    scope.launch {
//                        pagerState.animateScrollToPage(index)
//                    }
//                }
//            }
//        )
        Surface(modifier = Modifier.fillMaxSize()) {
            VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (homeNavItems[page].route) {
                    AppRoutes.HOME_SCREEN -> {
                        val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
                        val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory)

                        HomeScreen(
                            homeViewModel = homeViewModel,
                            foodViewModel = foodViewModel,
                            onGrantPermissionsClick = onGrantPermissionsClick
                        )
                    }
                    else -> {
                        Text("Unknown Home Screen")
                    }
                }
            }
        }
    }
}