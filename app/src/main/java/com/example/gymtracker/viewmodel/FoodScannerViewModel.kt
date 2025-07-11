package com.example.gymtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.model.Product
import com.example.gymtracker.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


// Represents the state of our UI
sealed interface FoodScannerUiState {
    object Idle : FoodScannerUiState
    object Loading : FoodScannerUiState
    data class Success(val product: Product) : FoodScannerUiState
    data class Error(val message: String) : FoodScannerUiState
}

class FoodScannerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FoodScannerUiState>(FoodScannerUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun getProductByBarcode(barcode: String) {
        viewModelScope.launch {
            _uiState.value = FoodScannerUiState.Loading
            try {
                val response = RetrofitClient.instance.getProductByBarcode(barcode)
                if (response.status == 1 && response.product != null) {
                    _uiState.value = FoodScannerUiState.Success(response.product)
                } else {
                    _uiState.value = FoodScannerUiState.Error("Product not found.")
                }
            } catch (e: Exception) {
                _uiState.value = FoodScannerUiState.Error("Product not found./Network error: ${e.message}")
            }
        }
    }
}