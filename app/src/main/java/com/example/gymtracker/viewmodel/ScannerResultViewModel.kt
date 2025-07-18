package com.example.gymtracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymtracker.data.model.FoodTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A shared ViewModel to communicate the result of the food scanner back to the
 * calling screen (e.g., AddEditRecipeScreen).
 *
 * To make this work as a shared ViewModel, ensure it's scoped correctly,
 * for example, to the navigation graph using Hilt:
 *
 * In your navigation graph:
 * val scannerResultViewModel: ScannerResultViewModel = hiltViewModel(navController.getBackStackEntry("your_recipe_route_or_graph"))
 *
 * Or provide it at the Activity level if that fits your architecture.
 */
class ScannerResultViewModel : ViewModel() {

    private val _scannedIngredient = MutableStateFlow<Pair<FoodTemplate, Float>?>(null)
    val scannedIngredient = _scannedIngredient.asStateFlow()

    fun setScannedIngredient(template: FoodTemplate, grams: Float) {
        _scannedIngredient.value = template to grams
    }

    fun consumeScannedIngredient() {
        _scannedIngredient.value = null
    }
}
