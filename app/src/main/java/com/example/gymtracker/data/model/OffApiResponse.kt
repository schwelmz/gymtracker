package com.example.gymtracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Represents the entire API response
@Serializable
data class OffApiResponse(
    val status: Int,
    @SerialName("product") val product: Product?
)
