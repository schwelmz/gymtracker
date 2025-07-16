package com.example.gymtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymtracker.data.AppDatabase
import com.example.gymtracker.data.model.Product
import com.example.gymtracker.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FoodScannerUiState {
    object Idle : FoodScannerUiState
    object Loading : FoodScannerUiState
    data class Success(val product: Product, val barcode: String) : FoodScannerUiState
    data class Error(val message: String) : FoodScannerUiState
}

// 1. Change to AndroidViewModel to get application context for the database
class FoodScannerViewModel(application: Application) : AndroidViewModel(application) {

    // 2. Get an instance of the FoodTemplateDao
    private val foodTemplateDao = AppDatabase.getDatabase(application).foodTemplateDao()

    private val _uiState = MutableStateFlow<FoodScannerUiState>(FoodScannerUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun getProductByBarcode(barcode: String) {
        viewModelScope.launch {
            _uiState.value = FoodScannerUiState.Loading
            try {
                val response = RetrofitClient.instance.getProductByBarcode(barcode)
                if (response.status == 1 && response.product != null) {
                    var finalProduct = response.product

                    // 3. Implement the new logic
                    val apiName = findBestName(finalProduct)
                    if (apiName == "Unknown Product") {
                        // If API name is bad, check our local database
                        val localTemplate = foodTemplateDao.getByBarcode(barcode)
                        if (localTemplate != null && !localTemplate.name.isNullOrBlank()) {
                            // A better name exists locally! Let's use it.
                            // We create a copy of the API product but override the name.
                            finalProduct = finalProduct.copy(name = localTemplate.name)
                        }
                    }

                    // 4. Set the success state with the potentially corrected product
                    _uiState.value = FoodScannerUiState.Success(finalProduct, barcode)

                } else {
                    _uiState.value = FoodScannerUiState.Error("Product not found.")
                }
            } catch (e: Exception) {
                _uiState.value = FoodScannerUiState.Error("Network error: ${e.message}")
            }
        }
    }

    // This helper function can be copied from your FoodViewModel or made a utility function
    private fun findBestName(product: Product): String {
        if (!product.name.isNullOrBlank()) {
            return product.name.split("–", "-").firstOrNull()?.trim() ?: product.name
        }
        if (!product.genericName.isNullOrBlank()) {
            return product.genericName
        }
        return "Unknown Product"
    }

    fun resetState() {
        _uiState.value = FoodScannerUiState.Idle
    }

    // 5. Create a Factory for the AndroidViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return FoodScannerViewModel(application) as T
            }
        }
    }
}